package com.lyric.lyric.Enums.function;

import lombok.Getter;

/**
 * 用户功能枚举类
 * 包含系统中所有可用的功能及其对应的中文名称
 *
 * @author Yichaoxuan
 * @since 2026/03/12
 */
public enum UserFunctionEnum {

    /**
     * AI分析功能
     */
    AI_ANALYTICS("aiAnalytics", "AI分析功能"),

    /**
     * 智能标签生成功能
     */
    SMART_LABEL_GENERATION("smartLabelGeneration", "智能标签生成功能"),

    /**
     * 实体标签生成功能
     */
    ENTITY_LABEL_GENERATION("entityLabelGeneration", "实体标签生成功能"),

    /**
     * 位置标记功能
     */
    LOCATION_MARKING("locationMarking", "位置标记功能"),

    /**
     * 天气识别功能
     */
    WEATHER_IDENTIFICATION("weatherIdentification", "天气识别功能");

    @Getter
    private final String code;

    @Getter
    private final String displayName;

    UserFunctionEnum(String code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    /**
     * 根据功能代码查找对应的枚举
     *
     * @param code 功能代码
     * @return 对应的枚举值，如果未找到则返回null
     */
    public static UserFunctionEnum fromCode(String code) {
        for (UserFunctionEnum function : UserFunctionEnum.values()) {
            if (function.getCode().equals(code)) {
                return function;
            }
        }
        return null;
    }
}