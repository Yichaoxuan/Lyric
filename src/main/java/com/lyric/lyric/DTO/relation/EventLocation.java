package com.lyric.lyric.DTO.relation;

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
     * 有参构造方法
     * @param id 主键ID
     * @param eventId 事件ID
     * @param locationId 地点ID
     */
    public EventLocation(Integer id, Integer eventId, Integer locationId) {
        this.id = id;
        this.eventId = eventId;
        this.locationId = locationId;
    }
}