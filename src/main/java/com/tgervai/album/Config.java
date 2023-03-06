package com.tgervai.album;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class Config implements Closeable {

    @Autowired
    private StoreUtils<FilesDB> storeUtils;
    String file = System.getProperty("user.home") + "/album_data/album_configuration.json";
    ObjectMapper mapper = new ObjectMapper();
    private Map<String, Object> values;

    public Config(StoreUtils<FilesDB> storeUtils) {
        this.storeUtils = storeUtils;
        read();
    }

    void read() {
        log.info("config read: " + file);
        try {
            values = mapper.readValue(Paths.get(file).toFile(), Map.class);
        } catch (IOException e) {
            log.error(e.getMessage());
            values = new HashMap();
        }
    }

    void save() throws IOException {
        log.info("config save: " + file);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.writerWithDefaultPrettyPrinter().writeValue(new File(file), values);

    }

    public String getString(ConfigKeys key) {
        if (values == null) {
            values = new HashMap<>();
        }
        if (values.containsKey(key.toString())) {
            Object o = values.get(key);
            if (o != null) {
                return String.valueOf(String.valueOf(o.toString()));
            }
        }
        values.put(key.toString(), key.default_);
        return String.valueOf(key.default_);
    }

    @Override
    public void close() throws IOException {
        save();
    }
}
