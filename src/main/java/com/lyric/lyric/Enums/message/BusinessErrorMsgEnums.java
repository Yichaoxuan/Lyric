package com.lyric.lyric.Enums.message;

import lombok.Getter;

/**
 * 业务错误消息枚举类
 * 用于定义业务逻辑层面的各种错误情况的错误消息
 */
@Getter
public enum BusinessErrorMsgEnums {

    // 日记模块相关 (包含多媒体)
    DIARY_NOT_FOUND("日记未找到"),
    DIARY_CONTENT_EMPTY("日记内容不能为空"),
    DIARY_TITLE_EMPTY("日记标题不能为空"),
    MEDIA_UPLOAD_FAILED("媒体文件上传失败"),
    MEDIA_TYPE_NOT_SUPPORTED("不支持的媒体文件类型"),
    DIARY_NOT_IN_TRASH("日记不在回收站中"),

    // 标签模块相关
    // BaseTag（基本标签）相关
    BASE_TAG_NOT_FOUND("基本标签不存在"),
    BASE_TAG_CREATE_FAILED("基本标签创建失败"),
    BASE_TAG_UPDATE_FAILED("基本标签更新失败"),
    BASE_TAG_DELETE_FAILED("基本标签删除失败"),
    BASE_TAG_TYPE_INVALID("基本标签类型无效"),
    BASE_TAG_NAME_EMPTY("基本标签名称不能为空"),
    BASE_TAG_ALREADY_EXISTS("基本标签已存在"),

    // PersonTag（人物标签）相关
    PERSON_TAG_NOT_FOUND("人物标签不存在"),
    PERSON_TAG_CREATE_FAILED("人物标签创建失败"),
    PERSON_TAG_UPDATE_FAILED("人物标签更新失败"),
    PERSON_TAG_DELETE_FAILED("人物标签删除失败"),
    PERSON_TAG_NAME_EMPTY("人物名称不能为空"),
    PERSON_TAG_NAME_ALREADY_EXISTS("人物名称已存在"),
    PERSON_TAG_GENDER_INVALID("人物性别无效"),
    PERSON_TAG_RELATION_INVALID("人物关系无效"),

    // LocationTag（地点标签）相关
    LOCATION_TAG_NOT_FOUND("地点标签不存在"),
    LOCATION_TAG_CREATE_FAILED("地点标签创建失败"),
    LOCATION_TAG_UPDATE_FAILED("地点标签更新失败"),
    LOCATION_TAG_DELETE_FAILED("地点标签删除失败"),
    LOCATION_TAG_NAME_EMPTY("地点名称不能为空"),
    LOCATION_TAG_ALREADY_EXISTS("地点标签已存在"),
    LOCATION_TAG_COORDINATES_INVALID("地点坐标无效"),
    LOCATION_TAG_PROVINCE_INVALID("省份信息无效"),
    LOCATION_TAG_CITY_INVALID("城市信息无效"),
    LOCATION_TAG_COUNTRY_INVALID("国家信息无效"),

    // EventTag（事件标签）相关
    TOG_EVENT_NOT_FOUND("父事件不存在"),
    TOG_EVENT_CREATE_FAILED("父事件创建失败"),
    TOG_EVENT_UPDATE_FAILED("父事件更新失败"),
    TOG_EVENT_DELETE_FAILED("父事件删除失败"),
    TOG_EVENT_DATE_INVALID("父事件日期无效"),
    TOG_EVENT_NAME_EMPTY("父事件名称不能为空"),
    SUB_EVENT_NOT_FOUND("子事件不存在"),
    SUB_EVENT_CREATE_FAILED("子事件创建失败"),
    SUB_EVENT_UPDATE_FAILED("子事件更新失败"),
    SUB_EVENT_DELETE_FAILED("子事件删除失败"),
    SUB_EVENT_DATE_INVALID("子事件日期无效"),
    SUB_EVENT_NAME_EMPTY("子事件名称不能为空"),
    SUB_EVENT_TOG_EVENT_NOT_FOUND("所属父事件不存在"),
    CASCADE_DELETE_FAILED("级联删除失败"),


    // 文件上传相关
    FILE_EMPTY("文件不能为空"),
    FILE_SIZE_EXCEEDED("文件大小超过限制"),
    FILE_TYPE_NOT_SUPPORTED("不支持的文件类型"),
    FILE_UPLOAD_FAILED("文件上传失败"),
    FILE_NOT_FOUND("文件不存在"),
    // AI 模块
    AI_PROCESSING_ERROR("AI 处理失败"),
    AI_MODEL_NOT_AVAILABLE("AI 模型不可用"),
    AI_REQUEST_TIMEOUT("AI 请求超时"),

    // 用户设置
    USER_SETTING_UPDATE_FAILED("用户设置更新失败"),
    USER_PREFERENCE_INVALID("用户偏好设置无效"),
    RESPONSE_MESSAGE_COMMAND_NOT_INPUT("响应消息命令未输入");

    /**
     * 错误消息名称
     */
    private final String name;

    /**
     * 业务错误消息枚举构造函数
     * 
     * @param name 错误名字
     */
    BusinessErrorMsgEnums(String name) {
        this.name = name;
    }
}