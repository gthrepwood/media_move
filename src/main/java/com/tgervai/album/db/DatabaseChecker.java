package com.tgervai.album.db;

import com.tgervai.album.file.FileData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class DatabaseChecker implements Serializable {

    @Autowired
    Database database;

    public void saveDupes(String basepath) {
        for (Index name : Index.values()) {
            FileWriter p = null;
            try {
                String out = basepath + "/dupes_" + name + ".html";
                log.debug("save dupe report: " + out);
                p = new FileWriter(out, Charset.defaultCharset());

                if (database.getIndexes().get(name) != null) {

                    for (Map.Entry<String, List<FileData>> entry : database.getIndexes().get(name).entrySet()) {
                        String k = entry.getKey();
                        List<FileData> v = entry.getValue();
                        if (v.size() > 1) {
                            p.write(String.format("%s", k));
                            for (FileData y : v) {
                                String path = y.getPath().substring(basepath.length() + 1);
                                p.write(String.format("<img loading=\"lazy\" src='%s' width='150px' />", y.getPath(), path));
                            }
                            p.write("<br/>");
                        }
                    }
                }
            } catch (IOException ioException) {
                ioException.printStackTrace();
            } finally {
                if (p != null) {
                    try {
                        p.close();
                    } catch (IOException e) {
                        log.error("", e);
                    }
                }
            }
        }

    }
}