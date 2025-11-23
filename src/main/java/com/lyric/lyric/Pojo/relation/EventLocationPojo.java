package com.lyric.lyric.Pojo.relation;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * 事件-地点关联实体类
 * 对应数据库表: event_location
 * 记录事件发生的地点
 *
 * @author Lyric
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EventLocationPojo {

    /**
     * 主键ID
     */
    private Integer id;

    /**
     * 事件ID
     */
    private Integer eventId;

    /**
     * 地点ID
     */
    private Integer locationId;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 有参构造方法（不包含自动生成的字段）
     * @param id 主键ID
     * @param eventId 事件ID
     * @param locationId 地点ID
     */
    public EventLocationPojo(Integer id, Integer eventId, Integer locationId) {
        this.id = id;
        this.eventId = eventId;
        this.locationId = locationId;
    }
}