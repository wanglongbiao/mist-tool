package com.mist.testingtool.service.impl;

import com.mist.testingtool.service.KafkaService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class KafkaServiceImplTest {

    @Autowired
    private KafkaService kafkaService;
    @Test
    void queryByText() {
        String startTime = "2021-12-10T00:00:00";
        String endTime = "2021-12-10T00:10:00";
        String topic = "center-alarm";
//        kafkaService.queryByText(topic, "1", startTime);
//        kafkaService.queryByText(topic, "1464172539176685568", startTime, endTime);
        kafkaService.queryByText(topic, "center.+Time", startTime, endTime);
    }
}