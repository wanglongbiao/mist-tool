package com.highlander.util;

import java.io.InputStream;
import java.util.Properties;

public class FileUtils {

    public final static String CONFIG_FILE_NAME = "config.properties";


    public static Properties getConfigMap() {
        Properties p = new Properties();
        try {
            InputStream inputStream = ClassLoader.getSystemResourceAsStream(CONFIG_FILE_NAME);
            p.load(inputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return p;
    }

}
