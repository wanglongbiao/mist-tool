package com.mist.testingtool.service.impl;

import com.mist.testingtool.service.RedisService;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
class RedisServiceImplTest {

    @Resource
    private RedisService redisService;

    @SneakyThrows
    @Test
    void findAbnormalData() {
        while (true) {
            log.info("start checking...");
            List<String> abnormalData = redisService.findAbnormalData();
            log.info("size: {}", abnormalData.size());
            abnormalData.forEach(System.out::println);
            TimeUnit.SECONDS.sleep(30);
        }
    }

    @Test
    void testSearchAreaAlarmCondition() {
        redisService.searchAreaAlarmCondition("1475346977708445696");
    }
}