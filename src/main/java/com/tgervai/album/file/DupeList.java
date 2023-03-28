package com.tgervai.album.file;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
public class DupeList extends ArrayList {
    Set<FileData> all = new HashSet<>();
    public boolean add(List<FileData> dupe) {
        dupe.removeAll(all);
        all.addAll(dupe);
        if (dupe.size() > 0) {
            return super.add(dupe);
        } else {
            log.error("skip dupe");
            return false;
        }
    }
}
