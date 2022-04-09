package com.mist.testingtool.controller;

import com.mist.testingtool.service.RedisService;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/redis")
@RequiredArgsConstructor
public class RedisController {
    private final RedisService redisService;

    @RequestMapping("/find")
    public List<String> find() {
        return redisService.findAbnormalData();
    }
}
