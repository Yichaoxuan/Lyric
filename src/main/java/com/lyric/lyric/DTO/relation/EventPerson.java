package com.lyric.lyric.DTO.relation;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 事件-人物关联请求DTO类
 * 只包含前端可信字段
 *
 * @author Lyric
 */
@Getter
@Setter
@NoArgsConstructor
public class EventPerson {

    /**
     * 主键ID
     */
    private Integer id;

    /**
     * 事件ID
     */
    private Integer eventId;

    /**
     * 人物ID
     */
    private Integer personId;

    /**
     * 角色
     */
    private String role;
    
    /**
     * 有参构造方法
     * @param id 主键ID
     * @param eventId 事件ID
     * @param personId 人物ID
     * @param role 角色
     */
    public EventPerson(Integer id, Integer eventId, Integer personId, String role) {
        this.id = id;
        this.eventId = eventId;
        this.personId = personId;
        this.role = role;
    }
}