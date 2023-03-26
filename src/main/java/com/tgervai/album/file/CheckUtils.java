package com.tgervai.album.file;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class CheckUtils {
    Splitter splitter = Splitter.on('/').trimResults().omitEmptyStrings();
    Joiner joiner = Joiner.on("/");

    public String getParent(String s) {
        List<String> list = new ArrayList<>(splitter.splitToList(s));
        int size = list.size();
        if (size > 3) {
            if (list.get(size - 2).equals(list.get(size - 3))) {
                list.remove(size - 2);
                return "/" + joiner.join(list);
            }
        }
        return null;
    }
}
