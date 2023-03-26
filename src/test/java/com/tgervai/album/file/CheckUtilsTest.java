package com.tgervai.album.file;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CheckUtilsTest {

    CheckUtils underTest = new CheckUtils();

    @Test
    void getParent() {
        String s= underTest.getParent("aaa/2022/12/31/31/x.y");
        assertEquals(s, "aaa/2022/12/31/x.y");
    }
}