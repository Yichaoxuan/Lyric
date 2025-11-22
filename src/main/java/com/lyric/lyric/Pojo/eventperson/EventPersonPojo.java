package com.lyric.lyric.Pojo.eventperson;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * 事件-人物关联实体类
 * 对应数据库表: event_person
 * 记录人物在事件中的参与情况
 *
 * @author Lyric
 * @since 2025-11-21
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EventPersonPojo {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 事件ID
     */
    private Long eventId;

    /**
     * 人物ID
     */
    private Long personId;

    /**
     * 人物在事件中的角色
     */
    private String role;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 有参构造方法（不包含自动生成的字段）
     * @param eventId 事件ID
     * @param personId 人物ID
     * @param role 人物在事件中的角色
     */
    public EventPersonPojo(Long eventId, Long personId, String role) {
        this.eventId = eventId;
        this.personId = personId;
        this.role = role;
    }
}