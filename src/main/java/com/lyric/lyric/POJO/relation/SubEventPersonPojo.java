package com.lyric.lyric.POJO.relation;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 子事件-人物关联实体类
 * 对应数据库表: sub_event_person
 *
 * @author Yichaoxuan
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubEventPersonPojo {
    
    /**
     * 主键ID
     */
    private Integer id;
    
    /**
     * 子事件ID
     */
    private Integer togEventId;
    
    /**
     * 人物ID
     */
    private Integer personId;
    
    /**
     * 人物在事件中的角色
     */
    private String role;
    
    /**
     * 有参构造方法（不包含自动生成的字段）
     * @param togEventId 子事件ID
     * @param personId 人物ID
     * @param role 角色
     */
    public SubEventPersonPojo(Integer togEventId, Integer personId, String role) {
        this.togEventId = togEventId;
        this.personId = personId;
        this.role = role;
    }
}