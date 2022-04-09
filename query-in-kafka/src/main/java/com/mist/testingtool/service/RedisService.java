package com.mist.testingtool.service;

import java.util.List;

public interface RedisService {

    List<String> findAbnormalData();

    void searchAreaAlarmCondition(String areaId);
}
