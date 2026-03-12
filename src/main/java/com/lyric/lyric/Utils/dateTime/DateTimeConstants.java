package com.lyric.lyric.Utils.dateTime;

import java.time.format.DateTimeFormatter;

/**
 * 日期时间常量类
 * 定义常用的日期时间格式和格式化器
 *
 * @author Yichaoxuan
 * @since 2026-03-12
 */
public class DateTimeConstants {
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
}
