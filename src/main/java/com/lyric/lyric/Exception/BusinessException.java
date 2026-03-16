package com.lyric.lyric.Exception;

import com.lyric.lyric.Enums.message.BusinessErrorMsgEnums;
import lombok.Getter;

/**
 * 业务异常类
 * 用于表示业务逻辑层面的异常，通常是由于用户输入、业务规则违反等原因导致的异常
 */
@Getter
public class BusinessException extends RuntimeException {

    /**
     * 业务错误消息枚举
     */
    private final BusinessErrorMsgEnums businessErrorMsgEnums;

    /**
     * 使用业务错误枚举构造业务异常
     *
     * @param businessErrorMsgEnums 业务错误枚举
     */
    public BusinessException(BusinessErrorMsgEnums businessErrorMsgEnums) {
        super(businessErrorMsgEnums.getName());
        this.businessErrorMsgEnums = businessErrorMsgEnums;
    }

    /**
     * 使用业务错误枚举和原因异常构造业务异常
     *
     * @param businessErrorMsgEnums 业务错误枚举
     * @param cause         导致此异常的原因异常
     */
    public BusinessException(BusinessErrorMsgEnums businessErrorMsgEnums, Throwable cause) {
        super(businessErrorMsgEnums.getName(), cause);
        this.businessErrorMsgEnums = businessErrorMsgEnums;
    }

    /**
     * 使用错误消息构造业务异常
     *
     * @param message 错误消息
     */
    public BusinessException(String message) {
        super(message);
        this.businessErrorMsgEnums = null;
    }

    /**
     * 使用错误消息和原因异常构造业务异常
     *
     * @param message 错误消息
     * @param cause   导致此异常的原因异常
     */
    public BusinessException(String message, Throwable cause) {
        super(message, cause);
        this.businessErrorMsgEnums = null;
    }
}