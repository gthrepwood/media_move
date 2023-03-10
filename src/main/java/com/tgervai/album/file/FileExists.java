package com.tgervai.album.file;

import com.tgervai.album.checker.Checker;
import lombok.Builder;
import lombok.Data;

@Builder @Data
public class FileExists {
    Checker checker;
    FileData local;
    FileData databaseFile;
}
