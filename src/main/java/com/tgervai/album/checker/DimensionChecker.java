package com.tgervai.album.checker;

import com.tgervai.album.file.FilesDB;

import static com.tgervai.album.file.FilesDB.Index.dimType;

public class DimensionChecker extends Checker {

    public DimensionChecker(FilesDB files_db) {
        super(files_db);
        type = dimType;
    }

}

