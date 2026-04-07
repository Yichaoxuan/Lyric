package com.lyric.lyric.POJO.relation;

import com.lyric.lyric.POJO.AI.AITagJson;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 活动-地点 关联实体类
 *
 * @author Yichaoxuan
 * @since 2026-04-02
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ActivityLocationPojo {

    /**
     * 主键
     */
    private Integer id;

    /**
     * 活动 ID
     */
    private Integer activityId;

    /**
     * 地点 ID
     */
    private Integer locationId;

    /**
     * 提及类型
     */
    private AITagJson.MentionType mentionType;

    /**
     * 构造函数
     *
     * @param activityId 活动 ID
     * @param locationId 地点 ID
     * @param mentionType 提及类型
     */
    public ActivityLocationPojo(Integer activityId, Integer locationId, AITagJson.MentionType mentionType) {
        this.activityId = activityId;
        this.locationId = locationId;
        this.mentionType = mentionType;
    }
}
