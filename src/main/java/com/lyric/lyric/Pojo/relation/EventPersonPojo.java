package com.lyric.lyric.Pojo.relation;

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
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class EventPersonPojo {

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
     * 人物在事件中的角色
     */
    private String role;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 有参构造方法（不包含自动生成的字段）
     * @param id 主键ID
     * @param eventId 事件ID
     * @param personId 人物ID
     * @param role 人物在事件中的角色
     */
    public EventPersonPojo(Integer id, Integer eventId, Integer personId, String role) {
        this.id = id;
        this.eventId = eventId;
        this.personId = personId;
        this.role = role;
    }
}