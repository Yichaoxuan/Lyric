package com.lyric.lyric.POJO.relation;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 子事件-地点关联实体类
 * 对应数据库表: sub_event_location
 *
 * @author Yichaoxuan
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubEventLocationPojo {
    
    /**
     * 主键ID
     */
    private Integer id;
    
    /**
     * 子事件ID
     */
    private Integer eventId;
    
    /**
     * 地点ID
     */
    private Integer locationId;
    
    /**
     * 有参构造方法（不包含自动生成的字段）
     * @param eventId 子事件ID
     * @param locationId 地点ID
     */
    public SubEventLocationPojo(Integer eventId, Integer locationId) {
        this.eventId = eventId;
        this.locationId = locationId;
    }
}