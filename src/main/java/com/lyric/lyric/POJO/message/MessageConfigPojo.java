package com.lyric.lyric.POJO.message;

import lombok.*;

import java.util.Map;

/**
 * 消息配置POJO类
 * 用于运行时动态更新消息配置
 */
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MessageConfigPojo {
    private Map<String, Message> businessError;
    private Map<String, Message> systemError;
    private Map<String, Message> success;

    public Message getBusinessErrorMessage(String key) {
        return businessError.get(key);
    }
    
    public Message getSystemErrorMessage(String key) {
        return systemError.get(key);
    }

    public Message getSuccessMessage(String key) {
        return success.get(key);
    }

    @Data
    @Getter
    @Setter
    public static class Message {
        private String code;
        private String message;
    }
}