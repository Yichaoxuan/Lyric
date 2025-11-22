package com.lyric.lyric.Dto.relation;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 事件-地点关联请求DTO类
 * 只包含前端可信字段
 *
 * @author Lyric
 */
@Getter
@Setter
@NoArgsConstructor
public class EventLocation {

    /**
     * 事件ID
     */
    private Long eventId;

    /**
     * 地点ID
     */
    private Long locationId;
    /**
     * 有参构造方法
     * @param eventId 事件ID
     * @param locationId 地点ID
     */
    public EventLocation(Long eventId, Long locationId) {
        this.eventId = eventId;
        this.locationId = locationId;
    }
}