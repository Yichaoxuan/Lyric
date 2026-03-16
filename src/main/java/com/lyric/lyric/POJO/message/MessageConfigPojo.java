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

    /**
     * 获取格式化的消息配置字符串
     * @param isEnter 是否在开头添加换行符
     * @return 格式化的消息配置字符串
     */
    public String getMessageConfigStr(Boolean isEnter) {
        StringBuilder sb = new StringBuilder();
        if (isEnter) {
            sb.append("\n");
        }
        
        // 业务错误消息
        sb.append("=== 业务错误消息 ===\n");
        if (businessError != null && !businessError.isEmpty()) {
            for (Map.Entry<String, Message> entry : businessError.entrySet()) {
                sb.append("  - ").append(entry.getKey())
                  .append(": [")
                  .append(entry.getValue().getCode())
                  .append("] ")
                  .append(entry.getValue().getMessage())
                  .append("\n");
            }
        } else {
            sb.append("  (无配置)\n");
        }
        
        // 系统错误消息
        sb.append("=== 系统错误消息 ===\n");
        if (systemError != null && !systemError.isEmpty()) {
            for (Map.Entry<String, Message> entry : systemError.entrySet()) {
                sb.append("  - ").append(entry.getKey())
                  .append(": [")
                  .append(entry.getValue().getCode())
                  .append("] ")
                  .append(entry.getValue().getMessage())
                  .append("\n");
            }
        } else {
            sb.append("  (无配置)\n");
        }
        
        // 成功消息
        sb.append("=== 成功消息 ===\n");
        if (success != null && !success.isEmpty()) {
            for (Map.Entry<String, Message> entry : success.entrySet()) {
                sb.append("  - ").append(entry.getKey())
                  .append(": [")
                  .append(entry.getValue().getCode())
                  .append("] ")
                  .append(entry.getValue().getMessage())
                  .append("\n");
            }
        } else {
            sb.append("  (无配置)");
        }
        
        return sb.toString();
    }

    @Data
    @Getter
    @Setter
    public static class Message {
        private String code;
        private String message;
    }
}