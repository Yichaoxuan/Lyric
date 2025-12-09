package com.lyric.lyric.Exception;

import com.lyric.lyric.Enums.message.SystemErrorMsgEnums;
import lombok.Getter;

/**
 * 系统异常类
 * 用于表示系统级别的异常，通常是由系统错误、资源不可用等非业务逻辑错误引起的异常
 */
@Getter
public class SystemException extends RuntimeException {

    /**
     * 系统错误消息枚举
     */
    private final SystemErrorMsgEnums systemErrorMsgEnums;

    /**
     * 使用系统错误枚举构造系统异常
     *
     * @param systemErrorMsgEnums 系统错误枚举
     */
    public SystemException(SystemErrorMsgEnums systemErrorMsgEnums) {
        super(systemErrorMsgEnums.getName());
        this.systemErrorMsgEnums = systemErrorMsgEnums;
    }

    /**
     * 使用系统错误枚举和原因异常构造系统异常
     *
     * @param systemErrorMsgEnums 系统错误枚举
     * @param cause         引起该异常的原始异常
     */
    public SystemException(SystemErrorMsgEnums systemErrorMsgEnums, Throwable cause) {
        super(systemErrorMsgEnums.getName(), cause);
        this.systemErrorMsgEnums = systemErrorMsgEnums;
    }
}