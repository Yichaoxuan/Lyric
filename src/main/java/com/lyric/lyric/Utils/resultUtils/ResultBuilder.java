package com.lyric.lyric.Utils.resultUtils;

import com.lyric.lyric.Enums.message.BusinessErrorMsgEnums;
import com.lyric.lyric.Enums.message.SuccessMsgEnums;
import com.lyric.lyric.Enums.message.SystemErrorMsgEnums;
import com.lyric.lyric.Config.message.MsgConfig;
import com.lyric.lyric.Pojo.message.MessageConfigPojo;
import com.lyric.lyric.Utils.stringProcessing.EnumUtils;
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
     * 从配置中获取业务错误消息
     * @param businessErrorEnum 业务错误枚举
     * @return 消息配置
     */
    private static MessageConfigPojo.Message getErrorMessageConfig(BusinessErrorMsgEnums businessErrorEnum) {
        MsgConfig msgConfig = getMsgConfig();
        String configKey = EnumUtils.toSnakeCase(businessErrorEnum.name());
        return msgConfig.getItems("businessError").get(configKey);
    }
    
    /**
     * 从配置中获取系统错误消息
     * @param systemErrorEnum 系统错误枚举
     * @return 消息配置
     */
    private static MessageConfigPojo.Message getErrorMessageConfig(SystemErrorMsgEnums systemErrorEnum) {
        MsgConfig msgConfig = getMsgConfig();
        String configKey = EnumUtils.toSnakeCase(systemErrorEnum.name());
        return msgConfig.getItems("systemError").get(configKey);
    }

    /**
     * 从配置中获取成功消息
     * @param successEnum 成功枚举
     * @return 消息配置
     */
    private static MessageConfigPojo.Message getSuccessMessageConfig(SuccessMsgEnums successEnum) {
        MsgConfig msgConfig = getMsgConfig();
        String configKey = EnumUtils.toSnakeCase(successEnum.name());
        return msgConfig.getItems("success").get(configKey);
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
     * 构建业务错误响应结果（无数据）
     *
     * @param businessErrorEnum 业务错误消息枚举
     * @return Result对象
     */
    public static Result<Void> error(BusinessErrorMsgEnums businessErrorEnum) {
        MessageConfigPojo.Message messageConfig = getErrorMessageConfig(businessErrorEnum);
        if (messageConfig != null) {
            return Result.error(messageConfig.getCode(), messageConfig.getMessage());
        }
        // 回退到默认值
        return Result.error("400", businessErrorEnum.name().toLowerCase());
    }

    /**
     * 构建系统错误响应结果（无数据）
     *
     * @param systemErrorEnum 系统错误消息枚举
     * @return Result对象
     */
    public static Result<Void> error(SystemErrorMsgEnums systemErrorEnum) {
        MessageConfigPojo.Message messageConfig = getErrorMessageConfig(systemErrorEnum);
        if (messageConfig != null) {
            return Result.error(messageConfig.getCode(), messageConfig.getMessage());
        }
        // 回退到默认值
        return Result.error("500", systemErrorEnum.name().toLowerCase());
    }

    /**
     * 构建业务错误响应结果（有数据）
     *
     * @param businessErrorEnum 业务错误消息枚举
     * @param data              响应数据
     * @param <T>               数据类型
     * @return Result对象
     */
    public static <T> Result<T> error(BusinessErrorMsgEnums businessErrorEnum, T data) {
        MessageConfigPojo.Message messageConfig = getErrorMessageConfig(businessErrorEnum);
        if (messageConfig != null) {
            return Result.error(messageConfig.getCode(), messageConfig.getMessage(), data);
        }
        // 回退到默认值
        return Result.error("400", businessErrorEnum.name().toLowerCase(), data);
    }

    /**
     * 构建系统错误响应结果（有数据）
     *
     * @param systemErrorEnum 系统错误消息枚举
     * @param data            响应数据
     * @param <T>             数据类型
     * @return Result对象
     */
    public static <T> Result<T> error(SystemErrorMsgEnums systemErrorEnum, T data) {
        MessageConfigPojo.Message messageConfig = getErrorMessageConfig(systemErrorEnum);
        if (messageConfig != null) {
            return Result.error(messageConfig.getCode(), messageConfig.getMessage(), data);
        }
        // 回退到默认值
        return Result.error("500", systemErrorEnum.name().toLowerCase(), data);
    }

    @Override
    public void setApplicationContext(@NotNull ApplicationContext applicationContext) throws BeansException {
        ResultBuilder.applicationContext = applicationContext;
    }
}