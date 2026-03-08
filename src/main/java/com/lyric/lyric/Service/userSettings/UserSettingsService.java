package com.lyric.lyric.Service.userSettings;

import com.lyric.lyric.Config.userSetting.UserSettingsConfig;
import com.lyric.lyric.Enums.function.UserFunctionEnum;
import com.lyric.lyric.POJO.usersettings.UserSettingsPojo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
/**
 * 用户设置服务类
 * 提供获取和更新用户设置的功能
 *
 * @author Yichaoxun
 * @since 2026-02-04
 */
@Service
public class UserSettingsService {

    private static final Logger logger = LoggerFactory.getLogger(UserSettingsService.class);
    
    private final UserSettingsConfig userSettingsConfig;

    public UserSettingsService(UserSettingsConfig userSettingsConfig) {
        this.userSettingsConfig = userSettingsConfig;
    }

    /**
     * 获取当前所有用户设置
     *
     * @return UserSettingsPojo对象
     */
    public UserSettingsPojo getLatestConfig() {
        logger.debug("正在获取用户设置");
        UserSettingsPojo settings = userSettingsConfig.getLatestUserSettingsConfig();
        logger.debug("成功获取用户设置");
        return settings;
    }

    /**
     * 获取最新功能配置
     * @return UserSettingsPojo对象，包含当前功能配置
     */
    public UserSettingsPojo.Features getLatestFeatureConfig() {
        return userSettingsConfig.getLatestFeatureConfig();
    }

    /**
     * 获取最新API配置
     * @return UserSettingsPojo对象，包含当前API配置
     */
    public UserSettingsPojo.Api getLatestApiConfig() {
        return userSettingsConfig.getLatestApiConfig();
    }

    /**
     * 获取最新用户信息配置
     * @return UserSettingsPojo对象，包含当前用户信息配置
     */
    public UserSettingsPojo.UserInfo getLatestUserInfoConfig() {
        return userSettingsConfig.getLatestUserInfoConfig();
    }

    /**
     * 获取最新分析规则
     */
    public String getLatestAnalysisRulesConfig() {
        return userSettingsConfig.getLatestRulesConfig().getTagAnalysisRules();
    }

    /**
     * 获取最新人物标签去重规则
     */
    public String getPersonTagDuplicationRules() {
        return userSettingsConfig.getLatestRulesConfig().getCharacterTagDeduplicationRules();
    }

    /**
     *  获取最新地点标签生成规则
     */
    public String getLocationTagGenerationRules() {
        return userSettingsConfig.getLatestRulesConfig().getPlaceLabelDeduplicationRules();
    }

    /**
     * 获取最新响应消息生成规则
     */
    public String getResponseMessageGenerationRules() {
        return userSettingsConfig.getLatestRulesConfig().getResponseMessageGenerationRules();
    }

    /**
     * 更新用户设置
     *
     * @param userSettings 新的用户设置
     */
    public void updateUserSettings(UserSettingsPojo userSettings) {
        logger.info("开始更新用户设置");
        userSettingsConfig.updateFromUserSettingsPojo(userSettings);
        logger.info("用户设置更新完成");
    }
    
    /**
     * 检查特定功能是否启用（通过枚举）
     *
     * @param function 功能枚举
     * @return 如果功能启用返回true，否则返回false
     */
    public boolean isFeatureEnabled(UserFunctionEnum function) {
        logger.debug("检查功能是否启用: {}", function.getDisplayName());

        // 检查AI分析功能总开关是否启用
        if(!userSettingsConfig.getFeatures().isAiAnalytics()){
            logger.warn("AI分析功能已禁用，请启用后再试");
            return false;
        }
        
        boolean isEnabled = switch (function.getCode()) {
            case "aiAnalytics" -> userSettingsConfig.getFeatures().isAiAnalytics();
            case "smartLabelGeneration" -> userSettingsConfig.getFeatures().isSmartLabelGeneration();
            case "entityLabelGeneration" -> userSettingsConfig.getFeatures().isEntityLabelGeneration();
            case "locationMarking" -> userSettingsConfig.getFeatures().isLocationMarking();
            case "weatherIdentification" -> userSettingsConfig.getFeatures().isWeatherIdentification();
            default -> false;
        };

        logger.debug("功能 {}({}) 启用状态: {}", function.getDisplayName(), function.getCode(), isEnabled);
        return isEnabled;
    }
    
    /**
     * 手动验证并打印用户设置配置
     */
    public void validateAndPrintSettings() {
        logger.info("=== 手动验证用户设置配置 ===");
        
        try {
            // 打印功能开关配置
            logger.info("功能开关配置:");
            logger.info("  AI分析功能: {}", userSettingsConfig.getFeatures().isAiAnalytics());
            logger.info("  智能标签生成功能: {}", userSettingsConfig.getFeatures().isSmartLabelGeneration());
            logger.info("  实体标签生成功能: {}", userSettingsConfig.getFeatures().isEntityLabelGeneration());
            logger.info("  位置标记功能: {}", userSettingsConfig.getFeatures().isLocationMarking());
            logger.info("  天气识别功能: {}", userSettingsConfig.getFeatures().isWeatherIdentification());
            
            // 打印用户信息配置
            logger.info("用户信息配置:");
            logger.info("  首次使用日期: {}", userSettingsConfig.getUserInfo().getFirstUseDate());
            logger.info("  默认城市: {}", userSettingsConfig.getUserInfo().getDefaultCity());
            logger.info("  默认国家: {}", userSettingsConfig.getUserInfo().getDefaultCountry());
            logger.info("  性别: {}", userSettingsConfig.getUserInfo().getGender());
            logger.info("  年龄: {}", userSettingsConfig.getUserInfo().getAge());
            logger.info("  职业: {}", userSettingsConfig.getUserInfo().getOccupation());
            logger.info("  ");

            // 打印分析规则配置
            logger.info("分析规则配置:");
            logger.info("  标签分析规则: {}", userSettingsConfig.getRules().getTagAnalysisRules());
            logger.info("  人物标签去重规则: {}", userSettingsConfig.getRules().getCharacterTagDeduplicationRules());
            logger.info("  地点标签生成规则: {}", userSettingsConfig.getRules().getPlaceLabelDeduplicationRules());
            logger.info("  响应消息生成规则: {}", userSettingsConfig.getRules().getResponseMessageGenerationRules());

            // 打印API配置状态（不显示具体密钥）
            logger.info("API配置状态:");
            logger.info("  AI大模型 API密钥已设置: {}",
                userSettingsConfig.getApi().getAiLLMApiKey() != null &&
                !userSettingsConfig.getApi().getAiLLMApiKey().isEmpty());
            logger.info("  百度NLP API密钥已设置: {}", 
                userSettingsConfig.getApi().getBaiduNlpApiKey() != null && 
                !userSettingsConfig.getApi().getBaiduNlpApiKey().isEmpty());
            logger.info("  百度地图API密钥已设置: {}", 
                userSettingsConfig.getApi().getBaiduMapApiHost() != null &&
                !userSettingsConfig.getApi().getBaiduMapApiHost().isEmpty());
            
            logger.info("=== 验证完成 ===");
        } catch (Exception e) {
            logger.error("验证用户设置配置时发生错误", e);
        }
    }
}