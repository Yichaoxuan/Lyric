package com.lyric.lyric.Enums.message;

import lombok.Getter;
import lombok.Setter;

/**
 * 成功消息枚举类
 * 用于定义系统中各种操作成功情况的提示消息
 */
@Getter
public enum SuccessMsgEnums {

    //日记模块相关
    SAVE_SUCCESS("保存成功"),
    DELETE_SUCCESS("删除成功"),
    MODIFY_SUCCESS("修改成功"),
    QUERY_SUCCESS("查询成功"),
    MOVE_TO_TRASH_SUCCESS("已移入回收站"),
    RESTORE_FROM_TRASH_SUCCESS("已从回收站恢复"),

    // 标签模块相关
    // BaseTag（基本标签）相关
    BASE_TAG_CREATE_SUCCESS("基本标签创建成功"),
    BASE_TAG_QUERY_SUCCESS("基本标签查询成功"),
    BASE_TAG_QUERY_BY_TYPE_SUCCESS("按类型查询基本标签成功"),
    BASE_TAG_UPDATE_SUCCESS("基本标签更新成功"),
    BASE_TAG_DELETE_SUCCESS("基本标签删除成功"),
    BASE_TAG_USAGE_INCREMENT_SUCCESS("基本标签使用次数增加成功"),

    // PersonTag（人物标签）相关
    PERSON_TAG_CREATE_SUCCESS("人物标签创建成功"),
    PERSON_TAG_QUERY_SUCCESS("人物标签查询成功"),
    PERSON_TAG_QUERY_BY_NAME_SUCCESS("按名称查询人物标签成功"),
    PERSON_TAG_QUERY_BY_GENDER_SUCCESS("按性别查询人物标签成功"),
    PERSON_TAG_QUERY_BY_RELATION_SUCCESS("按关系查询人物标签成功"),
    PERSON_TAG_UPDATE_SUCCESS("人物标签更新成功"),
    PERSON_TAG_DELETE_SUCCESS("人物标签删除成功"),
    PERSON_TAG_APPEARANCE_INCREMENT_SUCCESS("人物出现次数增加成功"),

    // LocationTag（地点标签）相关
    LOCATION_TAG_CREATE_SUCCESS("地点标签创建成功"),
    LOCATION_TAG_QUERY_SUCCESS("地点标签查询成功"),
    LOCATION_TAG_QUERY_BY_NAME_SUCCESS("按名称查询地点标签成功"),
    LOCATION_TAG_QUERY_BY_ALIAS_SUCCESS("按别名查询地点标签成功"),
    LOCATION_TAG_QUERY_BY_CITY_SUCCESS("按城市查询地点标签成功"),
    LOCATION_TAG_QUERY_BY_PROVINCE_SUCCESS("按省份查询地点标签成功"),
    LOCATION_TAG_QUERY_BY_COUNTRY_SUCCESS("按国家查询地点标签成功"),
    LOCATION_TAG_UPDATE_SUCCESS("地点标签更新成功"),
    LOCATION_TAG_DELETE_SUCCESS("地点标签删除成功"),
    LOCATION_TAG_APPEARANCE_INCREMENT_SUCCESS("地点出现次数增加成功"),

    // EventTag（事件标签）相关
    TOG_EVENT_CREATE_SUCCESS("父事件创建成功"),
    TOG_EVENT_QUERY_SUCCESS("父事件查询成功"),
    TOG_EVENT_QUERY_BY_DIARY_SUCCESS("按日记 ID 查询父事件成功"),
    TOG_EVENT_UPDATE_SUCCESS("父事件更新成功"),
    TOG_EVENT_DELETE_SUCCESS("父事件删除成功"),
    SUB_EVENT_CREATE_SUCCESS("子事件创建成功"),
    SUB_EVENT_QUERY_SUCCESS("子事件查询成功"),
    SUB_EVENT_QUERY_BY_TOG_EVENT_SUCCESS("按父事件 ID 查询子事件成功"),
    SUB_EVENT_UPDATE_SUCCESS("子事件更新成功"),
    SUB_EVENT_DELETE_SUCCESS("子事件删除成功"),

    // Weather（天气）相关
    WEATHER_CREATE_SUCCESS("天气记录创建成功"),
    WEATHER_QUERY_SUCCESS("天气记录查询成功"),
    WEATHER_QUERY_BY_DIARY_SUCCESS("按日记 ID 查询天气成功"),
    WEATHER_QUERY_BY_CITY_SUCCESS("按城市查询天气成功"),
    WEATHER_QUERY_BY_DATE_SUCCESS("按日期查询天气成功"),
    WEATHER_QUERY_BY_CONDITION_SUCCESS("按天气状况查询天气成功"),
    WEATHER_UPDATE_SUCCESS("天气记录更新成功"),
    WEATHER_DELETE_SUCCESS("天气记录删除成功"),


    //用户设置
    SETTING_SUCCESS("设置成功"),
    MESSAGE_CONFIG_SUCCESS("响应消息更新成功"),

    // AI 分析
    ANALYSIS_STARTED("AI 分析已启动");

    /**
     * 成功消息名称
     */
    private final String name;

    /**
     * 状态码
     */
    @Setter
    private String code;

    /**
     * 消息内容
     */
    @Setter
    private String message;

    /**
     * 成功消息枚举构造函数
     * @param name 成功名字
     */
    SuccessMsgEnums(String name) {
        this.name = name;
    }
}