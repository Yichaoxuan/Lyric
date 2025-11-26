package com.lyric.lyric.Config.userSetting;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 用户设置配置验证类
 * 用于在应用启动时验证用户设置是否成功加载
 */
@Component
public class UserSettingsVerification {
    
    private static final Logger logger = LoggerFactory.getLogger(UserSettingsVerification.class);
    
    private final UserSettingsConfig userSettingsConfig;
    
    public UserSettingsVerification(UserSettingsConfig userSettingsConfig) {
        this.userSettingsConfig = userSettingsConfig;
    }
    
    /**
     * 在应用启动后验证用户设置是否成功加载
     */
    @PostConstruct
    public void verifyUserSettings() {
        logger.info("=== 开始验证用户设置配置加载情况 ===");
        
        try {
            // 验证功能开关配置
            logger.info("功能开关配置:");
            logger.info("  AI分析功能: {}", userSettingsConfig.getFeatures().isAiAnalytics());
            logger.info("  智能标签生成功能: {}", userSettingsConfig.getFeatures().isSmartLabelGeneration());
            logger.info("  实体标签生成功能: {}", userSettingsConfig.getFeatures().isEntityLabelGeneration());
            logger.info("  位置标记功能: {}", userSettingsConfig.getFeatures().isLocationMarking());
            logger.info("  天气识别功能: {}", userSettingsConfig.getFeatures().isWeatherIdentification());
            
            // 验证用户偏好配置
            logger.info("用户偏好配置:");
            logger.info("  首次使用日期: {}", userSettingsConfig.getPreferences().getFirstUseDate());
            logger.info("  默认城市: {}", userSettingsConfig.getPreferences().getDefaultCity());
            logger.info("分析规则：{}", userSettingsConfig.getPreferences().getAnalysisRules());
            
            // 验证API配置（仅记录是否存在，不记录具体值）
            logger.info("API配置验证:");
            logger.info("  Deepseek API密钥是否存在: {}", 
                userSettingsConfig.getApi().getDeepseekApiKey() != null && 
                !userSettingsConfig.getApi().getDeepseekApiKey().isEmpty());
            logger.info("  百度NLP API密钥是否存在: {}", 
                userSettingsConfig.getApi().getBaiduNlpApiKey() != null && 
                !userSettingsConfig.getApi().getBaiduNlpApiKey().isEmpty());
            logger.info("  百度地图API密钥是否存在: {}", 
                userSettingsConfig.getApi().getBaiduMapApiKey() != null && 
                !userSettingsConfig.getApi().getBaiduMapApiKey().isEmpty());
            
            logger.info("=== 用户设置配置验证完成 ===");
        } catch (Exception e) {
            logger.error("用户设置配置验证失败", e);
        }
    }
}