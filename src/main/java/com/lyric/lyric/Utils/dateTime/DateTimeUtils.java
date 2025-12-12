package com.lyric.lyric.Utils.dateTime;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;

/**
 * 日期时间工具类
 * 提供常用的日期时间处理功能，包括格式转换、时间差计算等
 *
 * @author Lyric
 * @since 2025-11-21
 */
public class DateTimeUtils {
    
    // 常用日期时间格式
    public static final String DATE_PATTERN = "yyyy-MM-dd";
    public static final String TIME_PATTERN = "HH:mm:ss";
    public static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_TIME_MS_PATTERN = "yyyy-MM-dd HH:mm:ss.SSS";
    
    // 常用DateTimeFormatter实例
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DATE_PATTERN);
    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern(TIME_PATTERN);
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);
    public static final DateTimeFormatter DATE_TIME_MS_FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_MS_PATTERN);
    
    /**
     * 将LocalDateTime转换为Date
     *
     * @param localDateTime LocalDateTime对象
     * @return Date对象
     */
    public static Date toDate(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }
    
    /**
     * 将Date转换为LocalDateTime
     *
     * @param date Date对象
     * @return LocalDateTime对象，格式：yyyy-MM-dd HH:mm:ss
     */
    public static LocalDateTime toLocalDateTime(Date date) {
        if (date == null) {
            return null;
        }
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
    
    /**
     * 将LocalDate转换为Date
     *
     * @param localDate LocalDate对象
     * @return Date对象
     */
    public static Date toDate(LocalDate localDate) {
        if (localDate == null) {
            return null;
        }
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }
    
    /**
     * 将Date转换为LocalDate
     *
     * @param date Date对象
     * @return LocalDate对象，格式：yyyy-MM-dd
     */
    public static LocalDate toLocalDate(Date date) {
        if (date == null) {
            return null;
        }
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }
    
    /**
     * 格式化LocalDateTime为字符串
     *
     * @param dateTime LocalDateTime对象
     * @param pattern 日期时间格式模式
     * @return 格式化后的字符串，格式由pattern参数决定
     */
    public static String format(LocalDateTime dateTime, String pattern) {
        if (dateTime == null || pattern == null || pattern.isEmpty()) {
            return null;
        }
        return dateTime.format(DateTimeFormatter.ofPattern(pattern));
    }
    
    /**
     * 格式化LocalDateTime为字符串（格式：yyyy-MM-dd HH:mm:ss）
     *
     * @param dateTime LocalDateTime对象
     * @return 格式化后的字符串，格式：yyyy-MM-dd HH:mm:ss
     */
    public static String format(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.format(DATE_TIME_FORMATTER);
    }
    
    /**
     * 格式化LocalDate为字符串
     *
     * @param date    LocalDate对象
     * @param pattern 日期格式模式
     * @return 格式化后的字符串，格式由pattern参数决定
     */
    public static String format(LocalDate date, String pattern) {
        if (date == null || pattern == null || pattern.isEmpty()) {
            return null;
        }
        return date.format(DateTimeFormatter.ofPattern(pattern));
    }
    
    /**
     * 格式化LocalDate为字符串（格式：yyyy-MM-dd）
     *
     * @param date LocalDate对象
     * @return 格式化后的字符串，格式：yyyy-MM-dd
     */
    public static String format(LocalDate date) {
        if (date == null) {
            return null;
        }
        return date.format(DATE_FORMATTER);
    }
    
    /**
     * 解析字符串为LocalDateTime
     *
     * @param dateTimeStr 日期时间字符串
     * @param pattern     日期时间格式模式
     * @return LocalDateTime对象，格式由pattern参数决定
     */
    public static LocalDateTime parseDateTime(String dateTimeStr, String pattern) {
        if (dateTimeStr == null || dateTimeStr.isEmpty() || pattern == null || pattern.isEmpty()) {
            return null;
        }
        return LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ofPattern(pattern));
    }
    
    /**
     * 解析字符串为LocalDateTime（格式：yyyy-MM-dd HH:mm:ss）
     *
     * @param dateTimeStr 日期时间字符串，格式：yyyy-MM-dd HH:mm:ss
     * @return LocalDateTime对象，格式：yyyy-MM-dd HH:mm:ss
     */
    public static LocalDateTime parseDateTime(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.isEmpty()) {
            return null;
        }
        return LocalDateTime.parse(dateTimeStr, DATE_TIME_FORMATTER);
    }
    
    /**
     * 解析字符串为LocalDate
     *
     * @param dateStr 日期字符串
     * @param pattern 日期格式模式
     * @return LocalDate对象，格式由pattern参数决定
     */
    public static LocalDate parseDate(String dateStr, String pattern) {
        if (dateStr == null || dateStr.isEmpty() || pattern == null || pattern.isEmpty()) {
            return null;
        }
        return LocalDate.parse(dateStr, DateTimeFormatter.ofPattern(pattern));
    }
    
    /**
     * 解析字符串为LocalDate（格式：yyyy-MM-dd）
     *
     * @param dateStr 日期字符串，格式：yyyy-MM-dd
     * @return LocalDate对象，格式：yyyy-MM-dd
     */
    public static LocalDate parseDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) {
            return null;
        }
        return LocalDate.parse(dateStr, DATE_FORMATTER);
    }
    
    /**
     * 计算两个日期之间的天数差
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 天数差
     */
    public static long daysBetween(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            return 0;
        }
        return ChronoUnit.DAYS.between(startDate, endDate);
    }
    
    /**
     * 计算两个日期时间之间的小时差
     *
     * @param startDateTime 开始日期时间
     * @param endDateTime   结束日期时间
     * @return 小时差
     */
    public static long hoursBetween(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        if (startDateTime == null || endDateTime == null) {
            return 0;
        }
        return ChronoUnit.HOURS.between(startDateTime, endDateTime);
    }
    
    /**
     * 计算两个日期时间之间的分钟差
     *
     * @param startDateTime 开始日期时间
     * @param endDateTime   结束日期时间
     * @return 分钟差
     */
    public static long minutesBetween(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        if (startDateTime == null || endDateTime == null) {
            return 0;
        }
        return ChronoUnit.MINUTES.between(startDateTime, endDateTime);
    }
    
    /**
     * 计算两个日期时间之间的秒数差
     *
     * @param startDateTime 开始日期时间
     * @param endDateTime   结束日期时间
     * @return 秒数差
     */
    public static Integer timeBetween(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        if (startDateTime == null || endDateTime == null) {
            return 0;
        }
        long seconds = ChronoUnit.SECONDS.between(startDateTime, endDateTime);
        return (int) seconds;
    }

    /**
     * 获取当前日期时间
     *
     * @return 当前日期时间，格式：yyyy-MM-dd HH:mm:ss
     */
    public static LocalDateTime now() {
        return LocalDateTime.now();
    }
    
    /**
     * 获取当前日期
     *
     * @return 当前日期，格式：yyyy-MM-dd
     */
    public static LocalDate today() {
        return LocalDate.now();
    }
    
    /**
     * 在指定日期时间基础上增加天数
     *
     * @param dateTime 原始日期时间
     * @param days     要增加的天数
     * @return 增加天数后的日期时间，格式：yyyy-MM-dd HH:mm:ss
     */
    public static LocalDateTime plusDays(LocalDateTime dateTime, long days) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.plusDays(days);
    }
    
    /**
     * 在指定日期基础上增加天数
     *
     * @param date 原始日期
     * @param days 要增加的天数
     * @return 增加天数后的日期，格式：yyyy-MM-dd
     */
    public static LocalDate plusDays(LocalDate date, long days) {
        if (date == null) {
            return null;
        }
        return date.plusDays(days);
    }
    
    /**
     * 在指定日期时间基础上减少天数
     *
     * @param dateTime 原始日期时间
     * @param days     要减少的天数
     * @return 减少天数后的日期时间，格式：yyyy-MM-dd HH:mm:ss
     */
    public static LocalDateTime minusDays(LocalDateTime dateTime, long days) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.minusDays(days);
    }
    
    /**
     * 在指定日期基础上减少天数
     *
     * @param date 原始日期
     * @param days 要减少的天数
     * @return 减少天数后的日期，格式：yyyy-MM-dd
     */
    public static LocalDate minusDays(LocalDate date, long days) {
        if (date == null) {
            return null;
        }
        return date.minusDays(days);
    }
}