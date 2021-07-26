package com.highlander.arcgis;

import com.highlander.arcgis.utils.ConvertFileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FilenameFilter;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Client {
    public static Logger logger = LoggerFactory.getLogger(Client.class);

    public static void main(String[] args) throws Exception {

        long start = System.currentTimeMillis();
        logger.debug("start");
        new ConvertFileUtils().readAndCopy();

        logger.info("end");
        logger.info("耗时(ms)：" + (System.currentTimeMillis() - start));
    }
}
