package com.tgervai.album;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;


@Slf4j
@Service
public class FilesDB implements Serializable {

    @Setter
    @Getter
    SortedSet<FileData> files;

    boolean changed = false;
    HashMap<index, HashMap<String, List<FileData>>> indexes;

    @Autowired
    private StoreUtils<SortedSet<FileData>> storeUtils;

    public void index() {
        indexes = new HashMap<>();
        for (index i : index.values()) {
            indexes.put(i, buildIndex(i));
        }
    }

    private HashMap<String, List<FileData>> buildIndex(index name) {
        HashMap<String, List<FileData>> idx = new HashMap<>();
        for (FileData file : files) {
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

    void saveDupes(String basepath) {
        for (index name : index.values()) {
            try {
                String out = basepath + "/dupes_" + name + ".html";
                log.debug("save dupe report: " + out);
                FileWriter p = new FileWriter(out);

                if (indexes.get(name) != null) {

                    for (Map.Entry<String, List<FileData>> entry : indexes.get(name).entrySet()) {
                        String k = entry.getKey();
                        List<FileData> v = entry.getValue();
                        if (v.size() > 1) {
                            p.write(String.format("%s", k));
                            for (FileData y : v) {
                                String path = y.path.substring(basepath.length() + 1);
                                p.write(String.format("<img loading=\"lazy\" src='%s' width='150px' />", y.path, path));
                            }
                            p.write("<br/>");
                        }
                    }
                }
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }

    }

    @Override
    public String toString() {
        return "FilesDB{files=" + (files == null ? "null" : files.size()) + "}";
    }

    public void load(String filename) {
        files = storeUtils.load(filename);
        if (files != null) {
            changed = false;
        }
    }

    public void save() {
        try {
            storeUtils.save(files);
        } catch (IOException e) {
            log.error("canot save", e);
        }
    }

    public void clear() {
        if (files != null) files.clear();
        if (indexes != null) {
            indexes.clear();
        }
        changed = true;
    }

    public List get(FileData file, index i) {
        List list = (indexes.get(i)).get(file.get(i));
        return list == null ? new ArrayList() : list;
    }

    enum index {
        nameType, sizeType, dimType, gpsType
    }
}

