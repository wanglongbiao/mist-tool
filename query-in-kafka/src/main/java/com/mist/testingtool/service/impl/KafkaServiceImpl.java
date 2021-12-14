package com.mist.testingtool.service.impl;

import com.mist.testingtool.service.KafkaService;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndTimestamp;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class KafkaServiceImpl implements KafkaService {
    @Override
    public void queryByText(String topic, String keyword, String startTime) {
       queryByText(topic,keyword,startTime, LocalDateTime.now().toString());
    }

    @Override
    public void queryByText(String topic, String keyword, String startTime, String endTime) {
        KafkaConsumer<String, String> consumer = getConsumer();
        long startTimeInMillis = LocalDateTime.parse(startTime).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
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

        while (true) {
//            log.info("query...");
//            ConsumerRecords<String, String> records = consumer.poll(1000);
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(20));
            if (records.count() <= 1) {
                log.info("count: {}", records.count());
                continue;
            }
            for (ConsumerRecord<String, String> record : records) {
                String data = record.value();
                record.timestamp();
                if (data.contains(keyword)) {
                    log.info("find {}", data);
                }
            }
        }
//        log.info("end..");
    }

    private KafkaConsumer<String, String> getConsumer() {
        Properties props = new Properties();
        props.put("bootstrap.servers", "10.100.0.214:9092");
        props.put("group.id", "sub-center-testing-by-wlb");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        return consumer;
    }
}
