package com.lyric.lyric.Pojo.usersettings;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * 用户设置实体类
 * 用于存储和管理用户的各种配置选项
 *
 * @author Yichaoxun
 * @since 2025-11-23
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserSettingsPojo {

    /**
     * 首次使用日期
     */
    private LocalDate firstUseDate;

    /**
     * 默认城市
     */
    private String defaultCity;

    /**
     * 是否启用AI分析功能
     */
    private boolean isAIAnalytics;

    /**
     * 是否启用智能标签生成功能
     */
    private boolean isSmartLabelGeneration;

    /**
     * 是否启用实体标签生成功能
     */
    private boolean isEntityLabelGeneration;

    /**
     * 是否启用位置标记功能
     */
    private boolean isLocationMarking;

    /**
     * 是否启用天气识别功能
     */
    private boolean isWeatherIdentification;

    /**
     * Deepseek API配置
     */
    private String deepseekAPIKey;

    /**
     * 百度自然语言处理API配置
     */
    private String baiduNPLAPIKey;

    /**
     * 百度自然语言处理Secret密钥
     */
    private String baiduNLPAPIKey;

    /**
     * HanLP（汉语处理平台）API配置
     */
    private String HanLPAPIKey;

    /**
     * 百度地图API配置
     */
    private String baiduMapAPIKey;

    /**
     * 和风天气API配置
     */
    private String qWeatherAPIKey;
    private String qWeatherAPIHost;

    /**
     * 表情符号API配置
     */
    private String EmojiAPIKey;

}