package com.tgervai.album.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.Closeable;

@Slf4j
public class TimerUtil implements Closeable {

    long t;
    String name;

    public TimerUtil(String name) {
        t = System.currentTimeMillis();
        this.name = name;
    }

    public void close() {
        log.debug("Timer end for: " + name + ": " + ((System.currentTimeMillis() - t) / 1000.0) + " sec");
    }
}
