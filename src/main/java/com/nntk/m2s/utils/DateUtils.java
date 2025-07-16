package com.nntk.m2s.utils;

import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateUtils {

    public static String getCurrentDay(String date) {
        if (StringUtils.isEmpty(date)) {
            date = LocalDate.now().
                    format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }
        return date;
    }

    public static String getLastDay(String date) {
        if (StringUtils.isEmpty(date)) {
            date = LocalDate.now().
                    format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }
        // 定义日期格式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // 原始日期字符串
        String originalDateString = date;

        // 解析日期字符串为LocalDate对象
        LocalDate originalDate = LocalDate.parse(originalDateString, formatter);
        // 减去一天
        LocalDate newDate = originalDate.minusDays(1);
        // 格式化回字符串
        String newDateString = newDate.format(formatter);

        return newDateString;
    }
}
