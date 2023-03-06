package com.tgervai.album;

import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class Checker {

    final FilesDB files_db;
    FilesDB.index type;

    public List<Object> find(FileData src) {
        List<Object> list = files_db.get(src, type);
        return (list == null || list.size() == 0) ? null : list;
    }

}
