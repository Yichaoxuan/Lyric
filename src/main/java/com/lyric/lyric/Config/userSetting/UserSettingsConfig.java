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
    }

    /**
     * 转换为UserSettingsPojo对象
     * @return UserSettingsPojo对象
     */
    public UserSettingsPojo toUserSettingsPojo() {
        logger.debug("开始转换UserSettingsConfig为UserSettingsPojo对象");
        
        UserSettingsPojo userSettings = new UserSettingsPojo();
        userSettings.setFirstUseDate(preferences.getFirstUseDate());
        userSettings.setDefaultCity(preferences.getDefaultCity());
        userSettings.setAIAnalytics(features.isAiAnalytics());
        userSettings.setSmartLabelGeneration(features.isSmartLabelGeneration());
        userSettings.setEntityLabelGeneration(features.isEntityLabelGeneration());
        userSettings.setLocationMarking(features.isLocationMarking());
        userSettings.setWeatherIdentification(features.isWeatherIdentification());
        userSettings.setDeepseekAPIKey(api.getDeepseekApiKey());
        userSettings.setBaiduNPLAPIKey(api.getBaiduNlpApiKey());
        userSettings.setBaiduNLPAPIKey(api.getBaiduNlpSecretKey());
        userSettings.setHanLPAPIKey(api.getHanlpApiKey());
        userSettings.setBaiduMapAPIKey(api.getBaiduMapApiKey());
        userSettings.setQWeatherAPIKey(api.getQweatherApiKey());
        userSettings.setQWeatherAPIHost(api.getQweatherApiHost());
        userSettings.setEmojiAPIKey(api.getEmojiApiKey());
        
        logger.debug("完成UserSettingsPojo对象转换，包含偏好设置、功能开关和API配置");
        return userSettings;
    }

    /**
     * 从UserSettingsPojo对象更新配置
     * @param userSettings UserSettingsPojo对象
     */
    public void updateFromUserSettingsPojo(UserSettingsPojo userSettings) {
        logger.info("开始更新用户设置配置");
        
        preferences.setFirstUseDate(userSettings.getFirstUseDate());
        preferences.setDefaultCity(userSettings.getDefaultCity());
        features.setAiAnalytics(userSettings.isAIAnalytics());
        features.setSmartLabelGeneration(userSettings.isSmartLabelGeneration());
        features.setEntityLabelGeneration(userSettings.isEntityLabelGeneration());
        features.setLocationMarking(userSettings.isLocationMarking());
        features.setWeatherIdentification(userSettings.isWeatherIdentification());
        api.setDeepseekApiKey(userSettings.getDeepseekAPIKey());
        api.setBaiduNlpApiKey(userSettings.getBaiduNPLAPIKey());
        api.setBaiduNlpSecretKey(userSettings.getBaiduNLPAPIKey());
        api.setHanlpApiKey(userSettings.getHanLPAPIKey());
        api.setBaiduMapApiKey(userSettings.getBaiduMapAPIKey());
        api.setQweatherApiKey(userSettings.getQWeatherAPIKey());
        api.setQweatherApiHost(userSettings.getQWeatherAPIHost());
        api.setEmojiApiKey(userSettings.getEmojiAPIKey());
        
        logger.info("用户设置配置更新完成，包括偏好设置、功能开关和API配置");
    }

    @Setter
    @Getter
    public static class Features {
        private boolean aiAnalytics = true;
        private boolean smartLabelGeneration = true;
        private boolean entityLabelGeneration = true;
        private boolean locationMarking = true;
        private boolean weatherIdentification = true;

    }

    @Setter
    @Getter
    public static class Preferences {
        private LocalDate firstUseDate;
        private String defaultCity;

    }

    @Setter
    @Getter
    public static class Api {
        private String deepseekApiKey;
        private String baiduNlpApiKey;
        private String baiduNlpSecretKey;
        private String hanlpApiKey;
        private String baiduMapApiKey;
        private String qweatherApiKey;
        private String qweatherApiHost;
        private String emojiApiKey;

    }
}