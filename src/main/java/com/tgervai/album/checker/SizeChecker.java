package com.tgervai.album.checker;

import com.tgervai.album.file.FilesDB;

import static com.tgervai.album.file.FilesDB.Index.sizeType;

public class SizeChecker extends Checker {

    public SizeChecker(FilesDB files_db) {
        super(files_db);
        type  = sizeType;
    }

}
