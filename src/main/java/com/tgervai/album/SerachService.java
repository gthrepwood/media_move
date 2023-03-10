package com.tgervai.album;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.exif.GpsDirectory;
import com.drew.metadata.jpeg.JpegDirectory;
import com.tgervai.album.checker.*;
import com.tgervai.album.config.Config;
import com.tgervai.album.file.FileData;
import com.tgervai.album.file.FileExists;
import com.tgervai.album.file.FilesDB;
import com.tgervai.album.utils.TimerUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.*;

import static com.tgervai.album.config.ConfigKeys.data_file;
import static com.tgervai.album.config.ConfigKeys.pictures_path;

@Slf4j
@Service
public class SerachService {

    String[] skipExt = {"json", "lrc", "picasa.ini"};
    @Autowired
    FilesDB files_db;
    @Autowired
    Config config;

    public static boolean checkIfFileHasExtension(String s, String[] extn) {
        return Arrays.stream(extn).anyMatch(entry -> s.endsWith(entry));
    }

    public static boolean openWebpage(URI uri) {
        Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
        if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
            try {
                desktop.browse(uri);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static boolean openWebpage(URL url) {
        try {
            return openWebpage(url.toURI());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void prepareFilesDB() {
        Assert.notNull(files_db, "files_db should not be null");
        try (TimerUtil ignored = new TimerUtil("loading data")) {
            files_db.load(config.getString(data_file));
            if (files_db.getFiles() != null)
                log.debug("loaded size: " + files_db.getFiles().size());
        }

        if (files_db == null || files_db.getFiles() == null || files_db.getFiles().size() == 0) {
            try (TimerUtil ignored = new TimerUtil("walk on files")) {
                files_db.clear();
                files_db.setFiles(walk(new TreeSet<>(), config.getString(pictures_path)));
            }
            files_db.save();
        }
        files_db.index();
    }

    public SortedSet<FileData> walk(SortedSet<FileData> ts, String path) {
        File root = new File(path);
        File[] list = root.listFiles();
        if (list == null) {
            return ts;
        }
        for (File f : list) {
            if (f.isDirectory()) {
                ts = walk(ts, f.getAbsolutePath());
            } else {
                if (!checkIfFileHasExtension(f.getName(), skipExt)) {
//                    file f1 = readMetadata(f);
                    FileData f1 = new FileData();
                    f1.setName(f.getName());
                    f1.setPath(f.getPath());
                    f1.setSize(f.length());
                    ts.add(f1);
                }
            }
        }
        return ts;
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
            log.error(f.getAbsolutePath() + " " + e.getMessage());
        }
        return f1;
    }


    public void run(String srcPath) {
//        String baseDir = config.getString(base_dir);
//        log.debug("" + baseDir);
        Path temp;
        FileOutputStream fos;
        try {
            temp = Files.createTempFile("output", ".html");
            fos = new FileOutputStream(temp.toFile(), true);
        } catch (IOException e) {
            log.error("", e);
            return;
        }

        try (PrintWriter p = new PrintWriter(fos, true, Charset.defaultCharset())) {
            p.println("<!DOCTYPE html><html lang=\"en\"><head><meta charset=\"UTF-8\"><title>Export</title></head><body>");

            prepareFilesDB();
            files_db.saveDupes(config.getString(pictures_path));

            SortedSet<FileData> filesToCheck = walk(new TreeSet<>(), srcPath);
            log.info("check path: " + srcPath + " #:" + filesToCheck.size());

            List<Checker> requiredCheckers = Arrays.asList(
                    new NameChecker(files_db),
                    new SizeChecker(files_db),
                    new DimensionChecker(files_db),
                    new GPSChecker(files_db)
            );

            List<FileExists> filesExists;

            for (FileData local : filesToCheck) {
                log.debug(local.getName() + " ---");
                boolean alltrue = true;
                boolean allfalse = true;
                for (Checker checker : requiredCheckers) {
                    List found = checker.find(local);
                    if (found == null) {
                        allfalse = false;
                    } else {
                        alltrue = false;
                    }
                }
                log.debug("alltrue:" + alltrue + "  allfalse:" + allfalse);

//                    List<FileData> foundList = files_db.byName(local);
//                if (foundList != null) {
//                    for (FileData found : foundList) {
//                        for (Checker checker : requiredCheckers) {
//                            if (checker.check(local, found)) {
//                                filesExists.add(FileExists.builder().checker(checker).local(local).databaseFile(found).build());
//                                FileExists f = filesExists.get(filesExists.size() - 1);
//                                log.debug(f.toString());
//                            }
//                        }
//                    }
//                }
            }
            p.println("</body></html>");
        }

        log.debug(temp.toUri().toString());
//        openWebpage(temp.toUri());
//        log.debug(temp.toString());

    }
}