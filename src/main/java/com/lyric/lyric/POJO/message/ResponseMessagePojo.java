package com.lyric.lyric.POJO.message;

import lombok.Data;

/**
 * 响应消息实体类
 * 用于存储数据库中的响应消息配置
 */
@Data
public class ResponseMessagePojo {
    /**
     * 响应消息 ID，主键，自增长
     */
    private Integer id;
    
    /**
     * 响应消息键
     */
    private String messageKey;
    
    /**
     * 响应消息类型：success、business-error、system-error
     */
    private String messageType;
    
    /**
     * 响应消息码
     */
    private String code;
    
    /**
     * 响应消息内容
     */
    private String message;
}
