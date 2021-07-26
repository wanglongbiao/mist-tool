package com.highlander;

import com.highlander.entity.Position;
import com.highlander.util.CRSConvertor;
import com.highlander.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//import cn.wang.gis.Position;
//import cn.wang.gis.utils.CRSConvertor;

public class Client {

    private static Properties configMap = FileUtils.getConfigMap();

    private static String startTileLevle = configMap.getProperty("START_TILE_LEVLE");
    private static String endTileLevle = configMap.getProperty("END_TILE_LEVLE");

    private static String SOURCE_PATH = configMap.getProperty("SOURCE_PATH");
    private static String COORD_TEXT_PATH = configMap.getProperty("COORD_TEXT_PATH");

    static Logger logger = LoggerFactory.getLogger(Client.class);

    private static String sqlInsertHead = String.format("INSERT INTO [%s] ([LEVEL], [ROW], [COL], [LEFT_TOP_X], [LEFT_TOP_Y], [RIGHT_BOTTOM_X],[RIGHT_BOTTOM_Y]) VALUES ",
            configMap.getProperty("TABLE_NAME"));

    public static void main(String[] args) {//
        System.out.println("\n\n-- start\n\n");
        readPosition();
        System.out.println("\n\n-- end\n\n");
    }

    private static void readPosition() {
        try {
            int start = 0;
            int end = 0;
            if (startTileLevle != null && startTileLevle.length() > 0) {
                start = Integer.valueOf(startTileLevle);
            }
            if (endTileLevle != null && endTileLevle.length() > 0) {
                end = Integer.valueOf(endTileLevle);
            }
            for (int index = start; index <= end; index++) {
                String srcPositionPath = String.format("%s/L%02d/", COORD_TEXT_PATH, (index + 1));
                String destTilePath = String.format("%s/L%s/", SOURCE_PATH, index);
                File srcDir = new File(destTilePath);
                if (!srcDir.exists()) {
//                    logger.info("-- 找不到目录{}", srcDir);
                    System.out.println("-- 找不到目录," + srcDir);
                    continue;
                }

                int count = 0;
                int successRecord = 0;
                int failedRecord = 0;
                StringBuilder sb = new StringBuilder(sqlInsertHead);

                String[] fileArr = srcDir.list(new FilenameFilter() {
                    public boolean accept(File dir, String name) {
                        return name.endsWith(".png");
                    }
                });
                for (int i = 0; i < fileArr.length; i++) {
                    String file = fileArr[i];
                    Matcher m = Pattern.compile("R(\\d+)-C(\\d+)").matcher(file);
                    if (!m.find()) {
                        System.out.println("--匹配不到文件：" + file);
                        continue;
                    }
                    int row = Integer.valueOf(m.group(1));
                    int col = Integer.valueOf(m.group(2));
                    String srcTxtFile = String.format("%06d-%06d.txt", (row + 1), (col + 1));
                    srcTxtFile = srcPositionPath + srcTxtFile;

                    File srcFile = new File(srcTxtFile);
                    if (!srcFile.exists()) {
                        System.out.println("--找不到文件：" + srcFile.getAbsolutePath());
                        failedRecord++;
                        BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream("destPath" + "error.txt"));
                        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(destTilePath + "error.txt"));
                        bufferedWriter.write(file);

                        bufferedWriter.close();
                        os.close();
                        continue;
                    }

                    BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(srcTxtFile), Charset.forName("gbk")));
                    String line;
                    Position leftTop = null;
                    Position rightBottom = null;
                    while ((line = br.readLine()) != null) {
                        if (line.startsWith("左上角:") || line.startsWith("右下角")) {
                            boolean isLeft = line.startsWith("左上角:");

                            // System.out.println(line);
                            Matcher lineMatcher = Pattern.compile(":(\\S+)").matcher(line);
                            lineMatcher.find();
                            String position = lineMatcher.group(1);
                            // System.out.println(position);
                            String[] doubleArr = position.split(",");
                            Double x = Double.valueOf(doubleArr[0]);
                            Double y = Double.valueOf(doubleArr[1]);

                            double[] transform = CRSConvertor.transform(x, y);
                            Position lat = new Position(transform[0], transform[1]);

                            if (isLeft) {
                                leftTop = lat;
                            } else {
                                rightBottom = lat;
                            }
                        }
                    }

                    if (br != null) {
                        br.close();
                    }

                    count++;
                    if (count > 990) {
                        count = 0;

                        System.out.println(sb.append(";\n\n").toString());
                        sb = new StringBuilder(sqlInsertHead);
                    }
                    sb.append(
                            String.format("(%d, %d, %d, %d, %d, %d, %d)", index, row, col, (int) (leftTop.x * 1e6), (int) (leftTop.y * 1e6), (int) (rightBottom.x * 1e6), (int) (rightBottom.y * 1e6)));
                    // String.format("(%d, %d, %d, %d, %d, %d, %d)", index, row,
                    // col, (int) (leftTop.x * 1e6), (int) (leftTop.y * 1e6),
                    // (int) (rightBottom.x * 1e6),
                    // (int) (rightBottom.y * 1e6)));
                    successRecord++;

                    // System.out.println(sql);

                    if (i != fileArr.length - 1 && count < 990) {
                        sb.append(",");
                    }
                    sb.append("\n");

                    // DatabaseUtil.executeSql(sql);

                }

                sb.append(";");

                System.out.println(sb.toString());

                System.out.println(String.format("--级别：%s\t成功数量：%s\t失败数量：%s\n\n", index, successRecord, failedRecord));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
