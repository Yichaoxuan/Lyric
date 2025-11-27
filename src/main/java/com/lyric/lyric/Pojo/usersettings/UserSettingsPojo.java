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
     * 功能开关配置
     */
    private Features features = new Features();
    /**
     * 用户偏好配置
     */
    private Preferences preferences = new Preferences();
    /**
     * API配置
     */
    private Api api = new Api();

    /**
     * 功能开关配置内部类
     * 包含所有功能开关的配置项
     */
    @Setter
    @Getter
    public static class Features {
        /**
         * AI分析功能开关
         * 控制是否启用AI分析功能
         */
        private boolean aiAnalytics = true;

        /**
         * 智能标签生成功能开关
         * 控制是否启用智能标签生成功能
         */
        private boolean smartLabelGeneration = true;

        /**
         * 实体标签生成功能开关
         * 控制是否启用实体标签生成功能
         */
        private boolean entityLabelGeneration = true;

        /**
         * 位置标记功能开关
         * 控制是否启用位置标记功能
         */
        private boolean locationMarking = true;

        /**
         * 天气识别功能开关
         * 控制是否启用天气识别功能
         */
        private boolean weatherIdentification = true;

    }

    /**
     * 用户偏好配置内部类
     * 包含用户偏好相关的配置项
     */
    @Setter
    @Getter
    public  static class Preferences {
        /**
         * 首次使用日期
         * 记录用户首次使用应用的日期
         */
        private LocalDate firstUseDate;

        /**
         * 默认城市
         * 用户设置的默认城市，用于天气等服务
         */
        private String defaultCity;

        /**
         * 分析规则
         * 用户自定义的内容分析规则
         */
        private String analysisRules;

        /**
         * 响应消息生成规则
         * 用户自定义的响应消息生成规则
         */
        private String responseMessageGenerationRules;

    }

    /**
     * API配置内部类
     * 包含所有第三方API的密钥和配置信息
     */
    @Setter
    @Getter
    public static class Api {
        /**
         * DeepSeek API密钥
         * 用于访问DeepSeek AI服务的API密钥
         */
        private String deepseekApiKey;

        /**
         * 百度NLP API Key
         * 用于访问百度自然语言处理服务的API Key
         */
        private String baiduNlpApiKey;

        /**
         * 百度NLP Secret Key
         * 用于访问百度自然语言处理服务的Secret Key
         */
        private String baiduNlpSecretKey;

        /**
         * HanLP API密钥
         * 用于访问HanLP自然语言处理服务的API密钥
         */
        private String hanlpApiKey;

        /**
         * 百度地图API密钥
         * 用于访问百度地图服务的API密钥
         */
        private String baiduMapApiKey;

        /**
         * QWeather API密钥
         * 用于访问QWeather天气服务的API密钥
         */
        private String qweatherApiKey;

        /**
         * QWeather API主机地址
         * QWeather天气服务的API主机地址
         */
        private String qweatherApiHost;

        /**
         * Emoji API密钥
         * 用于访问Emoji服务的API密钥
         */
        private String emojiApiKey;

    }

}