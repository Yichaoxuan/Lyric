package com.lyric.lyric.POJO.tag.entityTag;

import com.lyric.lyric.POJO.AI.AITagJson;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * 人物实体类
 * 对应数据库表: person
 *
 * @author Yichaoxuan
 */
@Data
@NoArgsConstructor
public class PersonPojo {

    /**
     * 主键ID
     */
    private Integer id;

    /**
     * 人物名称
     */
    private String name;

    /**
     * 人物别称
     */
    private String alias;

    /**
     * 与用户的关系
     */
    private String relation;

    /**
     *  性别
     *  0: 未知
     *  1: 男
     *  2: 女
     */
    private Integer gender;

    /**
     * 性格总结
     */
    private String personality;

    /**
     * 颜色代码
     */
    private String color;

    /**
     * 首次出现时间
     */
    private LocalDateTime firstAppearance;

    /**
     * 最后一次出现时间
     */
    private LocalDateTime lastAppearance;

    /**
     * 出现次数
     */
    private Integer appearanceCount;

    /**
     * 重要性
     */
    private ImportanceLevel importance;

    /**
     * 由AITagJson.personInfo 转换为PersonPojo对象
     *
     * @param name 人物名称
     * @param persons AITagJson.PersonInfo对象
     */
    public PersonPojo(String name, AITagJson.PersonInfo persons) {
        this.name = name;
        this.relation = persons.getRelationship();
        this.gender = genderName(persons.getGender());
        this.personality = persons.getPersonality();
        this.color = persons.getColor();
        this.firstAppearance = null;
        this.lastAppearance = null;
        this.appearanceCount = 1;
        this.importance = null;
    }

    /**
     * 将性别字符串转换为对应的编码
     *
     * @param gender 性别字符串，"男"、"女"或其他
     * @return Integer 性别编码，1表示男性，2表示女性，0表示未知
     */
    public static Integer genderName(String gender) {
        return switch (gender) {
            case "男" -> 1;
            case "女" -> 2;
            default -> 0;
        };
    }

    /**
     * 获取性别字符串表示
     *
     * @return String 性别字符串，"男"、"女"或"未知"
     */
    public String getGenderName() {
        return switch (this.gender) {
            case 1 -> "男";
            case 2 -> "女";
            default -> "未知";
        };
    }

    /**
     * 将性别字符串转换为对应的编码
     * 
     * @param gender 性别字符串，"男"、"女"或其他
     */
    public void setGender(String gender) {
        switch (gender) {
            case "男":
                this.gender = 1;
                break;
            case "女":
                this.gender = 2;
                break;
            default:
                this.gender = 0;
        }
    }

    /**
     * 设置性别（整数形式）
     * 
     * @param gender 性别编码，1表示男性，2表示女性，0表示未知
     */
    public void setGender(Integer gender) {
        this.gender = gender;
    }

    /**
     * 重要性等级枚举
     */
    @Getter
    public enum ImportanceLevel {
        /**
         * 高重要性
         */
        HIGH,

        /**
         * 中等重要性
         */
        MEDIUM,

        /**
         * 低重要性
         */
        LOW
    }
}