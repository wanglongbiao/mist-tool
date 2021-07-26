package com.highlander.arcgis.utils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileRename extends SimpleFileVisitor<Path> {
    private static Logger log = LoggerFactory.getLogger(FileRename.class);
    private static Properties config = readConfig();
    private int count = 0;

    private static final String SOURCE_FOLDER = config.getProperty("SOURCE_FOLDER");
    private static final String ARCGIS_FOLDER = config.getProperty("ARCGIS_FOLDER");
    // 遍历目录，将文件按规则复制到指定目录

    public static void main(String[] args) {
        // readFromConfigFile();
        new FileRename().rename();
    }

    public void rename() {
        try {
            if (SOURCE_FOLDER == null || SOURCE_FOLDER.length() == 0 || ARCGIS_FOLDER == null || ARCGIS_FOLDER.length() == 0) {
                log.error("SOURCE_FOLDER or ARCGIS_FOLDER is null");
            }

            log.info("############### start ###############");

            Path source = Paths.get(ARCGIS_FOLDER);


            Files.walkFileTree(source, this);

            // log.info(path.toString());

            log.info("count:" + count);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }


    // d:\00-storted-map\base\fu-jian\L10\R429-C841.png
    public FileVisitResult visitFile(Path source, BasicFileAttributes attrs) throws IOException {

        if (source.toString().endsWith(".png")) {
            // log.info("..." + source.toString());
            String target = source.toString().replace(".png", ".jpg");

            // log.info(target);

            Files.move(source, Paths.get(target), StandardCopyOption.REPLACE_EXISTING);

        }
        // Files.mo


        return FileVisitResult.CONTINUE;
    }

    private void copy(Path source, String levelStr, String rowStr, String colStr) {
        try {
            Integer level = Integer.valueOf(levelStr);
            Integer row = Integer.valueOf(rowStr);
            Integer col = Integer.valueOf(colStr);

            levelStr = String.format("L%02d", level);
            rowStr = String.format("R%08X", row);
            colStr = String.format("C%08X.png", col);
            String destPath = String.join("/", ARCGIS_FOLDER, levelStr, rowStr, colStr);

            // log.info(source.getFileName().toString());
            // log.info(destPath);

            Path target = Paths.get(destPath);
            // if(dest.)
            if (Files.notExists(target.getParent())) {
                log.info(destPath + " 不存在，即将创建……");
                target.getParent().toFile().mkdirs();
            }
            
            count++;

            Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
            log.info(String.format("%d\t\tcopy %s to %s", (count++), source, target));

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

    }

    private static Properties readConfig() {
        Properties p = new Properties();
        try {
            InputStream is = FileRename.class.getClassLoader().getResourceAsStream("config.properties");
            p.load(is);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return p;


    }

}
