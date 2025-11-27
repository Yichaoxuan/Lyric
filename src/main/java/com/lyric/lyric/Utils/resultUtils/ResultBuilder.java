package com.lyric.lyric.Utils.resultUtils;

import com.lyric.lyric.Enums.message.ErrorMsgEnums;
import com.lyric.lyric.Enums.message.SuccessMsgEnums;
import com.lyric.lyric.Config.message.MsgConfig;
import com.lyric.lyric.Pojo.message.MessageConfigPojo;
import com.lyric.lyric.Utils.stringFormatConversion.EnumNameConverterUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * 统一响应结果构建器
 * <p>根据消息枚举类构建对应的统一响应Result</p>
 *
 * @since 2025-11-23
 */
@Component
public class ResultBuilder implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    /**
     * 获取消息配置
     * @return 消息配置实例
     */
    private static MsgConfig getMsgConfig() {
        return applicationContext.getBean(MsgConfig.class);
    }

    /**
     * 从配置中获取错误消息
     * @param errorEnum 错误枚举
     * @return 消息配置
     */
    private static MessageConfigPojo.Message getErrorMessageConfig(ErrorMsgEnums errorEnum) {
        MsgConfig msgConfig = getMsgConfig();
        String configKey = EnumNameConverterUtils.toSnakeCase(errorEnum.name());
        return msgConfig.getError().get(configKey);
    }

    /**
     * 从配置中获取成功消息
     * @param successEnum 成功枚举
     * @return 消息配置
     */
    private static MessageConfigPojo.Message getSuccessMessageConfig(SuccessMsgEnums successEnum) {
        MsgConfig msgConfig = getMsgConfig();
        String configKey = EnumNameConverterUtils.toSnakeCase(successEnum.name());
        return msgConfig.getSuccess().get(configKey);
    }

    /**
     * 构建成功响应结果（无数据）
     *
     * @param successEnum 成功消息枚举
     * @return Result对象
     */
    public static Result<Void> success(SuccessMsgEnums successEnum) {
        MessageConfigPojo.Message messageConfig = getSuccessMessageConfig(successEnum);
        if (messageConfig != null) {
            return Result.success(messageConfig.getCode(), messageConfig.getMessage());
        }
        // 回退到默认值
        return Result.success("200", successEnum.name().toLowerCase());
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
        MessageConfigPojo.Message messageConfig = getSuccessMessageConfig(successEnum);
        if (messageConfig != null) {
            return Result.success(messageConfig.getCode(), data);
        }
        // 回退到默认值
        return Result.success("200", data);
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
        MessageConfigPojo.Message messageConfig = getSuccessMessageConfig(successEnum);
        if (messageConfig != null) {
            return Result.success(messageConfig.getCode(), data, messageConfig.getMessage());
        }
        // 回退到默认值
        return Result.success("200", data, successEnum.name().toLowerCase());
    }

    /**
     * 构建错误响应结果（无数据）
     *
     * @param errorEnum 错误消息枚举
     * @return Result对象
     */
    public static Result<Void> error(ErrorMsgEnums errorEnum) {
        MessageConfigPojo.Message messageConfig = getErrorMessageConfig(errorEnum);
        if (messageConfig != null) {
            return Result.error(messageConfig.getCode(), messageConfig.getMessage());
        }
        // 回退到默认值
        return Result.error("500", errorEnum.name().toLowerCase());
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
        MessageConfigPojo.Message messageConfig = getErrorMessageConfig(errorEnum);
        if (messageConfig != null) {
            return Result.error(messageConfig.getCode(), messageConfig.getMessage(), data);
        }
        // 回退到默认值
        return Result.error("500", errorEnum.name().toLowerCase(), data);
    }

    @Override
    public void setApplicationContext(@NotNull ApplicationContext applicationContext) throws BeansException {
        ResultBuilder.applicationContext = applicationContext;
    }
}
