package com.lyric.lyric.Utils.dateTime;

import java.time.*;
import java.util.Date;

/**
 * 日期时间类型转换工具类
 * 处理不同日期时间类型之间的转换
 *
 * @author Yichaoxuan
 * @since 2026-03-12
 */
public class DateTimeConverter {
    
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
     * 将 LocalDateTime 转换为 LocalDate
     *
     * @param localDateTime LocalDateTime 对象
     * @return LocalDate 对象，格式：yyyy-MM-dd
     */
    public static LocalDate toLocalDate(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return localDateTime.toLocalDate();
    }
    
    /**
     * 将 LocalDate 转换为 LocalDateTime（时间部分设为 00:00:00）
     *
     * @param localDate LocalDate 对象
     * @return LocalDateTime 对象，时间部分为 00:00:00
     */
    public static LocalDateTime toLocalDateTime(LocalDate localDate) {
        if (localDate == null) {
            return null;
        }
        return localDate.atStartOfDay();
    }
}
