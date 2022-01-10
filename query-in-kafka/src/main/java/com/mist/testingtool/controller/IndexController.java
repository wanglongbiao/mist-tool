package com.mist.testingtool.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mist.testingtool.service.KafkaService;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

@Controller
@RequestMapping("/")
@Slf4j
public class IndexController {

    private final KafkaService kafkaService;

    public IndexController(KafkaService kafkaService) {
        this.kafkaService = kafkaService;
    }

    @RequestMapping("/")
    public String index(Map<String, String> map) {
        LocalDateTime now = LocalDateTime.now();
        map.put("topic", "center-alarm");
        map.put("regex", ".*");
        map.put("startTime", now.minusHours(2).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        map.put("endTime", now.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        return "index";
    }

    @ResponseBody
    @RequestMapping("/query")
    public List<String> query(String topic, String regex, String startTime, String endTime) {
        return kafkaService.queryByText(topic, regex, startTime, endTime);
    }

    @ResponseBody
    @RequestMapping("/queryLag")
    public List<String> queryLag() {
        LocalDateTime now = LocalDateTime.now();
        int minutes = 1;
        int seconds = 30;
        List<String> messageList = kafkaService.queryByText("unionTargetJs", ".*", now.minusMinutes(minutes).toString(), now.toString());
        List<String> resultList = new ArrayList<>();
        messageList.forEach(data -> {
            JSONObject jsonObject = JSON.parseObject(data);
            Long lastTm = jsonObject.getLong("lastTm");
            Long timestamp = jsonObject.getLong("timestamp");
            Long targetId = jsonObject.getLong("id");
            LocalDateTime lastTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(lastTm), ZoneId.systemDefault());
            LocalDateTime kafkaTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault());
            if (kafkaTime.minusSeconds(seconds).isAfter(lastTime)) {
                log.info("diff: {}s, lastTm: {}, kafka time: {}, target id {}", Duration.between(lastTime, kafkaTime).getSeconds(), lastTime, kafkaTime, targetId);
                resultList.add(String.valueOf(jsonObject));
            }
        });
        log.info("{} 分钟内延迟超过 {}s 的目标数量：{}", minutes, seconds, resultList.size());
        return resultList;
    }
}
