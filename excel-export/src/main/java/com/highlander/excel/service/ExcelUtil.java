package com.highlander.excel.service;

import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ExcelUtil {
    private static Map<String, Set<String>> yearWorkdayMap = new ConcurrentHashMap<>();

    ExcelUtil() {
        String path = ExcelService.class.getClassLoader().getResource("api").getPath();
        File dict = new File(path);
        if (dict.exists()) {
            File[] files = dict.listFiles();
            assert files != null;
            for (File file : files) {
                try {
                    String year = file.getName().split("_")[0];
                    BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
                    String line;
                    while (null != (line = bufferedReader.readLine())) {
                        Set<String> workSet = yearWorkdayMap.computeIfAbsent(year, k -> new HashSet<>());
                        workSet.add(line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // 例如，2020.01.19 是周六，但是为工作日，就返回 true
    // 其他的，如果是周六、周日，就返回 false
    // 暂时没有判断周一是节假日的情况
    public boolean isWorkday(LocalDate localDate) {
        String week = localDate.getDayOfWeek().toString();
        if (week.equals("SATURDAY") || week.equals("SUNDAY")) {
            String monthDay = localDate.format(DateTimeFormatter.ofPattern("MMdd"));
//            return yearWorkdayMap.get(localDate.getYear() + "").contains(monthDay);
            return false;
        }
        return true;
    }

    public boolean isWorkday(LocalDateTime localDateTime) {
        return isWorkday(localDateTime.toLocalDate());
    }

    public String getWeekName(LocalDateTime localDateTime) {
        String week = localDateTime.getDayOfWeek().toString().toLowerCase();
        switch (week) {
            case "monday":
                return "一";
            case "tuesday":
                return "二";
            case "wednesday":
                return "三";
            case "thursday":
                return "四";
            case "friday":
                return "五";
            case "saturday":
                return "六";
            case "sunday":
                return "日";
        }
        return "week";
    }
}
