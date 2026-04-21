package com.example.provider.utils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * 时间工具类
 * 提供获取当前时间的方法，使用 JDK 1.8 的 java.time API
 */
public class TimeUtils {
    
    private TimeUtils() {
        // 工具类，防止实例化
    }
    
    /**
     * 获取当前 LocalDateTime 对象
     * @return 当前 LocalDateTime
     */
    public static LocalDateTime getCurrentLocalDateTime() {
        return LocalDateTime.now();
    }
    
    /**
     * 获取当前时间戳（毫秒）
     * @return 当前时间戳
     */
    public static long getCurrentTimestamp() {
        return System.currentTimeMillis();
    }
    
    /**
     * 获取当前 Date 对象
     * @return 当前 Date
     */
    public static Date getCurrentDate() {
        return new Date();
    }
    
    /**
     * 获取当前时间字符串（默认格式：yyyy-MM-dd HH:mm:ss）
     * @return 格式化后的时间字符串
     */
    public static String getCurrentTimeString() {
        return getCurrentTimeString("yyyy-MM-dd HH:mm:ss");
    }
    
    /**
     * 获取当前时间字符串（自定义格式）
     * @param pattern 时间格式，如 "yyyy-MM-dd HH:mm:ss"
     * @return 格式化后的时间字符串
     */
    public static String getCurrentTimeString(String pattern) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
        return LocalDateTime.now().format(formatter);
    }
    
    /**
     * 获取指定时区的当前时间
     * @param zoneId 时区ID，如 "Asia/Shanghai"
     * @return 指定时区的当前 ZonedDateTime
     */
    public static ZonedDateTime getCurrentZonedDateTime(String zoneId) {
        return ZonedDateTime.now(ZoneId.of(zoneId));
    }
    
    /**
     * 获取当前年份
     * @return 当前年份
     */
    public static int getCurrentYear() {
        return LocalDateTime.now().getYear();
    }
    
    /**
     * 获取当前月份（1-12）
     * @return 当前月份
     */
    public static int getCurrentMonth() {
        return LocalDateTime.now().getMonthValue();
    }
    
    /**
     * 获取当前日期（月中的第几天，1-31）
     * @return 当前日期
     */
    public static int getCurrentDayOfMonth() {
        return LocalDateTime.now().getDayOfMonth();
    }
    
    /**
     * 获取当前小时（0-23）
     * @return 当前小时
     */
    public static int getCurrentHour() {
        return LocalDateTime.now().getHour();
    }
    
    /**
     * 获取当前分钟（0-59）
     * @return 当前分钟
     */
    public static int getCurrentMinute() {
        return LocalDateTime.now().getMinute();
    }
    
    /**
     * 获取当前秒（0-59）
     * @return 当前秒
     */
    public static int getCurrentSecond() {
        return LocalDateTime.now().getSecond();
    }
}