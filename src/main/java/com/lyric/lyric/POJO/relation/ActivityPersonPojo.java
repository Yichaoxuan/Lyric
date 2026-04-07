package com.lyric.lyric.POJO.relation;

import com.lyric.lyric.POJO.AI.AITagJson;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 活动-人物关联实体类
 * <p>
 * 用于表示活动中参与的人物及其角色和提及类型信息。
 * </p>
 *
 * @author Yichaoxuan
 * @since 2026-04-05
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ActivityPersonPojo {
    /**
     * 关联记录ID
     */
    private Integer id;

    /**
     * 活动ID
     */
    private Integer activityId;

    /**
     * 人物ID
     */
    private Integer personId;

    /**
     * 人物在活动中的角色
     */
    private String role;

    /**
     * 提及类型（实际出现/提及/回忆）
     */
    private AITagJson.MentionType mentionType;

    /**
     * 构造函数（不含ID，用于创建新记录）
     *
     * @param activityId 活动ID
     * @param personId 人物ID
     * @param role 人物在活动中的角色
     * @param mentionType 提及类型
     */
    public ActivityPersonPojo(Integer activityId, Integer personId, String role, AITagJson.MentionType mentionType) {
        this.activityId = activityId;
        this.personId = personId;
        this.role = role;
        this.mentionType = mentionType;
    }
}
