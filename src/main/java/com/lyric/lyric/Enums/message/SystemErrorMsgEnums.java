package com.lyric.lyric.Enums.message;

import lombok.Getter;

/**
 * 系统错误消息枚举类
 * 用于定义系统级别的各种错误情况的错误消息
 */
@Getter
public enum SystemErrorMsgEnums {

    //系统错误
    SYSTEM_ERROR("系统内部错误"),
    DATABASE_ERROR("数据库操作失败"),
    NETWORK_ERROR("网络连接异常");

    /**
     * 错误消息名称
     */
    private final String name;

    /**
     * 系统错误消息枚举构造函数
     * @param name 错误名字
     */
    SystemErrorMsgEnums(String name) {
        this.name = name;
    }
}