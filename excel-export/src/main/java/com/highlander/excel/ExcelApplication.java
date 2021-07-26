package com.highlander.excel;

import com.highlander.excel.service.ExcelService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@SpringBootApplication
public class ExcelApplication {

    public static void main(String[] args) {
        LocalDate localDate = LocalDateTime.now().toLocalDate();
        int value = localDate.getDayOfWeek().getValue();
        DayOfWeek dayOfWeek = localDate.getDayOfWeek();
        System.out.println(localDate.format(DateTimeFormatter.ofPattern("MMdd")));

        SpringApplication.run(ExcelApplication.class, args);
    }

}
