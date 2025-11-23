package com.lyric.lyric.Config.message;

import com.lyric.lyric.Enums.message.ErrorMsgEnums;
import com.lyric.lyric.Enums.message.SuccessMsgEnums;
import com.lyric.lyric.Utils.stringFormatConversion.EnumNameConverterUtils;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 消息配置验证类
 * 用于在应用启动时验证消息配置是否成功加载
 */
@Component
public class MessageConfigVerification {
    
    private static final Logger logger = LoggerFactory.getLogger(MessageConfigVerification.class);
    // 从枚举中获取期望的消息键集合
    private static final Set<String> EXPECTED_ERROR_KEYS = Arrays.stream(ErrorMsgEnums.values())
            .map(Enum::name)
            .map(EnumNameConverterUtils::toSnakeCase)
            .collect(Collectors.toSet());
    private static final Set<String> EXPECTED_SUCCESS_KEYS = Arrays.stream(SuccessMsgEnums.values())
            .map(Enum::name)
            .map(EnumNameConverterUtils::toSnakeCase)
            .collect(Collectors.toSet());
    private final MsgConfig msgConfig;
    
    public MessageConfigVerification(MsgConfig msgConfig) {
        this.msgConfig = msgConfig;
    }
    
    /**
     * 在应用启动后验证消息配置是否成功加载
     */
    @PostConstruct
    public void verifyMessageConfig() {
        logger.info("=== 开始验证消息配置加载情况 ===");
        
        try {
            // 验证错误消息配置
            logger.info("错误消息配置项数量: {}", msgConfig.getError().size());
            msgConfig.getError().forEach((key, message) -> 
                logger.info("成功注入错误消息 - {}: code={}, message={}", key, message.getCode(), message.getMessage()));
            
            // 检查缺失的错误消息
            Set<String> missingErrorKeys = new HashSet<>(EXPECTED_ERROR_KEYS);
            missingErrorKeys.removeAll(msgConfig.getError().keySet());
            if (!missingErrorKeys.isEmpty()) {
                logger.warn("以下错误消息注入失败: {}", String.join(", ", missingErrorKeys));
            } else {
                logger.info("所有预期的错误消息均已成功注入");
            }
            
            // 验证成功消息配置
            logger.info("成功消息配置项数量: {}", msgConfig.getSuccess().size());
            msgConfig.getSuccess().forEach((key, message) -> 
                logger.info("成功注入成功消息 - {}: code={}, message={}", key, message.getCode(), message.getMessage()));
            
            // 检查缺失的成功消息
            Set<String> missingSuccessKeys = new HashSet<>(EXPECTED_SUCCESS_KEYS);
            missingSuccessKeys.removeAll(msgConfig.getSuccess().keySet());
            if (!missingSuccessKeys.isEmpty()) {
                logger.warn("以下成功消息注入失败: {}", String.join(", ", missingSuccessKeys));
            } else {
                logger.info("所有预期的成功消息均已成功注入");
            }
            
            logger.info("=== 消息配置验证完成 ===");
        } catch (Exception e) {
            logger.error("消息配置验证失败", e);
        }
    }
}