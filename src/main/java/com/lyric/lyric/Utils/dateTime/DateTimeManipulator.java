package com.lyric.lyric.Utils.dateTime;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 日期时间操作工具类
 * 处理日期时间的操作，如增加天数、减少天数等
 *
 * @author Yichaoxuan
 * @since 2026-03-12
 */
public class DateTimeManipulator {
    
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
