package com.lyric.lyric.Utils.dateTime;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * 日期时间解析工具类
 * 处理字符串到日期时间对象的转换
 *
 * @author Yichaoxuan
 * @since 2026-03-12
 */
public class DateTimeParser {

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
     * 解析字符串为 LocalDateTime（格式：yyyy-MM-dd HH:mm:ss）
     *
     * @param dateTimeStr 日期时间字符串，格式：yyyy-MM-dd HH:mm:ss
     * @return LocalDateTime 对象，格式：yyyy-MM-dd HH:mm:ss
     */
    public static LocalDateTime parseDateTime(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.isEmpty()) {
            return null;
        }
        return LocalDateTime.parse(dateTimeStr, DateTimeConstants.DATE_TIME_FORMATTER);
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
        if (dateTimeStr == null || dateTimeStr.isEmpty()) {
            return null;
        }
            
        try {
            // 使用 OffsetDateTime 解析带时区偏移的字符串
            OffsetDateTime offsetDateTime = OffsetDateTime.parse(dateTimeStr);
            // 转换为系统默认时区的 LocalDateTime
            return offsetDateTime.atZoneSameInstant(ZoneId.systemDefault()).toLocalDateTime();
        } catch (Exception e) {
            // 如果解析失败，尝试其他常见格式
            return parseDateTime(dateTimeStr);
        }
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
        return LocalDate.parse(dateStr, DateTimeConstants.DATE_FORMATTER);
    }
}
