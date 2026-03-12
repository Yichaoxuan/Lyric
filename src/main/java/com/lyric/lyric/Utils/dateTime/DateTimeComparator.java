package com.lyric.lyric.Utils.dateTime;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * 日期时间比较工具类
 * 处理日期时间的比较操作
 *
 * @author Yichaoxuan
 * @since 2026-03-12
 */
public class DateTimeComparator {
    
    /**
     * 比较两个 LocalDateTime 时间是否相同（精确到秒）
     * 该方法会忽略纳秒部分，只比较到秒级别
     *
     * @param dateTime1 第一个时间
     * @param dateTime2 第二个时间
     * @return true 表示两个时间相同，false 表示不同
     */
    public static boolean isSameTime(LocalDateTime dateTime1, LocalDateTime dateTime2) {
        if (dateTime1 == null || dateTime2 == null) {
            return false;
        }
        // 截断到秒级别进行比较
        return dateTime1.truncatedTo(ChronoUnit.SECONDS)
                .equals(dateTime2.truncatedTo(ChronoUnit.SECONDS));
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
        if (dateTime1 == null || dateTime2 == null) {
            return false;
        }
        // 截断到分钟级别进行比较
        return dateTime1.truncatedTo(ChronoUnit.MINUTES)
                .equals(dateTime2.truncatedTo(ChronoUnit.MINUTES));
    }
    
    /**
     * 比较两个 LocalDate 日期是否相同
     *
     * @param date1 第一个日期
     * @param date2 第二个日期
     * @return true 表示两个日期相同，false 表示不同
     */
    public static boolean isSameDate(LocalDate date1, LocalDate date2) {
        if (date1 == null || date2 == null) {
            return false;
        }
        return date1.equals(date2);
    }
}
