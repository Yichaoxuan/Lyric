package com.lyric.lyric.Config.message;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;


/**
 * 消息枚举类的配置类
 * 
 * @since 2025-11-23
 */
@Data
@Component
@ConfigurationProperties(prefix = "response-message")
public class MsgConfig {

    private Map<String, Message> error = new HashMap<>();
    
    private Map<String, Message> success = new HashMap<>();

    @Data
    public static class Message {
        // 响应码（对应配置文件中的 code）
        private String code;
        // 用户消息（对应配置文件中的 message）
        private String message;
    }
    
    /**
     * 获取错误消息配置项
     * @return 错误消息配置项映射
     */
    public Map<String, Message> getError() {
        return error;
    }
    
    /**
     * 获取成功消息配置项
     * @return 成功消息配置项映射
     */
    public Map<String, Message> getSuccess() {
        return success;
    }
}