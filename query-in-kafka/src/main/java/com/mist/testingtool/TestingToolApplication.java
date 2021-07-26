package com.mist.testingtool;

import com.mist.testingtool.util.CopyUtil;

import org.springframework.beans.BeanUtils;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.stream.Collectors;

import lombok.Data;

@SpringBootApplication
public class TestingToolApplication {

    public static void main(String[] args) throws IllegalAccessException {
//        SpringApplication.run(TestingToolApplication.class, args);
        Person p1 = new Person();
        p1.setUsername("zhang san");
        p1.setPassword("sdsdsdsd");

        Person p2 = new Person();
        p2.setId(123L);
        p2.setPassword("ppppppppppppppp");

        Field[] declaredFields = p1.getClass().getDeclaredFields();
        for (Field field : declaredFields) {
            field.setAccessible(true);
            Object o = field.get(p1);
//            field.
        }

//        BeanUtils.copyProperties(p1, p2);
        CopyUtil.copyProperties(p1,p2);
        System.out.println(p2);
    }

}

@Data
class Person {
    private Long id;
    private String username;
    private String password;
    private Integer age;
    private Integer phone;
}