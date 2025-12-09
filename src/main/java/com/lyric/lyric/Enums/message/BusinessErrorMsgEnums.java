package com.lyric.lyric.Enums.message;

import lombok.Getter;

/**
 * 业务错误消息枚举类
 * 用于定义业务逻辑层面的各种错误情况的错误消息
 */
@Getter
public enum BusinessErrorMsgEnums {

    //日记模块相关(包含多媒体)
    DIARY_NOT_FOUND("日记未找到"),
    DIARY_CONTENT_EMPTY("日记内容不能为空"),
    DIARY_TITLE_EMPTY("日记标题不能为空"),
    MEDIA_UPLOAD_FAILED("媒体文件上传失败"),
    MEDIA_TYPE_NOT_SUPPORTED("不支持的媒体文件类型"),
    DIARY_NOT_IN_TRASH("日记不在回收站中"),
    
    //AI模块
    AI_PROCESSING_ERROR("AI处理失败"),
    AI_MODEL_NOT_AVAILABLE("AI模型不可用"),
    AI_REQUEST_TIMEOUT("AI请求超时"),
    
    //用户设置
    USER_SETTING_UPDATE_FAILED("用户设置更新失败"),
    USER_PREFERENCE_INVALID("用户偏好设置无效"),
    RESPONSE_MESSAGE_COMMAND_NOT_INPUT("响应消息命令未输入");

    /**
     * 错误消息名称
     */
    private final String name;

    /**
     * 业务错误消息枚举构造函数
     * @param name 错误名字
     */
    BusinessErrorMsgEnums(String name) {
        this.name = name;
    }
}