package com.mist.testingtool.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.io.FileWriter;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
public class AisShipDatabaseUtil implements CommandLineRunner {
    private static Map<String, String> filedMap = new ConcurrentHashMap<>();
    private SequenceGenerator sequenceGenerator = new SequenceGenerator();

    static {
        filedMap.put("ship_cnname", "vessel_name");
        filedMap.put("ship_enname", "vessel_name");
        filedMap.put("ship_type", "ship_type");
        filedMap.put("ship_mmsi", "mmsi");
        filedMap.put("ship_callsign", "call_sign");
        filedMap.put("ship_imo", "imo");
        filedMap.put("ship_length", "length");
        filedMap.put("ship_width", "wide");
        filedMap.put("ship_nationality", "nationality");
        filedMap.put("create_time", "create_time");
        filedMap.put("update_time", "update_time");
    }

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public static void main(String[] args) {
        String line = "INSERT INTO mscode_syhy_ship (`id`, `ship_cnname`, `ship_enname`, `ship_type`, `ship_mmsi`, `ship_callsign`, `ship_imo`, " +
                "`ship_beidou`, `ship_length`, `ship_width`, `ship_nationality`, `ship_homeport`, `ship_tonnage`, `ship_photo`, `creator`, `create_time`, `update_time`, `remark`) ";

        log.info(new AisShipDatabaseUtil().fillSql(line, new HashMap<>()));
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("start..");
        String sql = "select * from ais_static_info";
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
        log.info("size {}", list.size());
        FileWriter writer = new FileWriter("result.sql");
        writer.append("-- start " + LocalDateTime.now() + " \n");
        String line = "INSERT INTO mscode_syhy_ship(`id`, `ship_cnname`, `ship_enname`, `ship_type`, `ship_mmsi`, `ship_callsign`, `ship_imo`, " +
                "`ship_beidou`, `ship_length`, `ship_width`, `ship_nationality`, `ship_homeport`, `ship_tonnage`, `ship_photo`, `creator`, `create_time`, `update_time`, `remark`) ";
        for (Map<String, Object> map : list) {
            writer.append(fillSql(line, map));
        }
        writer.append("-- end ").append(String.valueOf(LocalDateTime.now()));
        writer.flush();
        writer.close();
        log.info("ended");
    }

    private String fillSql(String line, Map<String, Object> map) {
        Matcher matcher = Pattern.compile("`(.+?)`").matcher(line);
        StringBuilder builder = new StringBuilder(line);
        builder.append(" values (");
        List<String> values = new ArrayList<>();
        while (matcher.find()) {
            String destField = matcher.group(1);
            if (destField.equals("id")) {
                values.add(getId());
                continue;
            }
            if (destField.equals("creator")) {
                values.add("61"); // non null, landtool1
                continue;
            }
            Object srcField = map.get(filedMap.get(destField));
            if (srcField == null) {
                values.add("NULL");
            } else {
                if (srcField instanceof String || srcField instanceof Date) {
                    values.add(String.format("'%s'", srcField));
                } else {
                    values.add(String.format("%s", srcField));
                }
            }
//            log.info("find {} {}", destField, matcher.groupCount());
        }
        builder.append(String.join(", ", values));
        builder.append(" );\n");
        return builder.toString();
    }

    private String getId() {
        return sequenceGenerator.nextId() + "";
    }
}
