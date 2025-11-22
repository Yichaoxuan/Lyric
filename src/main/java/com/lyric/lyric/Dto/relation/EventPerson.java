package com.lyric.lyric.Dto.relation;

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
     * 事件ID
     */
    private Long eventId;

    /**
     * 人物ID
     */
    private Long personId;

    /**
     * 角色
     */
    private String role;
    /**
     * 有参构造方法
     * @param eventId 事件ID
     * @param personId 人物ID
     * @param role 角色
     */
    public EventPerson(Long eventId, Long personId, String role) {
        this.eventId = eventId;
        this.personId = personId;
        this.role = role;
    }
}