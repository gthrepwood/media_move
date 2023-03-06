package com.tgervai.album;

import lombok.extern.slf4j.Slf4j;

import java.io.Closeable;

@Slf4j
public class TimerUtil implements Closeable {

    long t;

    public TimerUtil(String text) {
        t = System.currentTimeMillis();
        log.debug(text);
    }

    public void close() {
        log.debug("Time: " + (System.currentTimeMillis() - t) / 1000.0);
    }
}
