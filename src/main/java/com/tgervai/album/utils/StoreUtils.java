package com.tgervai.album.utils;

import com.tgervai.album.config.Config;
import jdk.jfr.Timespan;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.*;

@Service
@Setter
@Getter
@Slf4j
public class StoreUtils<T> {

    String filename;
    private Config config;

    public void save(Object obj) throws IOException {
        FileOutputStream f = new FileOutputStream(filename);
        ObjectOutputStream o = new ObjectOutputStream(f);
        o.writeObject(obj);
        o.close();
        f.close();
    }

    @Timespan
    public T load(String filename) {
        this.filename = filename;
        T ts = null;
        try {
            FileInputStream fi = new FileInputStream(filename);
            ObjectInputStream oi = new ObjectInputStream(fi);
            ts = (T) oi.readObject();
            oi.close();
            fi.close();
            log.debug("loaded: " + filename);
        } catch (IOException | ClassNotFoundException e) {
            log.info("db fot found", e.getMessage());
        }
        return ts;
    }
}
