package com.tgervai.album.db;

import com.tgervai.album.config.Config;
import com.tgervai.album.file.FileData;
import com.tgervai.album.utils.FileUtils;
import com.tgervai.album.utils.StoreUtils;
import com.tgervai.album.utils.TimerUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;

import static com.tgervai.album.config.ConfigKeys.*;

@Slf4j
@Service
public class Database implements Serializable {

    boolean db_inited = false;

    @Setter
    @Getter
    SortedSet<FileData> files;
    @Autowired
    FileUtils fileUtils;
    @Autowired
    Config config;
    boolean changed = false;
    @Getter
    private HashMap<Index, HashMap<String, List<FileData>>> indexes;

    @Autowired
    private StoreUtils<SortedSet<FileData>> storeUtils;

    void index() {
        indexes = new HashMap<>();
        for (Index i : Index.values()) {
            indexes.put(i, buildIndex(i));
        }
    }

    synchronized HashMap<String, List<FileData>> buildIndex(Index name) {
        HashMap<String, List<FileData>> idx = new HashMap<>();
        for (FileData file : getFiles()) {
            String key = file.get(name);
            if (key != null) {
                List list = idx.get(key);
                if (list == null) {
                    list = new ArrayList();
                }
                list.add(file);
                idx.put(key, list);
            }
        }

        return idx;
    }


    @Override
    public String toString() {
        return "FilesDB{files=" + (files == null ? "null" : files.size()) + "}";
    }

    public synchronized void load(String filename) {
        setFiles(storeUtils.load(filename + ".gz"));
        if (getFiles() != null) {
            changed = false;
        }
    }

    public synchronized void save() {
        try {
            if (config.isValue(save_object_gz)) {
                storeUtils.save(files, save_object_gz);
            }
            if (config.isValue(save_json)) {
                storeUtils.save(files, save_json);
            }
        } catch (IOException e) {
            log.error("cannot save", e);
        }
    }

    public void clear() {
        if (getFiles() != null) files.clear();
        if (indexes != null) {
            indexes.clear();
        }
        changed = true;
        db_inited = false;
    }

    public List<FileData> get(FileData file, Index i) {
        List list = (indexes.get(i)).get(file.get(i));
        return list == null ? new ArrayList() : list;
    }

    public boolean exists(FileData file, Index i) {
        assert !db_inited : "db not yet initialized";
        return get(file, i).size() > 0;
    }

    public List<Index> exists(FileData file) {
        List<Index> r = new ArrayList<>();
        for (Index i : Index.values()) {
            if (exists(file, i)) {
                r.add(i);
            }
        }
        return r;
    }

    public synchronized void reload() {
        storeUtils.setFilename(config.getValue(data_file));

        if (config.isValue(read_from_filesdb)) {
            try (TimerUtil ignored = new TimerUtil("loading data")) {
                load(config.getValue(data_file));
                if (getFiles() != null)
                    log.debug("loaded size: " + getFiles().size());
            }
        }

        if (getFiles() == null || getFiles().size() == 0) {
            try (TimerUtil ignored = new TimerUtil("walk on files")) {
                clear();
                setFiles(fileUtils.walk(new TreeSet<>(), config.getValue(pictures_path)));
                log.debug("files scanned: " + getFiles().size());
            }

            if (config.isValue(save_data_file)) {
                save();
            }
        }
        index();
        db_inited = true;
    }
}