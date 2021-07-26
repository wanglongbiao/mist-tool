package com.highlander.excel.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest
class ExcelServiceTest {

    @Autowired
    private ExcelUtil excelService;

    @Test
    void isWorkDay() {
//        Assert.assertTrue(excelService.isWorkDay("19-11-22 星期五"));
//        Assert.assertFalse(excelService.isWorkDay("19-11-23 星期六"));
        // 法定周末工作日
//        Assert.assertTrue(excelService.isWorkDay("20-01-19 星期六"));
    }

    @Test
    void getDestPath() {
//        Assert.assertEquals(excelService.getDestPath(),"/C:/wang-work/code/excel/target/test-classes/20191220");
    }

    @Test
    void run() {
    }

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

}