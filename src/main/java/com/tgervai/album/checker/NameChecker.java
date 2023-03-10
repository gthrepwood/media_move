package com.tgervai.album.checker;

import com.tgervai.album.file.FilesDB;

import static com.tgervai.album.file.FilesDB.Index.nameType;

public class NameChecker extends Checker {

    public NameChecker(FilesDB files_db) {
        super(files_db);
        type  = nameType;
    }

}
