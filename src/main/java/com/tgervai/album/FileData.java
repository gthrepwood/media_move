package com.tgervai.album;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.Date;

@Slf4j
@Data
@NoArgsConstructor
public class FileData implements Comparable, Serializable {

    String name;
    String path;
    long size;
    int height;
    int width;
    double gpslat;
    double gpslong;
    Date origDate;

    @Override
    public int compareTo(Object o) {
        long a = ((FileData) o).size;

        return size == a ? 0 : (size > a ? -1 : 1);
    }

    public String get(FilesDB.index name) {
        return switch (name) {
            case nameType -> this.getNameForIndex();
            case sizeType -> String.valueOf(this.getSize());
            case dimType -> this.getDim();
            case gpsType -> this.getGpslatLong();
            default -> throw new IllegalStateException("Unexpected value: " + name);
        };
    }

    private String getGpslatLong() {
        if (gpslat == 0 || gpslong == 0) return null;
        return gpslat + "x" + gpslong;
    }

    String getDim() {
        if (height == 0 || width == 0) return null;
        return height + "x" + width;
    }

    String getNameForIndex() {
        return name.toLowerCase();
    }
}
