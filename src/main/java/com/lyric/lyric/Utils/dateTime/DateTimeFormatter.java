package com.lyric.lyric.Utils.dateTime;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 日期时间格式化工具类
 * 处理日期时间对象到字符串的转换
 *
 * @author Yichaoxuan
 * @since 2026-03-12
 */
public class DateTimeFormatter {

    /**
     * 将 yyyy-MM-dd HH:mm:ss 格式的日期时间字符串转换为 yyyy-MM-dd 格式的日期字符串
     *
     * @param dateTimeStr 日期时间字符串，格式：yyyy-MM-dd HH:mm:ss
     * @return 日期字符串，格式：yyyy-MM-dd
     */
    public static String formatDateTimeToDate(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.isEmpty()) {
            return null;
        }
        LocalDateTime localDateTime = DateTimeParser.parseDateTime(dateTimeStr);
        if (localDateTime == null) {
            return null;
        }
        return format(localDateTime.toLocalDate());
    }

    /**
     * 获取 LocalDateTime 的指定时间部分并格式化为字符串
     *
     * @param dateTime LocalDateTime 对象
     * @param pattern  日期时间格式模式，如 "yyyy"、"MM"、"dd"、"HH"、"mm"、"ss" 等
     * @return 格式化后的时间字符串,格式化后的字符串，格式由pattern参数决定
     */
    public static String format(LocalDateTime dateTime, String pattern) {
        if (dateTime == null || pattern == null || pattern.isEmpty()) {
            return null;
        }
        return dateTime.format(java.time.format.DateTimeFormatter.ofPattern(pattern));
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
        return dateTime.format(DateTimeConstants.DATE_TIME_FORMATTER);
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
        return date.format(java.time.format.DateTimeFormatter.ofPattern(pattern));
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
        return date.format(DateTimeConstants.DATE_FORMATTER);
    }
}
