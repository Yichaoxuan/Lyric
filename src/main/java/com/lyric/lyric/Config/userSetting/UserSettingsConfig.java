package com.lyric.lyric.Config.userSetting;

import com.lyric.lyric.Pojo.usersettings.UserSettingsPojo;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * 用户设置配置类
 * 用于加载和管理用户设置配置
 *
 * @author Yichaoxun
 * @since 2025-11-23
 */
@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "user-settings")
public class UserSettingsConfig {

    private static final Logger logger = LoggerFactory.getLogger(UserSettingsConfig.class);

    private Features features = new Features();
    private Preferences preferences = new Preferences();
    private Api api = new Api();

    /**
     * 在组件构造完成后打印配置信息
     * 该方法使用@PostConstruct注解，确保在依赖注入完成后自动执行
     */
    @PostConstruct
    public void printConfigurations() {
        logger.info("用户设置配置已加载:");
        logger.info("  AI分析功能: {}", features.isAiAnalytics());
        logger.info("  智能标签生成功能: {}", features.isSmartLabelGeneration());
        logger.info("  实体标签生成功能: {}", features.isEntityLabelGeneration());
        logger.info("  位置标记功能: {}", features.isLocationMarking());
        logger.info("  天气识别功能: {}", features.isWeatherIdentification());
        logger.info("  首次使用日期: {}", preferences.getFirstUseDate());
        logger.info("  默认城市: {}", preferences.getDefaultCity());
        logger.info("  分析规则: {}", preferences.getAnalysisRules());
    }

    /**
     * 转换为UserSettingsPojo对象
     * 将当前配置信息转换为UserSettingsPojo对象，便于在业务逻辑中使用
     * @return UserSettingsPojo对象，包含所有用户设置信息
     */
    public UserSettingsPojo toUserSettingsPojo() {
        logger.debug("开始转换UserSettingsConfig为UserSettingsPojo对象");
        
        UserSettingsPojo userSettingsPojo = new UserSettingsPojo();
        userSettingsPojo.setFirstUseDate(preferences.getFirstUseDate());
        userSettingsPojo.setDefaultCity(preferences.getDefaultCity());
        userSettingsPojo.setAIAnalytics(features.isAiAnalytics());
        userSettingsPojo.setSmartLabelGeneration(features.isSmartLabelGeneration());
        userSettingsPojo.setEntityLabelGeneration(features.isEntityLabelGeneration());
        userSettingsPojo.setLocationMarking(features.isLocationMarking());
        userSettingsPojo.setWeatherIdentification(features.isWeatherIdentification());
        userSettingsPojo.setDeepseekAPIKey(api.getDeepseekApiKey());
        userSettingsPojo.setBaiduNPLAPIKey(api.getBaiduNlpApiKey());
        userSettingsPojo.setBaiduNLPAPIKey(api.getBaiduNlpSecretKey());
        userSettingsPojo.setHanLPAPIKey(api.getHanlpApiKey());
        userSettingsPojo.setBaiduMapAPIKey(api.getBaiduMapApiKey());
        userSettingsPojo.setQWeatherAPIKey(api.getQweatherApiKey());
        userSettingsPojo.setQWeatherAPIHost(api.getQweatherApiHost());
        userSettingsPojo.setEmojiAPIKey(api.getEmojiApiKey());
        
        logger.debug("完成UserSettingsPojo对象转换，包含偏好设置、功能开关和API配置");
        return userSettingsPojo;
    }

    /**
     * 从UserSettingsPojo对象更新配置
     * 使用UserSettingsPojo对象中的数据更新当前配置信息
     * @param userSettingsPojo UserSettingsPojo对象，包含要更新的用户设置信息
     */
    public void updateFromUserSettingsPojo(UserSettingsPojo userSettingsPojo) {
        logger.info("开始更新用户设置配置");
        
        preferences.setFirstUseDate(userSettingsPojo.getFirstUseDate());
        preferences.setDefaultCity(userSettingsPojo.getDefaultCity());
        preferences.setAnalysisRules(userSettingsPojo.getAnalysisRules());
        features.setAiAnalytics(userSettingsPojo.isAIAnalytics());
        features.setSmartLabelGeneration(userSettingsPojo.isSmartLabelGeneration());
        features.setEntityLabelGeneration(userSettingsPojo.isEntityLabelGeneration());
        features.setLocationMarking(userSettingsPojo.isLocationMarking());
        features.setWeatherIdentification(userSettingsPojo.isWeatherIdentification());
        api.setDeepseekApiKey(userSettingsPojo.getDeepseekAPIKey());
        api.setBaiduNlpApiKey(userSettingsPojo.getBaiduNPLAPIKey());
        api.setBaiduNlpSecretKey(userSettingsPojo.getBaiduNLPAPIKey());
        api.setHanlpApiKey(userSettingsPojo.getHanLPAPIKey());
        api.setBaiduMapApiKey(userSettingsPojo.getBaiduMapAPIKey());
        api.setQweatherApiKey(userSettingsPojo.getQWeatherAPIKey());
        api.setQweatherApiHost(userSettingsPojo.getQWeatherAPIHost());
        api.setEmojiApiKey(userSettingsPojo.getEmojiAPIKey());
        
        logger.info("用户设置配置更新完成，包括偏好设置、功能开关和API配置");
    }

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
    public static class Preferences {
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