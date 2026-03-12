package com.lyric.lyric.Utils.dateTime;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * 日期时间工具类（门面类）
 * 提供常用的日期时间处理功能，作为对外的统一入口
 *
 * @author Yichaoxuan
 * @since 2026-03-12
 */
public class DateTimeUtils {

    // ==================== 类型转换 ====================

    /**
     * 将LocalDateTime转换为Date
     *
     * @param localDateTime LocalDateTime对象
     * @return Date对象
     */
    public static Date toDate(LocalDateTime localDateTime) {
        return DateTimeConverter.toDate(localDateTime);
    }

    /**
     * 将Date转换为LocalDateTime
     *
     * @param date Date对象
     * @return LocalDateTime对象，格式：yyyy-MM-dd HH:mm:ss
     */
    public static LocalDateTime toLocalDateTime(Date date) {
        return DateTimeConverter.toLocalDateTime(date);
    }

    /**
     * 将LocalDate转换为Date
     *
     * @param localDate LocalDate对象
     * @return Date对象
     */
    public static Date toDate(LocalDate localDate) {
        return DateTimeConverter.toDate(localDate);
    }

    /**
     * 将Date转换为LocalDate
     *
     * @param date Date对象
     * @return LocalDate对象，格式：yyyy-MM-dd
     */
    public static LocalDate toLocalDate(Date date) {
        return DateTimeConverter.toLocalDate(date);
    }

    /**
     * 将 LocalDateTime 转换为 LocalDate
     *
     * @param localDateTime LocalDateTime 对象
     * @return LocalDate 对象，格式：yyyy-MM-dd
     */
    public static LocalDate toLocalDate(LocalDateTime localDateTime) {
        return DateTimeConverter.toLocalDate(localDateTime);
    }

    /**
     * 将 LocalDate 转换为 LocalDateTime（时间部分设为 00:00:00）
     *
     * @param localDate LocalDate 对象
     * @return LocalDateTime 对象，时间部分为 00:00:00
     */
    public static LocalDateTime toLocalDateTime(LocalDate localDate) {
        return DateTimeConverter.toLocalDateTime(localDate);
    }

    // ==================== 格式化 ====================

    /**
     * 将 yyyy-MM-dd HH:mm:ss 格式的日期时间字符串转换为 yyyy-MM-dd 格式的日期字符串
     *
     * @param dateTimeStr 日期时间字符串，格式：yyyy-MM-dd HH:mm:ss
     * @return 日期字符串，格式：yyyy-MM-dd
     */
    public static String formatDateTimeToDate(String dateTimeStr) {
        return DateTimeFormatter.formatDateTimeToDate(dateTimeStr);
    }

    /**
     * 获取 LocalDateTime 的指定时间部分并格式化为字符串
     *
     * @param dateTime LocalDateTime 对象
     * @param pattern  日期时间格式模式，如 "yyyy"、"MM"、"dd"、"HH"、"mm"、"ss" 等
     * @return 格式化后的时间字符串,格式化后的字符串，格式由pattern参数决定
     */
    public static String format(LocalDateTime dateTime, String pattern) {
        return DateTimeFormatter.format(dateTime, pattern);
    }

    /**
     * 格式化LocalDateTime为字符串（格式：yyyy-MM-dd HH:mm:ss）
     *
     * @param dateTime LocalDateTime对象
     * @return 格式化后的字符串，格式：yyyy-MM-dd HH:mm:ss
     */
    public static String format(LocalDateTime dateTime) {
        return DateTimeFormatter.format(dateTime);
    }

    /**
     * 格式化LocalDate为字符串
     *
     * @param date    LocalDate对象
     * @param pattern 日期格式模式
     * @return 格式化后的字符串，格式由pattern参数决定
     */
    public static String format(LocalDate date, String pattern) {
        return DateTimeFormatter.format(date, pattern);
    }

    /**
     * 格式化LocalDate为字符串（格式：yyyy-MM-dd）
     *
     * @param date LocalDate对象
     * @return 格式化后的字符串，格式：yyyy-MM-dd
     */
    public static String format(LocalDate date) {
        return DateTimeFormatter.format(date);
    }

    // ==================== 解析 ====================

    /**
     * 解析字符串为LocalDateTime
     *
     * @param dateTimeStr 日期时间字符串
     * @param pattern     日期时间格式模式
     * @return LocalDateTime对象，格式由pattern参数决定
     */
    public static LocalDateTime parseDateTime(String dateTimeStr, String pattern) {
        return DateTimeParser.parseDateTime(dateTimeStr, pattern);
    }

    /**
     * 解析字符串为 LocalDateTime（格式：yyyy-MM-dd HH:mm:ss）
     *
     * @param dateTimeStr 日期时间字符串，格式：yyyy-MM-dd HH:mm:ss
     * @return LocalDateTime 对象，格式：yyyy-MM-dd HH:mm:ss
     */
    public static LocalDateTime parseDateTime(String dateTimeStr) {
        return DateTimeParser.parseDateTime(dateTimeStr);
    }

    /**
     * 解析带时区偏移的 ISO 8601 格式字符串为 LocalDateTime
     * 支持的格式：yyyy-MM-dd'T'HH:mm+HH:mm、yyyy-MM-dd'T'HH:mm-HH:mm 等
     * 例如："2026-03-11T01:00+08:00" 会转换为 "2026-03-11 01:00:00"
     *
     * @param dateTimeStr 带时区偏移的日期时间字符串
     * @return LocalDateTime 对象，格式：yyyy-MM-dd HH:mm:ss
     */
    public static LocalDateTime parseDateTimeWithOffset(String dateTimeStr) {
        return DateTimeParser.parseDateTimeWithOffset(dateTimeStr);
    }

    /**
     * 解析字符串为LocalDate
     *
     * @param dateStr 日期字符串
     * @param pattern 日期格式模式
     * @return LocalDate对象，格式由pattern参数决定
     */
    public static LocalDate parseDate(String dateStr, String pattern) {
        return DateTimeParser.parseDate(dateStr, pattern);
    }

    /**
     * 解析字符串为LocalDate（格式：yyyy-MM-dd）
     *
     * @param dateStr 日期字符串，格式：yyyy-MM-dd
     * @return LocalDate对象，格式：yyyy-MM-dd
     */
    public static LocalDate parseDate(String dateStr) {
        return DateTimeParser.parseDate(dateStr);
    }

    // ==================== 计算 ====================

    /**
     * 计算两个日期之间的天数差
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 天数差
     */
    public static long daysBetween(LocalDate startDate, LocalDate endDate) {
        return DateTimeCalculator.daysBetween(startDate, endDate);
    }

    /**
     * 计算两个日期时间之间的小时差
     *
     * @param startDateTime 开始日期时间
     * @param endDateTime   结束日期时间
     * @return 小时差
     */
    public static long hoursBetween(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return DateTimeCalculator.hoursBetween(startDateTime, endDateTime);
    }

    /**
     * 计算两个日期时间之间的分钟差
     *
     * @param startDateTime 开始日期时间
     * @param endDateTime   结束日期时间
     * @return 分钟差
     */
    public static long minutesBetween(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return DateTimeCalculator.minutesBetween(startDateTime, endDateTime);
    }

    /**
     * 计算两个日期时间之间的秒数差
     *
     * @param startDateTime 开始日期时间
     * @param endDateTime   结束日期时间
     * @return 秒数差
     */
    public static Integer timeBetween(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return DateTimeCalculator.timeBetween(startDateTime, endDateTime);
    }

    // ==================== 获取当前时间 ====================

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

    // ==================== 操作 ====================

    /**
     * 在指定日期时间基础上增加天数
     *
     * @param dateTime 原始日期时间
     * @param days     要增加的天数
     * @return 增加天数后的日期时间，格式：yyyy-MM-dd HH:mm:ss
     */
    public static LocalDateTime plusDays(LocalDateTime dateTime, long days) {
        return DateTimeManipulator.plusDays(dateTime, days);
    }

    /**
     * 在指定日期基础上增加天数
     *
     * @param date 原始日期
     * @param days 要增加的天数
     * @return 增加天数后的日期，格式：yyyy-MM-dd
     */
    public static LocalDate plusDays(LocalDate date, long days) {
        return DateTimeManipulator.plusDays(date, days);
    }

    /**
     * 在指定日期时间基础上减少天数
     *
     * @param dateTime 原始日期时间
     * @param days     要减少的天数
     * @return 减少天数后的日期时间，格式：yyyy-MM-dd HH:mm:ss
     */
    public static LocalDateTime minusDays(LocalDateTime dateTime, long days) {
        return DateTimeManipulator.minusDays(dateTime, days);
    }

    /**
     * 在指定日期基础上减少天数
     *
     * @param date 原始日期
     * @param days 要减少的天数
     * @return 减少天数后的日期，格式：yyyy-MM-dd
     */
    public static LocalDate minusDays(LocalDate date, long days) {
        return DateTimeManipulator.minusDays(date, days);
    }

    // ==================== 比较 ====================

    /**
     * 比较两个 LocalDateTime 时间是否相同（精确到秒）
     * 该方法会忽略纳秒部分，只比较到秒级别
     *
     * @param dateTime1 第一个时间
     * @param dateTime2 第二个时间
     * @return true 表示两个时间相同，false 表示不同
     */
    public static boolean isSameTime(LocalDateTime dateTime1, LocalDateTime dateTime2) {
        return DateTimeComparator.isSameTime(dateTime1, dateTime2);
    }

    /**
     * 比较两个 LocalDateTime 时间是否相同（精确到分钟）
     * 该方法会忽略秒和纳秒部分，只比较到分钟级别
     *
     * @param dateTime1 第一个时间
     * @param dateTime2 第二个时间
     * @return true 表示两个时间相同，false 表示不同
     */
    public static boolean isSameMinute(LocalDateTime dateTime1, LocalDateTime dateTime2) {
        return DateTimeComparator.isSameMinute(dateTime1, dateTime2);
    }

    /**
     * 比较两个 LocalDate 日期是否相同
     *
     * @param date1 第一个日期
     * @param date2 第二个日期
     * @return true 表示两个日期相同，false 表示不同
     */
    public static boolean isSameDate(LocalDate date1, LocalDate date2) {
        return DateTimeComparator.isSameDate(date1, date2);
    }
}
