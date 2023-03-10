package com.tgervai.album.checker;

import com.tgervai.album.file.FilesDB;

import static com.tgervai.album.file.FilesDB.Index.gpsType;

public class GPSChecker extends Checker {

    public GPSChecker(FilesDB files_db) {
        super(files_db);
        type = gpsType;
    }


}
