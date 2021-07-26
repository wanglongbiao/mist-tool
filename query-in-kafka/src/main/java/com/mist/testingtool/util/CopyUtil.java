package com.mist.testingtool.util;

import org.springframework.beans.BeanUtils;

import java.lang.reflect.Field;
import java.util.HashSet;

public class CopyUtil {

    public static void copyProperties(Object src, Object dest) {
        Field[] declaredFields = src.getClass().getDeclaredFields();
        HashSet<String> nullFieldSet = new HashSet<>();
        for (Field field : declaredFields) {
            field.setAccessible(true);
            try {
                if (field.get(src) == null) {
                    nullFieldSet.add(field.getName());
                    System.out.println(nullFieldSet);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        String[] nullSet = new String[nullFieldSet.size()];
        BeanUtils.copyProperties(src, dest, nullFieldSet.toArray(nullSet));
    }
}
