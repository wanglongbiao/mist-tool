package com.mist.testingtool.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mist.testingtool.service.RedisService;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisServiceImpl implements RedisService {
    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public List<String> findAbnormalData() {
        log.info("find...");
        List<String> resp = new ArrayList<>();
        HashOperations<String, String, String> hashOperations = stringRedisTemplate.opsForHash();
        stringRedisTemplate.keys("newAlarm:*").forEach(key -> {
            log.info("check {}", key);
            hashOperations.entries(key).forEach((k, v) -> {
                JSONObject jsonObject = JSON.parseObject(v);
                String eventId = jsonObject.getString("alarmEventId");
                String outKey = k.replaceAll("\"", "");
                if (!outKey.equals(eventId)) {
                    log.error("abnormal, key: {}, value: {}", k, v);
                    resp.add(k);
                }
            });
        });
        return resp;
    }
}
