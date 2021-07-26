package com.highlander.arcgis.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 该程序的主要功能的实现
 */
public class ConvertFileUtils extends SimpleFileVisitor<Path> {
    private static Logger log = LoggerFactory.getLogger(ConvertFileUtils.class);
    private static Properties config = readConfig();
    private int count = 0;

    private static final String SOURCE_FOLDER = config.getProperty("SOURCE_FOLDER");
    private static final String ARCGIS_FOLDER = config.getProperty("ARCGIS_FOLDER");
    // 遍历目录，将文件按规则复制到指定目录

    /**
     * 读取，然后复制
     */
    public void readAndCopy() {
        try {
            if (SOURCE_FOLDER == null || SOURCE_FOLDER.length() == 0 || ARCGIS_FOLDER == null || ARCGIS_FOLDER.length() == 0) {
                log.error("SOURCE_FOLDER or ARCGIS_FOLDER is null");
                return;
            }

            log.info("############### start ###############");

            log.info(String.format("SOURCE_FOLDER:%s, ARCGIS_FOLDER:%s", SOURCE_FOLDER, ARCGIS_FOLDER));
            Path path = Paths.get(SOURCE_FOLDER);


            Files.walkFileTree(path, this);

            // log.info(path.toString());

            log.info("count:" + count);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }


    /**
     * 重写方法，实现复制文件
     */
    public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {

        // log.info("Visiting:" + path);// do some thing
        String pathStr = path.toString();

        String regex = "L(\\d+)\\\\R(\\d+)-C(\\d+)";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(pathStr);
        if (m.find()) {
            // log.info(m.group(1) + "," + m.group(2) + "," + m.group(3));
            copy(path, m.group(1), m.group(2), m.group(3));

            // String.format("%02d", level);

            // log.info(levelStr + "," + rowStr + "," + colStr);

        }

        return FileVisitResult.CONTINUE;
    }

    private void copy(Path source, String levelStr, String rowStr, String colStr) {
        try {
            Integer level = Integer.valueOf(levelStr);
            Integer row = Integer.valueOf(rowStr);
            Integer col = Integer.valueOf(colStr);

            levelStr = String.format("L%02d", level);
            rowStr = String.format("R%08X", row);
            colStr = String.format("C%08X.jpg", col);
            String destPath = String.join("/", ARCGIS_FOLDER, levelStr, rowStr, colStr);

            // log.info(source.getFileName().toString());
            // log.info(destPath);

            Path target = Paths.get(destPath);
            // if(dest.)
            if (Files.notExists(target.getParent())) {
                log.info(destPath + " 不存在，即将创建 ……");
                boolean mkdirs = target.getParent().toFile().mkdirs();
                if (mkdirs) {
                    log.info("create success");
                }
            }

            count++;

            Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
            log.info(String.format("%d\t\tcopy %s to %s", (count), source,
                    target));

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

    }

    /**
     * 读取配置文件
     */
    private static Properties readConfig() {
        Properties p = new Properties();
        try {
            InputStream is = ConvertFileUtils.class.getClassLoader().getResourceAsStream("config.properties");
            p.load(is);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return p;


    }

}
