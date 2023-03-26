package com.tgervai.album.file;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FileExists {
    private FileData local;
    private FileData databaseFile;
}
