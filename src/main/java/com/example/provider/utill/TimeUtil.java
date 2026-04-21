package com.example.provider.utill;

import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class TimeUtil {

    private static final String DEFAULT_PATTERN = "yyyy-MM-dd HH:mm:ss";
    private static final DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter.ofPattern(DEFAULT_PATTERN);

    public static String getCurrentTime() {
        return LocalDateTime.now().format(DEFAULT_FORMATTER);
    }

    public static String getCurrentTime(String pattern) {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern(pattern));
    }

    public static long getCurrentTimestamp() {
        return System.currentTimeMillis();
    }

    public static long getCurrentTimestampSeconds() {
        return System.currentTimeMillis() / 1000;
    }

    public static String format(Date date) {
        return format(date, DEFAULT_PATTERN);
    }

    public static String format(Date date, String pattern) {
        return new SimpleDateFormat(pattern).format(date);
    }

    public static String format(LocalDateTime dateTime) {
        return format(dateTime, DEFAULT_PATTERN);
    }

    public static String format(LocalDateTime dateTime, String pattern) {
        return dateTime.format(DateTimeFormatter.ofPattern(pattern));
    }

    public static Date parse(String dateStr) {
        return parse(dateStr, DEFAULT_PATTERN);
    }

    public static Date parse(String dateStr, String pattern) {
        try {
            System.out.println(dateStr);
            return new SimpleDateFormat(pattern).parse(dateStr);
        } catch (Exception e) {
            return null;
        }
    }

    public static LocalDateTime parseLocalDateTime(String dateStr) {
        return parseLocalDateTime(dateStr, DEFAULT_PATTERN);
    }

    public static LocalDateTime parseLocalDateTime(String dateStr, String pattern) {
        return LocalDateTime.parse(dateStr, DateTimeFormatter.ofPattern(pattern));
    }

    public static long convertToTimestamp(LocalDateTime dateTime) {
        return dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    public static LocalDateTime convertToLocalDateTime(long timestamp) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault());
    }

    public static String getToday() {
        return LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    public static String getYesterday() {
        return LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    public static String getTomorrow() {
        return LocalDate.now().plusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    public static int getYear() {
        return LocalDate.now().getYear();
    }

    public static int getMonth() {
        return LocalDate.now().getMonthValue();
    }

    public static int getDay() {
        return LocalDate.now().getDayOfMonth();
    }

    public static int getHour() {
        return LocalTime.now().getHour();
    }

    public static int getMinute() {
        return LocalTime.now().getMinute();
    }

    public static int getSecond() {
        return LocalTime.now().getSecond();
    }

    public static boolean isToday(LocalDateTime dateTime) {
        LocalDate today = LocalDate.now();
        LocalDate targetDate = dateTime.toLocalDate();
        return today.equals(targetDate);
    }

    public static boolean isSameDay(LocalDateTime date1, LocalDateTime date2) {
        return date1.toLocalDate().equals(date2.toLocalDate());
    }
}