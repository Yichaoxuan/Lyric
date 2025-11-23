package com.lyric.lyric.Initializer.message;

import com.lyric.lyric.Config.message.MsgConfig;
import com.lyric.lyric.Enums.message.ErrorMsgEnums;
import com.lyric.lyric.Enums.message.SuccessMsgEnums;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 枚举值初始化器
 *
 * <p>在应用启动后执行配置注入</p>
 * <p>将配置文件内容动态设置到枚举实例中</p>
 * 
 * @since 2025-11-23
 */
@Component
public class MsgInitializer {

    private static final Logger logger = LoggerFactory.getLogger(MsgInitializer.class);

    @Autowired
    private MsgConfig msgConfig;

    /**
     * 初始化方法（应用启动时自动执行）
     *
     * <p>执行流程：</p>
     * 1. 遍历所有 ErrorCode 枚举实例
     * 2. 将枚举名称转换为配置键格式（大写转小写下划线）
     * 3. 从配置中查找对应条目
     * 4. 将配置值注入枚举实例
     */
    @PostConstruct
    public void init() {

        //定义一个数列存储未找到的枚举实例消息的名字
        List<String> notSuccessFound = new ArrayList<>();
        List<String> notErrorFound = new ArrayList<>();

        // 处理错误消息枚举
        for(ErrorMsgEnums errorMsgEnums : ErrorMsgEnums.values()) {
            String configKey = toSnakeCase(errorMsgEnums.name());
            MsgConfig.Message message = msgConfig.getError().get(configKey);
            if (message != null) {
                errorMsgEnums.setCode(message.getCode());
                errorMsgEnums.setMessage(message.getMessage());
            } else {
                notErrorFound.add(errorMsgEnums.name());
            }
        }

        // 处理成功消息枚举
        for(SuccessMsgEnums successMsgEnums : SuccessMsgEnums.values()) {
            String configKey = toSnakeCase(successMsgEnums.name());
            MsgConfig.Message message = msgConfig.getSuccess().get(configKey);
            if (message != null) {
                successMsgEnums.setCode(message.getCode());
                successMsgEnums.setMessage(message.getMessage());
            } else {
                notSuccessFound.add(successMsgEnums.name());
            }
        }

        /**
         * 检查配置加载结果并输出日志
         * 如果所有配置都加载成功，则打印详细信息
         * 否则输出警告信息
         */
        if (notErrorFound.isEmpty() && notSuccessFound.isEmpty()) {

            logger.info("=== 枚举实例消息加载成功！===");
            printLoadedMessages(); // 调用方法打印详细信息

        } else if (!notSuccessFound.isEmpty()) {

            logger.warn("未找到的 成功消息 枚举实例消息：" + notSuccessFound);

        } else {

            logger.warn("未找到的 失败消息 枚举实例消息：" + notErrorFound);

        }
    }

    /**
     * 转换枚举名称到配置键格式
     *
     * <p>转换规则：</p>
     * 1. 转为小写
     * 2. 下划线分割（兼容枚举命名规范）
     * 
     * @param enumName 枚举名称
     * @return 转换后的snake_case格式字符串
     */
    private String toSnakeCase(String enumName) {
        return enumName.replaceAll("([a-z])([A-Z])", "$1_$2")
                .toLowerCase();
    }
    
    /**
     * 打印已加载的消息配置详情
     * 
     * <p>分别打印已加载的错误消息和成功消息的详细信息</p>
     */
    private void printLoadedMessages() {
        logger.info("=== 已加载的错误消息 ===");
        for (ErrorMsgEnums errorMsgEnum : ErrorMsgEnums.values()) {
            logger.info("错误码: {}, 消息: {}, 枚举: {}", 
                       errorMsgEnum.getCode(), errorMsgEnum.getMessage(), errorMsgEnum.name());
        }
        
        logger.info("=== 已加载的成功消息 ===");
        for (SuccessMsgEnums successMsgEnum : SuccessMsgEnums.values()) {
            logger.info("成功码: {}, 消息: {}, 枚举: {}", 
                       successMsgEnum.getCode(), successMsgEnum.getMessage(), successMsgEnum.name());
        }
    }
}