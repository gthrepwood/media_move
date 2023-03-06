package com.tgervai.album;

import static com.tgervai.album.FilesDB.index.gpsType;

public class GPSChecker extends Checker {

    public GPSChecker(FilesDB files_db) {
        super(files_db);
        type = gpsType;
    }


}
