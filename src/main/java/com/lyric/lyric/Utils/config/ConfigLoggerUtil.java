package com.lyric.lyric.Utils.config;

import org.slf4j.Logger;

import java.util.Map;

/**
 * 配置日志工具类
 * 用于统一记录配置项的加载状态，避免敏感信息泄露
 */
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
        } else if (configName.contains("API密钥") || configName.contains("API Key") ||
                configName.contains("Secret") || configName.contains("密钥")) {
            // 对于敏感信息，只记录是否已设置，而不显示具体内容
            logger.info("{}: 注入成功 (已设置)", configName);
        } else if (value instanceof Map<?, ?> mapValue) {
            if (mapValue.isEmpty()) {
                logger.warn("{}: 未注入 (空集合)", configName);
            } else {
                logger.info("{}: 注入成功 (大小: {})", configName, mapValue.size());
            }
        } else {
            logger.info("{}: 注入成功 ({})", configName, value);
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
}