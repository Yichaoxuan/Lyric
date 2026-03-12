package com.lyric.lyric.Utils.dateTime;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * 日期时间计算工具类
 * 处理日期时间之间的计算，如天数差、小时差等
 *
 * @author Yichaoxuan
 * @since 2026-03-12
 */
public class DateTimeCalculator {
    
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
}
