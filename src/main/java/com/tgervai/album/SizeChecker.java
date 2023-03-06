package com.tgervai.album;

import static com.tgervai.album.FilesDB.index.sizeType;

public class SizeChecker extends Checker {

    public SizeChecker(FilesDB files_db) {
        super(files_db);
        type  = sizeType;
    }

}
