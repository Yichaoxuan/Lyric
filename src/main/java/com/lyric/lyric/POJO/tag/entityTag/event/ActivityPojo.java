package com.lyric.lyric.POJO.tag.entityTag.event;

import com.lyric.lyric.POJO.AI.AITagJson;
import com.lyric.lyric.Utils.dateTime.DateTimeUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

/**
 * 活动实体类
 * 对应数据库表: activity
 *
 * @author Yichaoxuan
 */
@Slf4j
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ActivityPojo {

    /**
     * 主键ID
     */
    private Integer id;

    /**
     * 所属事件ID
     */
    private Integer eventId;

    /**
     * 活动名称
     */
    private String name;

    /**
     * 活动发生日期时间
     */
    private LocalDateTime activityDate;

    /**
     * 活动发生的时间段（上午/中午/下午/晚上/深夜）
     */
    private TimePeriod timePeriod;

    /**
     * 活动描述
     */
    private String description;

    /**
     * 活动重要性
     */
    private ImportanceLevel importance;

    /**
     * 颜色代码
     */
    private String color;

    /**
     * 由AITagJson.ActivityInfo转换为ActivityPojo对象
     * 注意：此方法不设置eventId，因为活动的事件归属由后端业务逻辑决定
     *
     * @param name         活动名称
     * @param activityInfo AI返回的活动信息
     */
    public ActivityPojo(String name, AITagJson.ActivityInfo activityInfo) {
        this.name = name;
        this.activityDate = DateTimeUtils.parseDateTime(activityInfo.getDate());
        this.timePeriod = parseTimePeriod(activityInfo.getTimePeriod());
        this.description = activityInfo.getDescription();
        this.importance = ImportanceLevel.MEDIUM;
        this.color = activityInfo.getColor();
    }

    /**
     * 解析时间段
     *
     * @param timePeriod 时间段
     * @return 时间段枚举
     */
    private TimePeriod parseTimePeriod(String timePeriod) {
        if (timePeriod == null || timePeriod.isEmpty()) {
            return TimePeriod.NOON;
        }
        try {
            return TimePeriod.valueOf(timePeriod.toUpperCase());
        } catch (IllegalArgumentException e) {
            log.warn("无法解析时间段: {}", timePeriod);
            return TimePeriod.NOON;
        }
    }

    /**
     * 时间段枚举
     */
    public enum TimePeriod {
        EARLY_MORNING,     // 凌晨
        MORNING,   // 上午
        NOON,      // 中午
        AFTERNOON, // 下午
        EVENING   // 晚上

    }

    /**
     * 重要性等级枚举
     */
    public enum ImportanceLevel {
        HIGH,
        MEDIUM,
        LOW
    }
}