package com.tgervai.album;

import static com.tgervai.album.FilesDB.index.nameType;

public class NameChecker extends Checker {

    public NameChecker(FilesDB files_db) {
        super(files_db);
        type  = nameType;
    }

}
