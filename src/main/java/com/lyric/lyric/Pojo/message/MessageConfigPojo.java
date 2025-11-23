package com.lyric.lyric.Pojo.message;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

/**
 * 消息配置POJO类
 * 用于运行时动态更新消息配置
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MessageConfigPojo {
    private Map<String, Message> error;
    private Map<String, Message> success;

    @Getter
    @Setter
    public static class Message {
        private String code;
        private String message;
    }
}