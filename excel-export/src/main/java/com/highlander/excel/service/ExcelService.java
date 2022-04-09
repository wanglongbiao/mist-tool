package com.highlander.excel.service;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ExcelService implements CommandLineRunner {
    private Logger logger = LoggerFactory.getLogger(ExcelService.class);
    private static Map<String, Set<String>> yearWorkdayMap = new ConcurrentHashMap<>();
    private static Map<String, Workbook> userWorkBookMap = new ConcurrentHashMap<>();
    private static Map<String, FileOutputStream> userFileOutputStreamMap = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<String, LocalDateTime> startWorkMap = new ConcurrentHashMap<String, LocalDateTime>();
    private static DateTimeFormatter srcFormatter = DateTimeFormatter.ofPattern("yy-MM-dd");
    private static DateTimeFormatter destFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
    private static String currentFileDate = null;// 当前要处理的文件的时间戳，如20191220
    @Value("${reportExcelFile.name}")
    private String excelFileName;
    @Autowired
    private ExcelUtil excelUtil;

    /*
    流程：
    1. 遍历文件，如果符合条件，就写入到文件中去
        条件：平时>20:00
            周末
    2.写文件时，如果不存在就创建
     */
    private void doWork() throws Exception {
        deleteAndCreateDestPath();
        long start = System.currentTimeMillis();
        excelFileName = excelFileName.replaceAll("\\\\","/");
        Workbook workbook = new XSSFWorkbook(excelFileName);
        long end = System.currentTimeMillis();
        logger.info("读文件耗时(ms)：" + (end - start));
        Sheet sheet = workbook.getSheet("原始打卡记录");
        AtomicInteger nameIndex = new AtomicInteger();
        AtomicInteger timeIndex = new AtomicInteger();
        for (Row row : sheet) {
//            if (row.getRowNum() <= 0) continue;// 跳过表头
            if (row.getRowNum() == 0) {
                row.forEach(cell -> {
                    if(cell.getStringCellValue().equals("姓名")){
                        nameIndex.set(cell.getColumnIndex());
                    }
                    if(cell.getStringCellValue().equals("打卡时间")){
                        timeIndex.set(cell.getColumnIndex());
                    }
                });
                continue;
            }
            String userName = row.getCell(nameIndex.get()).getStringCellValue();
            String leaveTimeStr = row.getCell(timeIndex.get()).getStringCellValue();
            LocalDateTime clockTime = LocalDateTime.parse(leaveTimeStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            String key = userName + clockTime.toLocalDate();
            LocalDateTime startWorkTime = startWorkMap.get(key);
            if (startWorkTime == null) {
                startWorkMap.put(key, clockTime);
                continue;
            }
            if (startWorkTime.isAfter(clockTime)) {
                startWorkMap.put(key, clockTime);
            }
        }
        for (Row row : sheet) {
            if (row.getRowNum() <= 0) continue;// 跳过表头
            String leaveTimeStr = row.getCell(timeIndex.get()).getStringCellValue();
            LocalDateTime clockTime = LocalDateTime.parse(leaveTimeStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            if (StringUtils.isBlank(leaveTimeStr)) continue;// 跳过下班时间为空的
            boolean crossDay = leaveTimeStr.contains("次日");
//            String week = row.getCell(5).getStringCellValue();//19-09-26 星期四
            boolean isWorkDay = excelUtil.isWorkday(clockTime);// 是否是法定工作日
            if (crossDay) {
                writeToFile(row, !isWorkDay, true, clockTime);
                continue;
            }
//            LocalTime leaveTime = LocalTime.parse(leaveTimeStr);
            if (isWorkDay && clockTime.getHour() >= 20) // 符合工作日，加班到晚上8点的
                writeToFile(row, false, false, clockTime);
            else if (!isWorkDay)// 周末加班的
                writeToFile(row, true, false, clockTime);
        }
        postProcess();
    }

    /**
     * 删除旧文件，主要用来在多次生成时测试用的
     */
    private void deleteAndCreateDestPath() {
        String path = getDestPath();
        File dest = new File(path);
        if (dest.exists()) {
            try {
                FileUtils.deleteDirectory(dest);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        dest.mkdirs();
    }

    // 将一行加班数据写入到文件中
    private void writeToFile(Row row, boolean weekend, boolean isCrossDay, LocalDateTime endDateTime) {
        String userName = row.getCell(1).getStringCellValue();
        String key = userName + endDateTime.toLocalDate();
        LocalDateTime startWorkTime = startWorkMap.get(key);
        if (startWorkTime == null || startWorkTime.plusHours(4).isAfter(endDateTime)) {
            logger.info("错误的加班记录，key:{} start:{} clock:{}", key, startWorkTime, endDateTime);
            return;
        }
//        String date = endDateTime.toLocalDate().toString();//19-09-26 星期四
        String beginTime = startWorkTime.toLocalTime().toString();//08:20
        String endTime = endDateTime.toLocalTime().toString();//08:20
        if (isCrossDay) logger.info("跨天：" + userName);
        endTime = isCrossDay ? endTime.split(" ")[1] : endTime;
//        String[] dateArr = date.split(" ");
        LocalDate start = endDateTime.toLocalDate();
        LocalDate end = isCrossDay ? start.plusDays(1) : start;

//        String week = dateArr[1].replace("星期", "");
        String week = excelUtil.getWeekName(endDateTime);
        String userFileName = checkOrCreateFile(userName);
        try {
            Workbook workbook = userWorkBookMap.get(userName);
            if (workbook == null) {
                FileInputStream is = new FileInputStream(userFileName);
                workbook = new XSSFWorkbook(is);
                is.close();
                userWorkBookMap.put(userName, workbook);
            }
            if (!weekend) {
                Sheet sheet1 = workbook.getSheetAt(0);
                Row dataRow = sheet1.createRow(sheet1.getLastRowNum() + 1);
                dataRow.createCell(0).setCellValue(userName);
                dataRow.createCell(1).setCellValue(start.format(destFormatter));
                dataRow.createCell(2).setCellValue(end.format(destFormatter));
                dataRow.createCell(3).setCellValue("18:00");
                dataRow.createCell(4).setCellValue(endTime);
                dataRow.createCell(5).setCellValue(week);
                dataRow.createCell(6).setCellValue("");
                setBorderAndResize(dataRow);
            } else {// 处理周日
                Sheet sheet2 = workbook.getSheetAt(1);
                Row dataRow2 = sheet2.createRow(sheet2.getLastRowNum() + 1);
                dataRow2.createCell(0).setCellValue(userName);
                dataRow2.createCell(1).setCellValue(start.format(destFormatter));
                dataRow2.createCell(2).setCellValue(end.format(destFormatter));
                dataRow2.createCell(3).setCellValue(beginTime);
                dataRow2.createCell(4).setCellValue(endTime);
                dataRow2.createCell(5).setCellValue("");
                dataRow2.createCell(6).setCellValue(week);
                dataRow2.createCell(7).setCellValue("");
                setBorderAndResize(dataRow2);
            }
            FileOutputStream stream = new FileOutputStream(userFileName);
            workbook.write(stream);
            stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setBorderAndResize(Row dataRow) {
//        String userName = dataRow.getCell(0).getStringCellValue();
//        if (sizedMap.get(userName) == null) {
//            sizedMap.put(userName, userName);
//        }
        Sheet sheet = dataRow.getSheet();
        CellStyle cellStyle = sheet.getWorkbook().createCellStyle();
        configBorder(cellStyle);
        for (Cell cell : dataRow) {
            cell.setCellStyle(cellStyle);
            int i = cell.getColumnIndex();
            sheet.autoSizeColumn(i);
            sheet.setColumnWidth(i, sheet.getColumnWidth(i) * 16 / 10);
        }
    }

    private String checkOrCreateFile(String userName) {
        String path = getDestPath();
        File dir = new File(path);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String fileName = String.format("%s/加班统计-%s-%s.xlsx", path, userName, currentFileDate);
        File file = new File(fileName);
        if (!file.exists()) {
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet1 = workbook.createSheet("平时加班填写表");
            Sheet sheet2 = workbook.createSheet("节假日加班填写表");
            CellStyle headerStyle = workbook.createCellStyle();
            configBorder(headerStyle);

            headerStyle.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            Row header = sheet1.createRow(0);
            header.createCell(0).setCellValue("姓名");
            header.createCell(1).setCellValue("开始日期");
            header.createCell(2).setCellValue("结束日期");
            header.createCell(3).setCellValue("加班开始时间");
            header.createCell(4).setCellValue("加班结束时间");
            header.createCell(5).setCellValue("星期");
            header.createCell(6).setCellValue("备注");
            for (Cell cell : header) {
                cell.setCellStyle(headerStyle);
            }

            // sheet2
            Row header2 = sheet2.createRow(0);
            header2.createCell(0).setCellValue("姓名");
            header2.createCell(1).setCellValue("开始日期");
            header2.createCell(2).setCellValue("结束日期");
            header2.createCell(3).setCellValue("加班开始时间");
            header2.createCell(4).setCellValue("加班结束时间");
            header2.createCell(5).setCellValue("经理批准（非必填）");
            header2.createCell(6).setCellValue("星期");
            header2.createCell(7).setCellValue("备注");
            for (Cell cell : header2) {
                cell.setCellStyle(headerStyle);
            }
            try {
                FileOutputStream stream = userFileOutputStreamMap.get(userName);
                if (stream == null) {
                    stream = new FileOutputStream(file);
                    userFileOutputStreamMap.put(userName, stream);
                }
                workbook.write(stream);
                stream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return fileName;
    }

    /**
     * 生成文件的目录
     */
    public String getDestPath() {
        if (currentFileDate == null) {
            Matcher matcher = Pattern.compile("(\\d+-\\d+-\\d+)\\.").matcher(excelFileName);
            if (matcher.find()) {
                currentFileDate = matcher.group(1);
                currentFileDate = currentFileDate.replace("-", "");
            }
        }
        if (currentFileDate == null){
            currentFileDate = LocalDateTime.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        }
        return ExcelService.class.getClassLoader().getResource("").getPath() + currentFileDate;
    }

    private static void configBorder(CellStyle style) {
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
    }


    @Override
    public void run(String... args) throws Exception {
        long start = System.currentTimeMillis();
        logger.info("#### start ####");
        doWork();
        openDestPath();
        long end = System.currentTimeMillis();
        logger.info("#### end #### takes(s):" + ((end - start) / 1000.0));
    }

    private void openDestPath() {
        String destPath = getDestPath();
        try {
            Runtime.getRuntime().exec("cmd /c start " + destPath.substring(1));
//            Runtime.getRuntime().exec("cmd /c start " + destPath.substring(1) + "/加班统计-王龙彪-20191220.xlsx");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void postProcess() {
        for (FileOutputStream outputStream : userFileOutputStreamMap.values()) {
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
