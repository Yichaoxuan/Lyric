package com.lyric.lyric.Utils.config;

import com.lyric.lyric.Config.userSetting.UserSettingsConfig;
import com.lyric.lyric.POJO.usersettings.UserSettingsPojo;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;

/**
 * 配置日志工具类
 * 用于统一记录配置项的加载状态，避免敏感信息泄露
 */
@Slf4j
public class ConfigLoggerUtil {

    /**
     * 记录配置项状态的辅助方法
     *
     * @param logger     日志记录器
     * @param configName 配置项名称
     * @param value      配置项值
     */
    public static void logConfigStatus(Logger logger, String configName, Object value) {
        if (value == null) {
            logger.warn("{}: 未注入 (null)", configName);
        } else if (value instanceof String && ((String) value).isEmpty()) {
            logger.warn("{}: 未注入 (空字符串)", configName);
        } else if (isSensitiveField(configName)) {
            // 对于敏感信息，只记录是否已设置，而不显示具体内容
            logger.info("{}: 注入成功 (已设置)", configName);
        } else if (value instanceof Map<?, ?> mapValue) {
            if (mapValue.isEmpty()) {
                logger.warn("{}: 未注入 (空集合)", configName);
            } else {
                logger.info("{}: 注入成功 (大小: {})", configName, mapValue.size());
                // 详细记录Map中的关键信息
                for (Map.Entry<?, ?> entry : mapValue.entrySet()) {
                    String key = String.valueOf(entry.getKey());
                    Object val = entry.getValue();
                    logConfigStatus(logger, "  - " + key, val);
                }
            }
        } else if (value instanceof Collection<?> collectionValue) {
            if (collectionValue.isEmpty()) {
                logger.warn("{}: 未注入 (空集合)", configName);
            } else {
                logger.info("{}: 注入成功 (大小: {})", configName, collectionValue.size());
            }
        } else if (value instanceof Number numberValue) {
            logger.info("{}: 注入成功 ({})", configName, numberValue);
        } else if (value instanceof Boolean boolValue) {
            logger.info("{}: 注入成功 ({})", configName, boolValue);
        } else if (value instanceof Enum<?> enumValue) {
            logger.info("{}: 注入成功 ({})", configName, enumValue);
        } else if (isComplexObject(value)) {
            // 对于复杂对象，递归记录其属性
            logger.info("{}: 注入成功 (类型: {})", configName, value.getClass().getSimpleName());
            logObjectFields(logger, value, "  ");
        } else {
            logger.info("{}: 注入成功 ({})", configName, value);
        }
    }

    /**
     * 直接使用类级别的logger记录配置项状态
     *
     * @param configName 配置项名称
     * @param value      配置项值
     */
    public static void logConfigStatus(String configName, Object value) {
        if (value == null) {
            log.warn("{}: 未注入 (null)", configName);
        } else if (value instanceof String && ((String) value).isEmpty()) {
            log.warn("{}: 未注入 (空字符串)", configName);
        } else if (isSensitiveField(configName)) {
            // 对于敏感信息，只记录是否已设置，而不显示具体内容
            log.info("{}: 注入成功 (已设置)", configName);
        } else if (value instanceof Map<?, ?> mapValue) {
            if (mapValue.isEmpty()) {
                log.warn("{}: 未注入 (空集合)", configName);
            } else {
                log.info("{}: 注入成功 (大小: {})", configName, mapValue.size());
                // 详细记录Map中的关键信息
                for (Map.Entry<?, ?> entry : mapValue.entrySet()) {
                    String key = String.valueOf(entry.getKey());
                    Object val = entry.getValue();
                    logConfigStatus("  - " + key, val);
                }
            }
        } else if (value instanceof Collection<?> collectionValue) {
            if (collectionValue.isEmpty()) {
                log.warn("{}: 未注入 (空集合)", configName);
            } else {
                log.info("{}: 注入成功 (大小: {})", configName, collectionValue.size());
            }
        } else if (value instanceof Number numberValue) {
            log.info("{}: 注入成功 ({})", configName, numberValue);
        } else if (value instanceof Boolean boolValue) {
            log.info("{}: 注入成功 ({})", configName, boolValue);
        } else if (value instanceof Enum<?> enumValue) {
            log.info("{}: 注入成功 ({})", configName, enumValue);
        } else if (isComplexObject(value)) {
            // 对于复杂对象，递归记录其属性
            log.info("{}: 注入成功 (类型: {})", configName, value.getClass().getSimpleName());
            logObjectFields(log, value, "  ");
        } else {
            log.info("{}: 注入成功 ({})", configName, value);
        }
    }

    /**
     * 安全记录配置项状态（仅显示是否注入成功及数量，不显示具体信息）
     * 特别适用于敏感配置项的检查
     *
     * @param configName 配置项名称
     * @param value      配置项值
     */
    public static void logConfigStatusSafely(String configName, Object value) {
        if (value == null) {
            log.warn("{}: 未注入 (null)", configName);
        } else if (value instanceof String && ((String) value).isEmpty()) {
            log.warn("{}: 未注入 (空字符串)", configName);
        } else if (value instanceof Map<?, ?> mapValue) {
            if (mapValue.isEmpty()) {
                log.warn("{}: 未注入 (空集合)", configName);
            } else {
                log.info("{}: 注入成功 (数量: {})", configName, mapValue.size());
            }
        } else if (value instanceof Collection<?> collectionValue) {
            if (collectionValue.isEmpty()) {
                log.warn("{}: 未注入 (空集合)", configName);
            } else {
                log.info("{}: 注入成功 (数量: {})", configName, collectionValue.size());
            }
        } else if (isComplexObject(value)) {
            // 对于复杂对象，只记录对象类型和基本注入状态
            log.info("{}: 注入成功 (类型: {})", configName, value.getClass().getSimpleName());
        } else {
            log.info("{}: 注入成功", configName);
        }
    }

    /**
     * 安全记录配置项状态（仅显示是否注入成功及数量，不显示具体信息）
     * 特别适用于敏感配置项的检查
     *
     * @param logger     日志记录器
     * @param configName 配置项名称
     * @param value      配置项值
     */
    public static void logConfigStatusSafely(Logger logger, String configName, Object value) {
        if (value == null) {
            logger.warn("{}: 未注入 (null)", configName);
        } else if (value instanceof String && ((String) value).isEmpty()) {
            logger.warn("{}: 未注入 (空字符串)", configName);
        } else if (value instanceof Map<?, ?> mapValue) {
            if (mapValue.isEmpty()) {
                logger.warn("{}: 未注入 (空集合)", configName);
            } else {
                logger.info("{}: 注入成功 (数量: {})", configName, mapValue.size());
            }
        } else if (value instanceof Collection<?> collectionValue) {
            if (collectionValue.isEmpty()) {
                logger.warn("{}: 未注入 (空集合)", configName);
            } else {
                logger.info("{}: 注入成功 (数量: {})", configName, collectionValue.size());
            }
        } else if (isComplexObject(value)) {
            // 对于复杂对象，只记录对象类型和基本注入状态
            logger.info("{}: 注入成功 (类型: {})", configName, value.getClass().getSimpleName());
        } else {
            logger.info("{}: 注入成功", configName);
        }
    }

    /**
     * 检查字段名是否包含敏感信息关键词
     *
     * @param fieldName 字段名
     * @return 是否为敏感字段
     */
    private static boolean isSensitiveField(String fieldName) {
        return fieldName.toLowerCase().contains("api密钥") || 
               fieldName.toLowerCase().contains("api key") ||
               fieldName.toLowerCase().contains("secret") || 
               fieldName.toLowerCase().contains("密钥") ||
               fieldName.toLowerCase().contains("password") ||
               fieldName.toLowerCase().contains("token") ||
               fieldName.toLowerCase().contains("key") ||
               fieldName.contains("ApiKey") ||
               fieldName.contains("SecretKey") ||
               fieldName.contains("Password") ||
               fieldName.contains("Token");
    }

    /**
     * 检查是否为复杂对象（需要递归记录属性的对象）
     *
     * @param obj 要检查的对象
     * @return 是否为复杂对象
     */
    private static boolean isComplexObject(Object obj) {
        if (obj == null) {
            return false;
        }
        Class<?> clazz = obj.getClass();
        // 排除基本类型和常用包装类型、String、日期时间类等简单类型
        return !(clazz.isPrimitive() || 
                 clazz == String.class || 
                 Number.class.isAssignableFrom(clazz) || 
                 Boolean.class == clazz || 
                 Character.class == clazz ||
                 clazz.getName().startsWith("java.lang") ||
                 clazz.getName().startsWith("java.time") ||
                 clazz.isEnum());
    }

    /**
     * 递归记录对象的所有字段
     *
     * @param logger 日志记录器
     * @param obj    要记录的对象
     * @param indent 缩进字符串
     */
    private static void logObjectFields(Logger logger, Object obj, String indent) {
        if (obj == null) {
            logger.warn("{}对象为null", indent);
            return;
        }

        Class<?> clazz = obj.getClass();
        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            try {
                field.setAccessible(true);
                String fieldName = field.getName();
                Object fieldValue = field.get(obj);
                
                String fieldLogName = indent + fieldName;
                if (fieldValue == null) {
                    logger.warn("{}: 未注入 (null)", fieldLogName);
                } else if (isComplexObject(fieldValue) && !isSimpleClass(fieldValue.getClass())) {
                    logger.info("{}: (类型: {})", fieldLogName, fieldValue.getClass().getSimpleName());
                    logObjectFields(logger, fieldValue, indent + "  ");
                } else {
                    logConfigStatus(logger, fieldLogName, fieldValue);
                }
            } catch (IllegalAccessException e) {
                logger.warn("无法访问字段: {}", field.getName());
            }
        }
    }

    /**
     * 检查是否为简单类（不需要递归记录属性的类）
     *
     * @param clazz 类
     * @return 是否为简单类
     */
    private static boolean isSimpleClass(Class<?> clazz) {
        return clazz.isPrimitive() || 
               clazz == String.class || 
               Number.class.isAssignableFrom(clazz) || 
               Boolean.class == clazz || 
               Character.class == clazz ||
               clazz.getName().startsWith("java.lang") ||
               clazz.getName().startsWith("java.time") ||
               clazz.isEnum();
    }

    /**
     * 特殊处理UserSettingsConfig的内部类对象
     *
     * @param logger     日志记录器
     * @param configName 配置项名称
     * @param value      UserSettingsConfig中的配置对象
     */
    public static void logConfigStatus(Logger logger, String configName, UserSettingsConfig.Features value) {
        if (value == null) {
            logger.warn("{}: 未注入 (null)", configName);
        } else {
            logger.info("{}:", configName);
            logConfigStatus(logger, "  - AI分析功能开关", value.isAiAnalytics());
            logConfigStatus(logger, "  - 智能标签生成功能开关", value.isSmartLabelGeneration());
            logConfigStatus(logger, "  - 实体标签生成功能开关", value.isEntityLabelGeneration());
            logConfigStatus(logger, "  - 位置标记功能开关", value.isLocationMarking());
            logConfigStatus(logger, "  - 天气识别功能开关", value.isWeatherIdentification());
        }
    }

    /**
     * 特殊处理UserSettingsPojo.Features对象
     *
     * @param logger     日志记录器
     * @param configName 配置项名称
     * @param value      UserSettingsPojo中的Features对象
     */
    public static void logConfigStatus(Logger logger, String configName, UserSettingsPojo.Features value) {
        if (value == null) {
            logger.warn("{}: 未注入 (null)", configName);
        } else {
            logger.info("{}:", configName);
            logConfigStatus(logger, "  - AI分析功能开关", value.isAiAnalytics());
            logConfigStatus(logger, "  - 智能标签生成功能开关", value.isSmartLabelGeneration());
            logConfigStatus(logger, "  - 实体标签生成功能开关", value.isEntityLabelGeneration());
            logConfigStatus(logger, "  - 位置标记功能开关", value.isLocationMarking());
            logConfigStatus(logger, "  - 天气识别功能开关", value.isWeatherIdentification());
        }
    }

    /**
     * 使用Slf4j注解的logger直接记录UserSettingsConfig.Features对象
     *
     * @param configName 配置项名称
     * @param value      UserSettingsConfig中的Features对象
     */
    public static void logConfigStatus(String configName, UserSettingsConfig.Features value) {
        if (value == null) {
            log.warn("{}: 未注入 (null)", configName);
        } else {
            log.info("{}:", configName);
            logConfigStatus("  - AI分析功能开关", value.isAiAnalytics());
            logConfigStatus("  - 智能标签生成功能开关", value.isSmartLabelGeneration());
            logConfigStatus("  - 实体标签生成功能开关", value.isEntityLabelGeneration());
            logConfigStatus("  - 位置标记功能开关", value.isLocationMarking());
            logConfigStatus("  - 天气识别功能开关", value.isWeatherIdentification());
        }
    }

    /**
     * 使用Slf4j注解的logger直接记录UserSettingsPojo.Features对象
     *
     * @param configName 配置项名称
     * @param value      UserSettingsPojo中的Features对象
     */
    public static void logConfigStatus(String configName, UserSettingsPojo.Features value) {
        if (value == null) {
            log.warn("{}: 未注入 (null)", configName);
        } else {
            log.info("{}:", configName);
            logConfigStatus("  - AI分析功能开关", value.isAiAnalytics());
            logConfigStatus("  - 智能标签生成功能开关", value.isSmartLabelGeneration());
            logConfigStatus("  - 实体标签生成功能开关", value.isEntityLabelGeneration());
            logConfigStatus("  - 位置标记功能开关", value.isLocationMarking());
            logConfigStatus("  - 天气识别功能开关", value.isWeatherIdentification());
        }
    }

    /**
     * 安全记录UserSettingsConfig.Features对象（仅显示是否注入成功及数量，不显示具体信息）
     *
     * @param configName 配置项名称
     * @param value      UserSettingsConfig中的Features对象
     */
    public static void logConfigStatusSafely(String configName, UserSettingsConfig.Features value) {
        if (value == null) {
            log.warn("{}: 未注入 (null)", configName);
        } else {
            log.info("{}: 注入成功 (功能开关数量: 5)", configName);
        }
    }

    /**
     * 安全记录UserSettingsPojo.Features对象（仅显示是否注入成功及数量，不显示具体信息）
     *
     * @param configName 配置项名称
     * @param value      UserSettingsPojo中的Features对象
     */
    public static void logConfigStatusSafely(String configName, UserSettingsPojo.Features value) {
        if (value == null) {
            log.warn("{}: 未注入 (null)", configName);
        } else {
            log.info("{}: 注入成功 (功能开关数量: 5)", configName);
        }
    }

    /**
     * 特殊处理UserSettingsConfig的UserInfo对象
     *
     * @param logger     日志记录器
     * @param configName 配置项名称
     * @param value      UserSettingsConfig中的UserInfo对象
     */
    public static void logConfigStatus(Logger logger, String configName, UserSettingsConfig.UserInfo value) {
        if (value == null) {
            logger.warn("{}: 未注入 (null)", configName);
        } else {
            logger.info("{}:", configName);
            logConfigStatus(logger, "  - 首次使用日期", value.getFirstUseDate());
            logConfigStatus(logger, "  - 默认城市", value.getDefaultCity());
            logConfigStatus(logger, "  - 默认国家", value.getDefaultCountry());
            logConfigStatus(logger, "  - 性别", value.getGender());
            logConfigStatus(logger, "  - 年龄", value.getAge());
            logConfigStatus(logger, "  - 职业", value.getOccupation());
        }
    }

    /**
     * 特殊处理UserSettingsPojo的UserInfo对象
     *
     * @param logger     日志记录器
     * @param configName 配置项名称
     * @param value      UserSettingsPojo中的UserInfo对象
     */
    public static void logConfigStatus(Logger logger, String configName, UserSettingsPojo.UserInfo value) {
        if (value == null) {
            logger.warn("{}: 未注入 (null)", configName);
        } else {
            logger.info("{}:", configName);
            logConfigStatus(logger, "  - 首次使用日期", value.getFirstUseDate());
            logConfigStatus(logger, "  - 默认城市", value.getDefaultCity());
            logConfigStatus(logger, "  - 默认国家", value.getDefaultCountry());
            logConfigStatus(logger, "  - 性别", value.getGender());
            logConfigStatus(logger, "  - 年龄", value.getAge());
            logConfigStatus(logger, "  - 职业", value.getOccupation());
        }
    }

    /**
     * 使用Slf4j注解的logger直接记录UserSettingsConfig.UserInfo对象
     *
     * @param configName 配置项名称
     * @param value      UserSettingsConfig中的UserInfo对象
     */
    public static void logConfigStatus(String configName, UserSettingsConfig.UserInfo value) {
        if (value == null) {
            log.warn("{}: 未注入 (null)", configName);
        } else {
            log.info("{}:", configName);
            logConfigStatus("  - 首次使用日期", value.getFirstUseDate());
            logConfigStatus("  - 默认城市", value.getDefaultCity());
            logConfigStatus("  - 默认国家", value.getDefaultCountry());
            logConfigStatus("  - 性别", value.getGender());
            logConfigStatus("  - 年龄", value.getAge());
            logConfigStatus("  - 职业", value.getOccupation());
        }
    }

    /**
     * 使用Slf4j注解的logger直接记录UserSettingsPojo.UserInfo对象
     *
     * @param configName 配置项名称
     * @param value      UserSettingsPojo中的UserInfo对象
     */
    public static void logConfigStatus(String configName, UserSettingsPojo.UserInfo value) {
        if (value == null) {
            log.warn("{}: 未注入 (null)", configName);
        } else {
            log.info("{}:", configName);
            logConfigStatus("  - 首次使用日期", value.getFirstUseDate());
            logConfigStatus("  - 默认城市", value.getDefaultCity());
            logConfigStatus("  - 默认国家", value.getDefaultCountry());
            logConfigStatus("  - 性别", value.getGender());
            logConfigStatus("  - 年龄", value.getAge());
            logConfigStatus("  - 职业", value.getOccupation());
        }
    }

    /**
     * 安全记录UserSettingsConfig.UserInfo对象（仅显示是否注入成功及数量，不显示具体信息）
     *
     * @param configName 配置项名称
     * @param value      UserSettingsConfig中的UserInfo对象
     */
    public static void logConfigStatusSafely(String configName, UserSettingsConfig.UserInfo value) {
        if (value == null) {
            log.warn("{}: 未注入 (null)", configName);
        } else {
            log.info("{}: 注入成功 (用户信息项数量: 6)", configName);
        }
    }

    /**
     * 安全记录UserSettingsPojo.UserInfo对象（仅显示是否注入成功及数量，不显示具体信息）
     *
     * @param configName 配置项名称
     * @param value      UserSettingsPojo中的UserInfo对象
     */
    public static void logConfigStatusSafely(String configName, UserSettingsPojo.UserInfo value) {
        if (value == null) {
            log.warn("{}: 未注入 (null)", configName);
        } else {
            log.info("{}: 注入成功 (用户信息项数量: 6)", configName);
        }
    }

    /**
     * 特殊处理UserSettingsConfig的AnalysisRules对象
     *
     * @param logger     日志记录器
     * @param configName 配置项名称
     * @param value      UserSettingsConfig中的AnalysisRules对象
     */
    public static void logConfigStatus(Logger logger, String configName, UserSettingsConfig.Rules value) {
        if (value == null) {
            logger.warn("{}: 未注入 (null)", configName);
        } else {
            logger.info("{}:", configName);
            logConfigStatus(logger, "  - 分析规则", value.getTagAnalysisRules());
            logConfigStatus(logger, "  - 人物标签去重规则", value.getPersonTagDuplicationRules());
            logConfigStatus(logger, "  - 响应消息生成规则", value.getResponseMessageGenerationRules());
        }
    }

    /**
     * 特殊处理UserSettingsPojo的AnalysisRules对象
     *
     * @param logger     日志记录器
     * @param configName 配置项名称
     * @param value      UserSettingsPojo中的AnalysisRules对象
     */
    public static void logConfigStatus(Logger logger, String configName, UserSettingsPojo.Rules value) {
        if (value == null) {
            logger.warn("{}: 未注入 (null)", configName);
        } else {
            logger.info("{}:", configName);
            logConfigStatus(logger, "  - 分析规则", value.getTagAnalysisRules());
            logConfigStatus(logger, "  - 人物标签去重规则", value.getPersonTagDuplicationRules());
            logConfigStatus(logger, "  - 响应消息生成规则", value.getResponseMessageGenerationRules());
        }
    }

    /**
     * 使用Slf4j注解的logger直接记录UserSettingsConfig.AnalysisRules对象
     *
     * @param configName 配置项名称
     * @param value      UserSettingsConfig中的AnalysisRules对象
     */
    public static void logConfigStatus(String configName, UserSettingsConfig.Rules value) {
        if (value == null) {
            log.warn("{}: 未注入 (null)", configName);
        } else {
            log.info("{}:", configName);
            logConfigStatus("  - 分析规则", value.getTagAnalysisRules());
            logConfigStatus("  - 人物标签去重规则", value.getPersonTagDuplicationRules());
            logConfigStatus("  - 响应消息生成规则", value.getResponseMessageGenerationRules());
        }
    }

    /**
     * 使用Slf4j注解的logger直接记录UserSettingsPojo.AnalysisRules对象
     *
     * @param configName 配置项名称
     * @param value      UserSettingsPojo中的AnalysisRules对象
     */
    public static void logConfigStatus(String configName, UserSettingsPojo.Rules value) {
        if (value == null) {
            log.warn("{}: 未注入 (null)", configName);
        } else {
            log.info("{}:", configName);
            logConfigStatus("  - 分析规则", value.getTagAnalysisRules());
            logConfigStatus("  - 人物标签去重规则", value.getPersonTagDuplicationRules());
            logConfigStatus("  - 响应消息生成规则", value.getResponseMessageGenerationRules());
        }
    }

    /**
     * 安全记录UserSettingsConfig.AnalysisRules对象（仅显示是否注入成功及数量，不显示具体信息）
     *
     * @param configName 配置项名称
     * @param value      UserSettingsConfig中的AnalysisRules对象
     */
    public static void logConfigStatusSafely(String configName, UserSettingsConfig.Rules value) {
        if (value == null) {
            log.warn("{}: 未注入 (null)", configName);
        } else {
            log.info("{}: 注入成功 (分析规则项数量: 3)", configName);
        }
    }

    /**
     * 安全记录UserSettingsPojo.AnalysisRules对象（仅显示是否注入成功及数量，不显示具体信息）
     *
     * @param configName 配置项名称
     * @param value      UserSettingsPojo中的AnalysisRules对象
     */
    public static void logConfigStatusSafely(String configName, UserSettingsPojo.Rules value) {
        if (value == null) {
            log.warn("{}: 未注入 (null)", configName);
        } else {
            log.info("{}: 注入成功 (分析规则项数量: 3)", configName);
        }
    }

    /**
     * 特殊处理UserSettingsConfig的Api对象
     *
     * @param logger     日志记录器
     * @param configName 配置项名称
     * @param value      UserSettingsConfig中的Api对象
     */
    public static void logConfigStatus(Logger logger, String configName, UserSettingsConfig.Api value) {
        if (value == null) {
            logger.warn("{}: 未注入 (null)", configName);
        } else {
            logger.info("{}:", configName);
            logConfigStatus(logger, "  - DeepSeek API密钥", value.getDeepseekApiKey());
            logConfigStatus(logger, "  - 百度NLP API Key", value.getBaiduNlpApiKey());
            logConfigStatus(logger, "  - 百度NLP Secret Key", value.getBaiduNlpSecretKey());
            logConfigStatus(logger, "  - HanLP API密钥", value.getHanlpApiKey());
            logConfigStatus(logger, "  - 百度地图API密钥", value.getBaiduMapApiKey());
            logConfigStatus(logger, "  - QWeather API密钥", value.getQweatherApiKey());
            logConfigStatus(logger, "  - QWeather API主机地址", value.getQweatherApiHost());
            logConfigStatus(logger, "  - Emoji API密钥", value.getEmojiApiKey());
        }
    }

    /**
     * 特殊处理UserSettingsPojo的Api对象
     *
     * @param logger     日志记录器
     * @param configName 配置项名称
     * @param value      UserSettingsPojo中的Api对象
     */
    public static void logConfigStatus(Logger logger, String configName, UserSettingsPojo.Api value) {
        if (value == null) {
            logger.warn("{}: 未注入 (null)", configName);
        } else {
            logger.info("{}:", configName);
            logConfigStatus(logger, "  - DeepSeek API密钥", value.getDeepseekApiKey());
            logConfigStatus(logger, "  - 百度NLP API Key", value.getBaiduNlpApiKey());
            logConfigStatus(logger, "  - 百度NLP Secret Key", value.getBaiduNlpSecretKey());
            logConfigStatus(logger, "  - HanLP API密钥", value.getHanlpApiKey());
            logConfigStatus(logger, "  - 百度地图API密钥", value.getBaiduMapApiKey());
            logConfigStatus(logger, "  - QWeather API密钥", value.getQweatherApiKey());
            logConfigStatus(logger, "  - QWeather API主机地址", value.getQweatherApiHost());
            logConfigStatus(logger, "  - Emoji API密钥", value.getEmojiApiKey());
        }
    }

    /**
     * 使用Slf4j注解的logger直接记录UserSettingsConfig.Api对象
     *
     * @param configName 配置项名称
     * @param value      UserSettingsConfig中的Api对象
     */
    public static void logConfigStatus(String configName, UserSettingsConfig.Api value) {
        if (value == null) {
            log.warn("{}: 未注入 (null)", configName);
        } else {
            log.info("{}:", configName);
            logConfigStatus("  - DeepSeek API密钥", value.getDeepseekApiKey());
            logConfigStatus("  - 百度NLP API Key", value.getBaiduNlpApiKey());
            logConfigStatus("  - 百度NLP Secret Key", value.getBaiduNlpSecretKey());
            logConfigStatus("  - HanLP API密钥", value.getHanlpApiKey());
            logConfigStatus("  - 百度地图API密钥", value.getBaiduMapApiKey());
            logConfigStatus("  - QWeather API密钥", value.getQweatherApiKey());
            logConfigStatus("  - QWeather API主机地址", value.getQweatherApiHost());
            logConfigStatus("  - Emoji API密钥", value.getEmojiApiKey());
        }
    }

    /**
     * 使用Slf4j注解的logger直接记录UserSettingsPojo.Api对象
     *
     * @param configName 配置项名称
     * @param value      UserSettingsPojo中的Api对象
     */
    public static void logConfigStatus(String configName, UserSettingsPojo.Api value) {
        if (value == null) {
            log.warn("{}: 未注入 (null)", configName);
        } else {
            log.info("{}:", configName);
            logConfigStatus("  - DeepSeek API密钥", value.getDeepseekApiKey());
            logConfigStatus("  - 百度NLP API Key", value.getBaiduNlpApiKey());
            logConfigStatus("  - 百度NLP Secret Key", value.getBaiduNlpSecretKey());
            logConfigStatus("  - HanLP API密钥", value.getHanlpApiKey());
            logConfigStatus("  - 百度地图API密钥", value.getBaiduMapApiKey());
            logConfigStatus("  - QWeather API密钥", value.getQweatherApiKey());
            logConfigStatus("  - QWeather API主机地址", value.getQweatherApiHost());
            logConfigStatus("  - Emoji API密钥", value.getEmojiApiKey());
        }
    }

    /**
     * 安全记录UserSettingsConfig.Api对象（仅显示是否注入成功及数量，不显示具体信息）
     *
     * @param configName 配置项名称
     * @param value      UserSettingsConfig中的Api对象
     */
    public static void logConfigStatusSafely(String configName, UserSettingsConfig.Api value) {
        if (value == null) {
            log.warn("{}: 未注入 (null)", configName);
        } else {
            log.info("{}: 注入成功 (API配置项数量: 8)", configName);
        }
    }

    /**
     * 安全记录UserSettingsPojo.Api对象（仅显示是否注入成功及数量，不显示具体信息）
     *
     * @param configName 配置项名称
     * @param value      UserSettingsPojo中的Api对象
     */
    public static void logConfigStatusSafely(String configName, UserSettingsPojo.Api value) {
        if (value == null) {
            log.warn("{}: 未注入 (null)", configName);
        } else {
            log.info("{}: 注入成功 (API配置项数量: 8)", configName);
        }
    }

    /**
     * 记录布尔型配置项状态的辅助方法
     *
     * @param logger        日志记录器
     * @param configName    配置项名称
     * @param value         配置项值
     * @param expectedValue 期望的值（用于判断配置是否按预期工作）
     */
    public static void logConfigStatus(Logger logger, String configName, boolean value, boolean expectedValue) {
        if (value == expectedValue) {
            logger.info("{}: 注入成功 ({})", configName, value);
        } else {
            logger.warn("{}: 已注入但可能不符合预期 ({})", configName, value);
        }
    }

    /**
     * 使用Slf4j注解的logger直接记录布尔型配置项状态
     *
     * @param configName    配置项名称
     * @param value         配置项值
     * @param expectedValue 期望的值（用于判断配置是否按预期工作）
     */
    public static void logConfigStatus(String configName, boolean value, boolean expectedValue) {
        if (value == expectedValue) {
            log.info("{}: 注入成功 ({})", configName, value);
        } else {
            log.warn("{}: 已注入但可能不符合预期 ({})", configName, value);
        }
    }
}