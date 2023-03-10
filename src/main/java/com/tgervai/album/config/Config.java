package com.tgervai.album.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tgervai.album.file.FilesDB;
import com.tgervai.album.utils.StoreUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class Config implements Closeable {

    String defaultName = "config.json";
    String file = "{user.home}/album_data/" + defaultName;
    ObjectMapper mapper = new ObjectMapper();
    Map<String, String> map;
    @Autowired
    private StoreUtils<FilesDB> storeUtils;
    private Map<String, Object> values;

    public Config(StoreUtils<FilesDB> storeUtils) {
        this.storeUtils = storeUtils;
        map = new HashMap<>();
        map.put("user.home", System.getProperty("user.home"));
        read();
    }

    void read() {
        try {
            values = mapper.readValue(Paths.get(file).toFile(), Map.class);
            log.info("config read: " + file);
        } catch (IOException e) {
            try {
                InputStream is = getClass().getClassLoader().getResourceAsStream(defaultName);
                String text = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                values = mapper.readValue(text, Map.class);
                log.info("config read: classpath://" + defaultName);
            } catch (IOException e1) {
                log.error("", e1);
                values = new HashMap();
            }
        }
    }

    void save() throws IOException {
        log.info("config save: " + file);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.writerWithDefaultPrettyPrinter().writeValue(new File(file), values);

    }

    public String getString(ConfigKeys key1) {
        String key = key1.toString();
        if (values == null) {
            values = new HashMap<>();
        }
        if (values.containsKey(key)) {
            Object o = values.get(key);
            if (o != null) {
                String value = String.valueOf(o.toString());
                for (String m : map.keySet()) {
                    value = value.replace("{" + m + "}", map.get(m));
                }
                return value;
            }
        }
        values.put(key.toString(), key1.default_);
        return String.valueOf(key1.default_);
    }

    @Override
    public void close() {
//        save();
    }
}
