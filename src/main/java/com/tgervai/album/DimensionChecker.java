package com.tgervai.album;

import static com.tgervai.album.FilesDB.index.dimType;

public class DimensionChecker extends Checker {

    public DimensionChecker(FilesDB files_db) {
        super(files_db);
        type = dimType;
    }

}

