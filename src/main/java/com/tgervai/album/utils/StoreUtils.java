package com.tgervai.album.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tgervai.album.config.ConfigKeys;
import jdk.jfr.Timespan;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

@Component
@Setter
@Getter
@Slf4j
public class StoreUtils<T> implements Serializable {

    String filename;

    public void save(Object obj, final ConfigKeys type) throws IOException {

        log.debug("save file " + filename + " " + type);
        if (ConfigKeys.save_json.equals(type)) {
            try (TimerUtil ignored = new TimerUtil("json")) {
                File file = new File(filename + ".json");
                ObjectMapper mapper = new ObjectMapper();
                mapper.writerWithDefaultPrettyPrinter().writeValue(file, obj);
            }
        } else if (ConfigKeys.save_object_gz.equals(type)) {
            try (TimerUtil ignored = new TimerUtil("java gz")) {
                String name = filename + ".gz";
                log.debug("Save " + name);
                FileOutputStream f = new FileOutputStream(name);
                GZIPOutputStream gz = new GZIPOutputStream(f);
                ObjectOutputStream o = new ObjectOutputStream(gz);
                o.writeObject(obj);
                o.close();
                gz.close();
                f.close();
            }
        } else {
            log.error("invalid type to save: " + type);
        }
    }

    @Timespan
    public T load(String filename) {
        this.filename = filename;
        T ts = null;
        try {
            FileInputStream fi = new FileInputStream(filename);
            GZIPInputStream gz = new GZIPInputStream(fi);
            ObjectInputStream oi = new ObjectInputStream(gz);
            ts = (T) oi.readObject();
            oi.close();
            gz.close();
            fi.close();
            log.debug("loaded: " + filename);
        } catch (IOException | ClassNotFoundException e) {
            log.info("db fot found", e);
        }
        return ts;
    }
}
