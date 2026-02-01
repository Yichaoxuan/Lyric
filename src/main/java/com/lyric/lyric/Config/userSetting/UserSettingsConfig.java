package com.lyric.lyric.Config.userSetting;

import com.lyric.lyric.POJO.usersettings.UserSettingsPojo;
import com.lyric.lyric.Utils.config.ConfigLoggerUtil;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

/**
 * 用户设置配置类
 * 用于加载和管理用户设置配置
 *
 * @author Yichaoxun
 * @since 2026-01-31
 */
@Slf4j
@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "user-settings")
public class UserSettingsConfig {



    private Features features = new Features();
    private UserInfo userInfo = new UserInfo();
    private Rules rules = new Rules();
    private Api api = new Api();

    /**
     * 在组件构造完成后打印配置信息
     * 该方法使用@PostConstruct注解，确保在依赖注入完成后自动执行
     */
    @PostConstruct
    public void printConfigurations() {
        log.info("用户设置配置加载状态检查:");

        // 检查功能开关配置
        ConfigLoggerUtil.logConfigStatusSafely("功能开关配置", features);

        // 检查用户信息配置
        ConfigLoggerUtil.logConfigStatusSafely("用户信息配置", userInfo);
        
        // 检查分析规则配置
        ConfigLoggerUtil.logConfigStatusSafely("分析规则配置", rules);

        // 检查API配置
        ConfigLoggerUtil.logConfigStatusSafely("API配置", api);
    }

    /**
     * 转换为UserSettingsPojo对象
     * 将当前配置信息转换为UserSettingsPojo对象，便于在业务逻辑中使用
     * @return UserSettingsPojo对象，包含所有用户设置信息
     */
    public UserSettingsPojo toUserSettingsPojo() {

        UserSettingsPojo userSettingsPojo = new UserSettingsPojo();
        // 创建内部类实例
        UserSettingsPojo.Features pojoFeatures = new UserSettingsPojo.Features();
        UserSettingsPojo.UserInfo pojoUserInfo = new UserSettingsPojo.UserInfo();
        UserSettingsPojo.Rules pojoRules = new UserSettingsPojo.Rules();
        UserSettingsPojo.Api pojoApi = new UserSettingsPojo.Api();

        // 设置功能开关
        pojoFeatures.setAiAnalytics(features.isAiAnalytics());
        pojoFeatures.setSmartLabelGeneration(features.isSmartLabelGeneration());
        pojoFeatures.setEntityLabelGeneration(features.isEntityLabelGeneration());
        pojoFeatures.setLocationMarking(features.isLocationMarking());
        pojoFeatures.setWeatherIdentification(features.isWeatherIdentification());

        // 设置用户信息
        pojoUserInfo.setFirstUseDate(userInfo.getFirstUseDate());
        pojoUserInfo.setDefaultCity(userInfo.getDefaultCity());
        pojoUserInfo.setDefaultCountry(userInfo.getDefaultCountry());
        pojoUserInfo.setGender(userInfo.getGender());
        pojoUserInfo.setAge(userInfo.getAge());
        pojoUserInfo.setOccupation(userInfo.getOccupation());

        // 设置分析规则
        pojoRules.setTagAnalysisRules(rules.getTagAnalysisRules());
        pojoRules.setPersonTagDuplicationRules(rules.getPersonTagDuplicationRules());
        pojoRules.setResponseMessageGenerationRules(rules.getResponseMessageGenerationRules());

        // 设置API配置
        pojoApi.setDeepseekApiKey(api.getDeepseekApiKey());
        pojoApi.setBaiduNlpApiKey(api.getBaiduNlpApiKey());
        pojoApi.setBaiduNlpSecretKey(api.getBaiduNlpSecretKey());
        pojoApi.setHanlpApiKey(api.getHanlpApiKey());
        pojoApi.setBaiduMapApiKey(api.getBaiduMapApiKey());
        pojoApi.setQweatherApiKey(api.getQweatherApiKey());
        pojoApi.setQweatherApiHost(api.getQweatherApiHost());
        pojoApi.setEmojiApiKey(api.getEmojiApiKey());

        // 将内部类实例设置到UserSettingsPojo中
        userSettingsPojo.setFeatures(pojoFeatures);
        userSettingsPojo.setUserInfo(pojoUserInfo);
        userSettingsPojo.setRules(pojoRules);
        userSettingsPojo.setApi(pojoApi);

        log.info("用户设置配置更新完成");
        return userSettingsPojo;
    }

    /**
     * 从UserSettingsPojo对象更新配置
     * 使用UserSettingsPojo对象中的数据更新当前配置信息
     * @param userSettingsPojo UserSettingsPojo对象，包含要更新的用户设置信息
     */
    public void updateFromUserSettingsPojo(UserSettingsPojo userSettingsPojo) {
        log.info("开始更新用户设置配置");

        // 更新用户偏好配置
        userInfo.setFirstUseDate(userSettingsPojo.getUserInfo().getFirstUseDate());
        userInfo.setDefaultCity(userSettingsPojo.getUserInfo().getDefaultCity());
        userInfo.setDefaultCountry(userSettingsPojo.getUserInfo().getDefaultCountry());
        userInfo.setGender(userSettingsPojo.getUserInfo().getGender());
        userInfo.setAge(userSettingsPojo.getUserInfo().getAge());
        userInfo.setOccupation(userSettingsPojo.getUserInfo().getOccupation());

        // 更新规则配置
        rules.setTagAnalysisRules(userSettingsPojo.getRules().getTagAnalysisRules());
        rules.setPersonTagDuplicationRules(userSettingsPojo.getRules().getPersonTagDuplicationRules());
        rules.setResponseMessageGenerationRules(userSettingsPojo.getRules().getResponseMessageGenerationRules());

        // 更新功能开关配置
        features.setAiAnalytics(userSettingsPojo.getFeatures().isAiAnalytics());
        features.setSmartLabelGeneration(userSettingsPojo.getFeatures().isSmartLabelGeneration());
        features.setEntityLabelGeneration(userSettingsPojo.getFeatures().isEntityLabelGeneration());
        features.setLocationMarking(userSettingsPojo.getFeatures().isLocationMarking());
        features.setWeatherIdentification(userSettingsPojo.getFeatures().isWeatherIdentification());

        // 更新API配置
        api.setDeepseekApiKey(userSettingsPojo.getApi().getDeepseekApiKey());
        api.setBaiduNlpApiKey(userSettingsPojo.getApi().getBaiduNlpApiKey());
        api.setBaiduNlpSecretKey(userSettingsPojo.getApi().getBaiduNlpSecretKey());
        api.setHanlpApiKey(userSettingsPojo.getApi().getHanlpApiKey());
        api.setBaiduMapApiKey(userSettingsPojo.getApi().getBaiduMapApiKey());
        api.setQweatherApiKey(userSettingsPojo.getApi().getQweatherApiKey());
        api.setQweatherApiHost(userSettingsPojo.getApi().getQweatherApiHost());
        api.setEmojiApiKey(userSettingsPojo.getApi().getEmojiApiKey());

        log.info("完成用户设置配置更新");
    }

    /**
     * 获取最新的用户设置配置
     * @return UserSettingsPojo对象，包含所有用户设置信息
     */
    public UserSettingsPojo getLatestUserSettingsConfig() {
        return toUserSettingsPojo();
    }

    /**
     * 获取最新的功能开关配置
     * @return UserSettingsPojo对象，包含功能开关配置
     */
    public UserSettingsPojo.Features getLatestFeatureConfig() {
        UserSettingsPojo.Features pojoFeatures = new UserSettingsPojo.Features();
        pojoFeatures.setAiAnalytics(features.isAiAnalytics());
        pojoFeatures.setSmartLabelGeneration(features.isSmartLabelGeneration());
        pojoFeatures.setEntityLabelGeneration(features.isEntityLabelGeneration());
        pojoFeatures.setLocationMarking(features.isLocationMarking());
        pojoFeatures.setWeatherIdentification(features.isWeatherIdentification());
        return pojoFeatures;
    }

    /**
     * 获取最新的用户信息配置
     * @return UserSettingsPojo对象，包含用户信息配置
     */
    public UserSettingsPojo.UserInfo getLatestUserInfoConfig() {
        UserSettingsPojo.UserInfo pojoUserInfo = new UserSettingsPojo.UserInfo();
        pojoUserInfo.setDefaultCity(userInfo.getDefaultCity());
        pojoUserInfo.setDefaultCountry(userInfo.getDefaultCountry());
        pojoUserInfo.setGender(userInfo.getGender());
        pojoUserInfo.setAge(userInfo.getAge());
        pojoUserInfo.setOccupation(userInfo.getOccupation());
        return pojoUserInfo;
    }

    /**
     * 获取最新的分析规则配置
     * @return UserSettingsPojo对象，包含分析规则配置
     */
    public UserSettingsPojo.Rules getLatestRulesConfig() {
        UserSettingsPojo.Rules pojoRules = new UserSettingsPojo.Rules();
        pojoRules.setTagAnalysisRules(rules.getTagAnalysisRules());
        pojoRules.setPersonTagDuplicationRules(rules.getPersonTagDuplicationRules());
        pojoRules.setResponseMessageGenerationRules(rules.getResponseMessageGenerationRules());
        return pojoRules;
    }

    /**
     * 获取最新的API配置
     * @return UserSettingsPojo对象，包含API配置
     */
    public UserSettingsPojo.Api getLatestApiConfig() {
        UserSettingsPojo.Api pojoApi = new UserSettingsPojo.Api();
        pojoApi.setDeepseekApiKey(api.getDeepseekApiKey());
        pojoApi.setBaiduNlpApiKey(api.getBaiduNlpApiKey());
        pojoApi.setBaiduNlpSecretKey(api.getBaiduNlpSecretKey());
        pojoApi.setHanlpApiKey(api.getHanlpApiKey());
        pojoApi.setBaiduMapApiKey(api.getBaiduMapApiKey());
        pojoApi.setQweatherApiKey(api.getQweatherApiKey());
        pojoApi.setQweatherApiHost(api.getQweatherApiHost());
        pojoApi.setEmojiApiKey(api.getEmojiApiKey());
        return pojoApi;
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
         * 默认城市
         * 用户设置的默认城市，用于天气等服务
         */
        private String defaultCity;

        /**
         * 默认国家
         * 用户设置的默认国家，用于天气等服务
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
    }

    /**
     * 分析规则配置内部类
     * 用于存储用户自定义的分析规则
     */
    @Setter
    @Getter
    public  static class Rules {

        /**
         * 标签分析规则
         * 用户自定义的内容分析规则
         */
        private String tagAnalysisRules;

        /**
         * 人物标签去重规则
         * 用于对人物标签进行去重
         */
        private String personTagDuplicationRules;

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