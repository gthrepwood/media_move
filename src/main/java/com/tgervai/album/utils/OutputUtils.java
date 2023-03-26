package com.tgervai.album.utils;

import com.tgervai.album.config.Config;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

import static com.tgervai.album.config.ConfigKeys.base_dir;

@Component
@Slf4j
public class OutputUtils implements AutoCloseable {
    @Autowired
    Config config;

    public static boolean openWebpage(URI uri) {
        Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
        if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
            try {
                desktop.browse(uri);
                return true;
            } catch (Exception e) {
                log.debug("", e);
            }
        }
        return false;
    }

    public static boolean openWebpage(URL url) {
        try {
            return openWebpage(url.toURI());
        } catch (URISyntaxException e) {
            log.debug("", e);
        }
        return false;
    }

    FileOutputStream fos;

    public void startOutput() {

        String baseDir = config.getValue(base_dir);
        log.debug("" + baseDir);
        Path temp;
        try {
            temp = Files.createTempFile("output", ".html");
            fos = new FileOutputStream(temp.toFile(), true);
        } catch (IOException e) {
            log.error("", e);
            return;
        }


        try (PrintWriter p = new PrintWriter(fos, true, Charset.defaultCharset())) {
            p.println("<!DOCTYPE html><html lang=\"en\"><head><meta charset=\"UTF-8\"><title>Export</title></head><body>");

        }
    }

    @Override
    public void close() throws Exception {
        if (fos != null) {
            fos.close();
        }
    }
}
