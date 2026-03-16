package com.lyric.lyric.POJO.usersettings;

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
 * @since 2026-02-01
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
     * 用户信息配置
     */
    private UserInfo userInfo = new UserInfo();

    /**
     * 分析规则配置
     */
    private Rules rules = new Rules();

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
         * 自定义文件存储路径
         *
         */
        private FileStorageConfig fileStorageConfig;

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

        /**
         * 自定义文件存储路径内部类
         * 用于存储用户自定义的文件存储路径
         */
        @Setter
        @Getter
        @AllArgsConstructor
        public static class FileStorageConfig {
            /**
             * 文件存储路径
             * 用户自定义的文件存储路径
             */
            private String uploadDir;

            /**
             * 缩略图前缀
             */
            private String thumbnailSuffix;
        }

    }

    /**
     * 用户信息内部类
     * 存储用户信息
     */
    @Setter
    @Getter
    public static class UserInfo {
        /**
         * 首次使用日期
         * 记录用户首次使用应用的日期
         */
        private LocalDate firstUseDate;

        /**
         * 默认区县
         */
        private String defaultDistrict;

        /**
         * 默认城市
         */
        private String defaultCity;

        /**
         * 默认省份
         */
        private String defaultProvince;

        /**
         * 默认国家
         */
        private String defaultCountry;

        /**
         * 性别
         * 用户设置的性别
         */
        private String gender;

        /**
         * 年龄
         * 用户设置的年龄
         */
        private Integer age;

        /**
         * 职业
         * 用户设置的职业
         */
        private String occupation;

        /**
         * 用户基本信息打印
         * 输出包含用户基本信息的文本
         *
         * @param isEnter 首行是否换行 - true: 换行，false: 不换行
         * @return 包含用户基本信息的文本
         */
        public String getUserInfoStr(Boolean isEnter) {
            StringBuilder sb = new StringBuilder();
            if (isEnter) {
                sb.append("\n");
            }
            sb.append("- 默认区县：").append(defaultDistrict).append("\n");
            sb.append("- 默认城市：").append(defaultCity).append("\n");
            sb.append("- 默认省份：").append(defaultProvince).append("\n");
            sb.append("- 默认国家：").append(defaultCountry).append("\n");
            sb.append("- 性别：").append(gender).append("\n");
            sb.append("- 年龄：").append(age).append("\n");
            sb.append("- 职业：").append(occupation);
            return sb.toString();
        }
    }

    /**
     * 分析规则配置内部类
     * 用于存储用户自定义的分析规则
     */
    @Setter
    @Getter
    public  static class Rules {

        /**
         * 分析规则
         * 用户自定义的内容分析规则
         */
        private String tagAnalysisRules;

        /**
         * 人物标签去重规则
         * 用于对人物标签进行去重
         */
        private String CharacterTagDeduplicationRules;

        /**
         * 地点标签去重规则
         * 用于对地点标签进行去重
         */
        private String PlaceLabelDeduplicationRules;

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
         * AI大语言模型 API密钥
         * 用于访问AI服务的API密钥
         */
        private String aiLLMApiKey;

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
         * 百度地图API主机地址
         * 用于访问百度地图服务的API主机地址
         */
        private String baiduMapApiHost;

        /**
         * QWeather API密钥
         * 用于访问QWeather天气服务的API密钥
         */
        private String weatherApiKey;

        /**
         * QWeather API主机地址
         * QWeather天气服务的API主机地址
         */
        private String weatherApiHost;

        /**
         * Emoji API密钥
         * 用于访问Emoji服务的API密钥
         */
        private String emojiApiKey;
    }

}