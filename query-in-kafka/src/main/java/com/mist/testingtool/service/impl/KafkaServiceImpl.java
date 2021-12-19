package com.mist.testingtool.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mist.testingtool.service.KafkaService;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndTimestamp;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class KafkaServiceImpl implements KafkaService {
    @Override
    public void queryByText(String topic, String regex, String startTime) {
        queryByText(topic, regex, startTime, LocalDateTime.now().toString());
    }

    @Override
    public List<String> queryByText(String topic, String regex, String startTime, String endTime) {
        List<String> resultList = new ArrayList<>();
        KafkaConsumer<String, String> consumer = getConsumer();
        long startTimeInMillis = LocalDateTime.parse(startTime).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        long endTimeInMillis = LocalDateTime.parse(endTime).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        List<PartitionInfo> partitionInfos = consumer.partitionsFor(topic);
        List<TopicPartition> topicPartitionList = new ArrayList<>();
        Map<TopicPartition, Long> startTimeToSearch = new HashMap<>();
        for (PartitionInfo partitionInfo : partitionInfos) {
            TopicPartition topicPartition = new TopicPartition(partitionInfo.topic(), partitionInfo.partition());
            topicPartitionList.add(topicPartition);
            startTimeToSearch.put(topicPartition, startTimeInMillis);
        }
        consumer.assign(topicPartitionList);
        // 获取每个partition一个小时之前的偏移量
        Map<TopicPartition, OffsetAndTimestamp> map = consumer.offsetsForTimes(startTimeToSearch);
        System.out.println("开始设置各分区初始偏移量...");
        for (Map.Entry<TopicPartition, OffsetAndTimestamp> entry : map.entrySet()) {
            // 如果设置的查询偏移量的时间点大于最大的索引记录时间，那么value就为空
            OffsetAndTimestamp offsetTimestamp = entry.getValue();
            if (offsetTimestamp != null) {
                int partition = entry.getKey().partition();
                long timestamp = offsetTimestamp.timestamp();
                long offset = offsetTimestamp.offset();
                System.out.println("partition = " + partition + ", time = " + timestamp + ", offset = " + offset);
                // 设置读取消息的偏移量
                consumer.seek(entry.getKey(), offset);
            }
        }
        System.out.println("设置各分区初始偏移量结束...");
        Pattern pattern = Pattern.compile(regex);
        outer:
        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(20));
            if (records.count() <= 1) {
                log.info("count: {}", records.count());
                continue;
            }
            for (ConsumerRecord<String, String> record : records) {
                String data = record.value();
                long timestamp = record.timestamp();
                String timestampString = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault()).toString();
                if (timestamp > endTimeInMillis) {
                    log.info("end time: {}", timestampString);
//                    resultList.add(String.format("end time: %s", timestampString));
                    break outer;
                }
                if (pattern.matcher(data).find()) {
//                    log.info("time: {}, find {}", timestampString, data);
//                    resultList.add(String.format("time: %s, %s", timestampString, data));
                    JSONObject jsonObject = JSON.parseObject(data);
                    jsonObject.put("timestamp", timestamp);
                    resultList.add(jsonObject.toString());
                }
            }
        }
        log.info("end..");
        return resultList;
    }

    private KafkaConsumer<String, String> getConsumer() {
        Properties props = new Properties();
//        props.put("bootstrap.servers", "10.134.162.204:9092");
        props.put("bootstrap.servers", "10.58.76.10:9092");// center
//        props.put("bootstrap.servers", "10.100.0.214:9092");
        props.put("group.id", "sub-center-testing-by-wlb");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        log.info("config: {}", props);
        return consumer;
    }
}
