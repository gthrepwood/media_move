package com.tgervai.album.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tgervai.album.db.Database;
import com.tgervai.album.utils.StoreUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import static com.tgervai.album.config.ConfigKeys.save_config;

@Service
@Slf4j
public class Config implements Closeable, Serializable {

    String defaultName = "config.json";
    String file = "{user.home}/album_data/" + defaultName;
    ObjectMapper mapper = new ObjectMapper();
    Map<String, String> map;
    private Map<String, Object> values;

    public Config(StoreUtils<Database> storeUtils) {
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
                if (is!=null) {
                    String text = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                    values = mapper.readValue(text, Map.class);
                    log.info("config read: classpath://" + defaultName);
                } else {
                    log.debug("config file cannot be loaded from disk");
                    values = new HashMap();
                }
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

    public boolean isValue(ConfigKeys key1) {
        return "true".equalsIgnoreCase(getValue(key1));
    }

    public void set(String key, Object value) {
        var ck = ConfigKeys.valueOf(key);
        var def = ck.getDefaultValue();
        if (def instanceof Boolean) {
            values.put(key, "true".equalsIgnoreCase(value.toString()));
        } else if (def instanceof String) {
            values.put(key, value);
        }
    }

    public String getValue(ConfigKeys key1) {
        if (values == null) {
            values = new HashMap<>();
        }
        if (values.containsKey(key1.toString())) {
            Object key = values.get(key1.toString());
            if (key != null) {
                String value = String.valueOf(key.toString());
                for (String m : map.keySet()) {
                    value = value.replace("{" + m + "}", map.get(m));
                }
                return value;
            }
        }
        values.put(key1.toString().toString(), key1.getDefaultValue());
        return String.valueOf(key1.getDefaultValue());
    }

    @Override
    public void close() {
        if (isValue(save_config)) {
            try {
                save();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
