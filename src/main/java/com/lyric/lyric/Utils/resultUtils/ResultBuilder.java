package com.lyric.lyric.Utils.resultUtils;

import com.lyric.lyric.Enums.message.ErrorMsgEnums;
import com.lyric.lyric.Enums.message.SuccessMsgEnums;

/**
 * 统一响应结果构建器
 * <p>根据消息枚举类构建对应的统一响应Result</p>
 *
 * @since 2025-11-23
 */
public class ResultBuilder {

    /**
     * 构建成功响应结果（无数据）
     *
     * @param successEnum 成功消息枚举
     * @return Result对象
     */
    public static Result<Void> success(SuccessMsgEnums successEnum) {
        return Result.success(successEnum.getCode(), successEnum.getMessage());
    }

    /**
     * 构建成功响应结果（有数据）
     *
     * @param successEnum 成功消息枚举
     * @param data        响应数据
     * @param <T>         数据类型
     * @return Result对象
     */
    public static <T> Result<T> successWithData(SuccessMsgEnums successEnum, T data) {
        return Result.success(successEnum.getCode(), data);
    }

    /**
     * 构建成功响应结果（有数据，消息）
     *
     * @param successEnum 成功消息枚举
     * @param data        响应数据
     * @param <T>         数据类型
     * @return Result对象
     */
    public static <T> Result<T> successWithDataAndMessage(SuccessMsgEnums successEnum, T data) {
        return Result.success(successEnum.getCode(), data, successEnum.getMessage());
    }

    /**
     * 构建错误响应结果（无数据）
     *
     * @param errorEnum 错误消息枚举
     * @return Result对象
     */
    public static Result<Void> error(ErrorMsgEnums errorEnum) {
        return Result.error(errorEnum.getCode(), errorEnum.getMessage());
    }

    /**
     * 构建错误响应结果（有数据）
     *
     * @param errorEnum 错误消息枚举
     * @param data      响应数据
     * @param <T>       数据类型
     * @return Result对象
     */
    public static <T> Result<T> error(ErrorMsgEnums errorEnum, T data) {
        return Result.error(errorEnum.getCode(), errorEnum.getMessage(), data);
    }
}
