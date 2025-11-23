package com.lyric.lyric.Config.message;

import com.lyric.lyric.Pojo.message.MessageConfigPojo;
import lombok.Data;
import lombok.Getter;
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
@Getter
@Component
@ConfigurationProperties(prefix = "response-message")
public class MsgConfig {

    /**
     *  将错误消息配置项保存为Map
     *
     */
    private Map<String, MessageConfigPojo.Message> error = new HashMap<>();

    /**
     *  将成功消息配置项保存为Map
     *
     */
    private Map<String, MessageConfigPojo.Message> success = new HashMap<>();

    /**
     * 根据消息类型获取对应的配置映射
     * @param messageType 消息类型 ("error" 或 "success")
     * @return 对应的配置映射
     */
    public Map<String, MessageConfigPojo.Message> getItems(String messageType) {
        if ("error".equals(messageType)) {
            return this.error;
        } else if ("success".equals(messageType)) {
            return this.success;
        }
        return new HashMap<>();
    }

    /**
     * 更新消息配置
     * @param configPojo 包含新配置的POJO对象
     */
    public void updateConfig(MessageConfigPojo configPojo) {
        this.error.clear();
        this.error.putAll(configPojo.getError());

        this.success.clear();
        this.success.putAll(configPojo.getSuccess());
    }


}