package com.tgervai.album.utils;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.exif.GpsDirectory;
import com.drew.metadata.jpeg.JpegDirectory;
import com.tgervai.album.file.FileData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.Serializable;
import java.util.Arrays;
import java.util.SortedSet;

@Slf4j
@Component
public class FileUtils  implements Serializable {
    int files = 0;
    static int max_files_to_read = 10000;
    String[] skipExt = {"json", "lrc", "picasa.ini"};

    public SortedSet<FileData> walk(SortedSet<FileData> ts, String path) {
        File root = new File(path);
        File[] list = root.listFiles();
        if (list == null) {
            return ts;
        }
        for (File f : list) {

//            if (files > max_files_to_read) return ts;

            if (f.isDirectory()) {
                ts = walk(ts, f.getAbsolutePath());
            } else {
                if (!checkIfFileHasExtension(f.getName(), skipExt)) {
                    FileData f1 = readMetadata(f);
                    f1.setName(f.getName());
                    f1.setPath(f.getPath());
                    f1.setSize(f.length());
                    ts.add(f1);
                    files++;
                    if (files % 5000 == 0) {
                        log.debug("read {}", files);
                    }
                }
            }
        }
        return ts;
    }

    public static boolean checkIfFileHasExtension(String s, String[] extn) {
        return Arrays.stream(extn).anyMatch(entry -> s.endsWith(entry));
    }

    private FileData readMetadata(File f) {
        FileData f1 = new FileData();
        ExifSubIFDDirectory idf;
        JpegDirectory j;
        Metadata metadata;
        try {
            metadata = ImageMetadataReader.readMetadata(f);

            j = metadata.getFirstDirectoryOfType(JpegDirectory.class);
            if (j != null) {
                f1.setHeight(j.getImageHeight());
                f1.setWidth(j.getImageWidth());
            }

            idf = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
            if (idf != null) {
                f1.setOrigDate(idf.getDateOriginal());
            }

            if (metadata.containsDirectoryOfType(GpsDirectory.class)) {
                GpsDirectory gpsDirectory = metadata.getFirstDirectoryOfType(GpsDirectory.class);
                if (gpsDirectory.containsTag(GpsDirectory.TAG_LATITUDE) && gpsDirectory.containsTag(GpsDirectory.TAG_LONGITUDE)) {
                    f1.setGpslat(gpsDirectory.getGeoLocation().getLatitude());
                    f1.setGpslong(gpsDirectory.getGeoLocation().getLongitude());
                }
            }

        } catch (Exception e) {
            f1.setError(e.getMessage());
            log.error(f.getAbsolutePath() + " " + e.getMessage());
        }
        return f1;
    }

}
