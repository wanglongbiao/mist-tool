package com.mist.testingtool;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TestingToolApplication {

    public static void main(String[] args) throws IllegalAccessException {
        SpringApplication.run(TestingToolApplication.class, args);
//        User p1 = new User();
//        p1.setUsername("zhang san");
//        p1.setPassword("sdsdsdsd");
//
//        User p2 = new User();
//        p2.setId(123L);
//        p2.setPassword("ppppppppppppppp");
//
//        Field[] declaredFields = p1.getClass().getDeclaredFields();
//        for (Field field : declaredFields) {
//            field.setAccessible(true);
//            Object o = field.get(p1);
////            field.
//        }

//        BeanUtils.copyProperties(p1, p2);
//        CopyUtil.copyProperties(p1, p2);
//        System.out.println(p2);
    }

}

