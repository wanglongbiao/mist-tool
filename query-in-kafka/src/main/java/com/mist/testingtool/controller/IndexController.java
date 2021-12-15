package com.mist.testingtool.controller;

import com.mist.testingtool.service.KafkaService;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/")
public class IndexController {

    private final KafkaService kafkaService;

    public IndexController(KafkaService kafkaService) {
        this.kafkaService = kafkaService;
    }

    @RequestMapping("/")
    public String index(Map<String, String> map) {
        map.put("topic", "center-alarm");
        map.put("regex", "1459039324787052544.+4SZHY3119061205689499651");
        map.put("startTime", "2021-12-10T05:30:00");
        map.put("endTime", "2021-12-10T05:30:00");
        return "index";
    }

    @ResponseBody
    @RequestMapping("/query")
    public List<String> query(String topic, String regex, String startTime, String endTime) {
        return kafkaService.queryByText(topic, regex, startTime, endTime);
    }
}
