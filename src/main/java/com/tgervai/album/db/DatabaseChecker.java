package com.tgervai.album.db;

import com.tgervai.album.file.DupeList;
import com.tgervai.album.file.FileData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Set;

@Slf4j
@Service
public class DatabaseChecker implements Serializable {

    @Autowired
    Database database;

    public HashMap<String, DupeList> searchDuplicate() {
        HashMap<String, DupeList> r = new HashMap<>();
        for (final Index value : Index.values()) {
            r.put(value.name(), searchDuplicatedByType(value));
        }
        return r;
    }

    public DupeList searchDuplicatedByType(Index indexType) {
        DupeList dupes = new DupeList();
        if (database.getIndexes().get(indexType) != null) {
            HashMap<String, Set<FileData>> db = database.getIndexes().get(indexType);
            for (String entry : db.keySet()) {
                Set<FileData> set = db.get(entry);
                if (set.size() > 1) {
                    dupes.add(set);
                }
            }
        }
        return dupes;
    }
}