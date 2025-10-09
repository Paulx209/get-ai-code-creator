package com.getian.getaicodemother.utils;

import org.junit.jupiter.api.Test;


class WebScreenShotUtilsTest {

    @Test
    void saveWebPageScreenshot() {
        String url="http://113.44.209.173/";
        String path = WebScreenShotUtils.saveWebPageScreenshot(url);
        System.out.println(path);
    }
}
