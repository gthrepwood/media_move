package com.tgervai.album;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder @Data
public class FileExists {
    Checker checker;
    FileData local;
    FileData databaseFile;
}
