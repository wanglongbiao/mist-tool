package com.mist.testingtool.service;

import java.util.List;

/**
 * kafka 常用查询
 */
public interface KafkaService {
    /**
     * 从开始时间读取某个 topic，将 json 转化为 map，然后打印出来 value 中包含关键字的数据
     *
     * @param topic     kafka topic
     * @param regex     要搜索的文本, 支持正则表达式
     * @param startTime 开始时间，yyyy-MM-ddTHH:mm:ss
     */
    void queryByText(String topic, String regex, String startTime);

    /**
     * 从开始时间读取某个 topic，将 json 转化为 map，然后打印出来 value 中包含关键字的数据
     *
     * @param topic     kafka topic
     * @param regex     要搜索的文本, 支持正则表达式
     * @param startTime 开始时间，yyyy-MM-ddTHH:mm:ss
     * @param endTime   结束时间，yyyy-MM-ddTHH:mm:ss
     * @return
     */
    List<String> queryByText(String topic, String regex, String startTime, String endTime);
}
