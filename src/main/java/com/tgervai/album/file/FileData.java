package com.tgervai.album.file;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.tgervai.album.db.Index;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;

@Slf4j
@Data
@NoArgsConstructor
//@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@JsonInclude
@EqualsAndHashCode
public class FileData implements Serializable, Comparable {
    String name;
    String path;
    Long size;
    Integer height;
    Integer width;
    Double gpslat;
    Double gpslong;
    Date origDate;
    String error;

    //    @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
    public String get(Index name) {
        return switch (name) {
            case nameType -> this.getNameForIndex();
            case sizeType -> this.getSizeForIndex();
            case dimType -> this.getDimForIndex();
            case gpsType -> this.getGpslatLongForIndex();
            case fullpath -> this.getFullpath();
            case hash -> this.getHash();
        };
    }

    private String getHash() {
        return Arrays.asList(name, size, height, width, gpslat, gpslong).toString();
//        return /*String.valueOf(Objects.hashCode(*/name + path + size + height + width + gpslat + gpslong/*))*/;
    }

    private String getFullpath() {
        return path;
    }

    private String getSizeForIndex() {
        return String.valueOf(this.getSize());
    }

    private String getGpslatLongForIndex() {
        if (gpslat == null || gpslong ==
                null || gpslat == 0 || gpslong == 0) return null;
        return gpslat + "x" + gpslong;
    }

    String getDimForIndex() {
        if (height == null || width == null || height == 0 || width == 0) return null;
        return height + "x" + width;
    }

    String getNameForIndex() {
        return name.toLowerCase();
    }

    @Override
    public int compareTo(final Object o) {
        return Integer.valueOf(o.hashCode()).compareTo(hashCode());
    }
}
