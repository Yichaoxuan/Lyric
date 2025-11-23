package com.lyric.lyric.Utils;

import com.lyric.lyric.Enums.message.ErrorMsgEnums;
import com.lyric.lyric.Enums.message.SuccessMsgEnums;

/**
 * 消息工具类
 * 提供获取成功和错误消息的便捷方法
 */
public class MessageUtils {

    /**
     * 获取成功消息
     * @param successEnum 成功消息枚举
     * @return 消息内容
     */
    public static String getSuccessMessage(SuccessMsgEnums successEnum) {
        return successEnum.getMessage();
    }

    /**
     * 获取成功状态码
     * @param successEnum 成功消息枚举
     * @return 状态码
     */
    public static String getSuccessCode(SuccessMsgEnums successEnum) {
        return successEnum.getCode();
    }

    /**
     * 获取错误消息
     * @param errorEnum 错误消息枚举
     * @return 消息内容
     */
    public static String getErrorMessage(ErrorMsgEnums errorEnum) {
        return errorEnum.getMessage();
    }

    /**
     * 获取错误状态码
     * @param errorEnum 错误消息枚举
     * @return 状态码
     */
    public static String getErrorCode(ErrorMsgEnums errorEnum) {
        return errorEnum.getCode();
    }
}