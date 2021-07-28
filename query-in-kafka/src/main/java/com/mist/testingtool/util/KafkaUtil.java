package com.mist.testingtool.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndTimestamp;
import org.apache.kafka.common.PartitionInfo;
import org.apache.kafka.common.TopicPartition;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Slf4j
public class KafkaUtil {

    private static KafkaConsumer<String, String> getConsumer() {
        Properties props = new Properties();
        props.put("bootstrap.servers", "10.100.0.221:9092");
        props.put("group.id", "unionTargetJs-testing-0521");
        props.put("key.deserializer",
                "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer",
                "org.apache.kafka.common.serialization.StringDeserializer");
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(props);
        return consumer;
    }

    public static void query(String topic, int minute, Map<String, String> params) {
        KafkaConsumer<String, String> consumer = getConsumer();
        long lastHourMilli = LocalDateTime.now().minusMinutes(minute).atZone(ZoneId.systemDefault()).toInstant()
                .toEpochMilli();
        // 获取topic的partition信息
        List<PartitionInfo> partitionInfos = consumer.partitionsFor(topic);
        List<TopicPartition> topicPartitions = new ArrayList<>();
        Map<TopicPartition, Long> timestampsToSearch = new HashMap<>();
        for (PartitionInfo partitionInfo : partitionInfos) {
            topicPartitions.add(new TopicPartition(partitionInfo.topic(), partitionInfo.partition()));
            timestampsToSearch.put(new TopicPartition(partitionInfo.topic(), partitionInfo.partition()), lastHourMilli);
        }
        consumer.assign(topicPartitions);
        // 获取每个partition一个小时之前的偏移量
        Map<TopicPartition, OffsetAndTimestamp> map = consumer.offsetsForTimes(timestampsToSearch);
        OffsetAndTimestamp offsetTimestamp;
        System.out.println("开始设置各分区初始偏移量...");
        for (Map.Entry<TopicPartition, OffsetAndTimestamp> entry : map.entrySet()) {
            // 如果设置的查询偏移量的时间点大于最大的索引记录时间，那么value就为空
            offsetTimestamp = entry.getValue();
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
        String mmsiParam = params.get("mmsi");
        String idParam = params.get("id");

        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(1000);
            if (records.count() <= 1) break;
//            log.info("length " + records.count());
            for (ConsumerRecord<String, String> record : records) {
                // System.out.println("partition = " + record.partition() + ", offset = " +
                // record.offset());
                String value = record.value();
                JSONObject jsonObject = JSON.parseObject(value);
                String id = jsonObject.getString("id");
                String mmsi = jsonObject.getString("mmsi");
                String alarmCode = jsonObject.getString("alarmCode");
//                long receiveTime = jsonObject.getLong("receiveTime");
                if (jsonObject.getLong("alarmTime") == null) {
                    continue;
                }
                String alarmCodeParam = params.get("alarmCode");
                if (alarmCodeParam != null && (!alarmCodeParam.equals(alarmCode))
                        || mmsiParam != null && mmsiParam.equals(mmsi)
                        || idParam != null && idParam.equals(id)) {
                    continue;
                }
                long receiveTime = jsonObject.getLong("alarmTime");
                int state = jsonObject.getIntValue("state");
                Double longitude = jsonObject.getDouble("longitude");
                Double latitude = jsonObject.getDouble("latitude");
                LocalDateTime time = LocalDateTime.ofInstant(Instant.ofEpochMilli(receiveTime), ZoneId.systemDefault());
                log.info("[{}] {}", time, jsonObject);
//                    System.out.println(String.format("[%s] id %s mmsi %s state %s", time, id, mmsi,state));
//                    log.info("[{}] id {} mmsi {} state {} lon {} lat {}", time, id, mmsi, state, longitude, latitude);
            }
//            log.info("after for...");
        }

    }


    public static void main(String[] args) {
        printKafkaTarget();
    }

    private static void printKafkaTarget() {
        HashMap<String, String> param = new HashMap<>();
//        param.put("mmsi", "100000062");
//        param.put("alarmCode", "20");// 非法搭靠
//        param.put("id","6825363070786912256");
//        query("unionTargetJs", 60, param);
        query("AlarmTargetTopic", 60, param);
        log.info("end...");
    }
}