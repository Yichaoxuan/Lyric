package com.lyric.lyric.Service.message;

import com.lyric.lyric.Config.message.MsgConfig;
import com.lyric.lyric.Enums.message.ErrorMsgEnums;
import com.lyric.lyric.Enums.message.SuccessMsgEnums;
import com.lyric.lyric.Pojo.message.MessageConfigPojo;
import com.lyric.lyric.Utils.stringFormatConversion.EnumNameConverterUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.ArrayList;

/**
 * 消息服务类
 * 提供消息配置的动态更新功能
 */
@Service
public class MessageService {
    
    private static final Logger logger = LoggerFactory.getLogger(MessageService.class);
    
    private final MsgConfig msgConfig;
    
    public MessageService(MsgConfig msgConfig) {
        this.msgConfig = msgConfig;
    }
    
    /**
     * 更新消息配置
     * @param messageConfigPojo 新的消息配置
     */
    public void updateMessageConfig(MessageConfigPojo messageConfigPojo) {
        logger.info("开始更新消息配置");
        
        // 更新配置
        msgConfig.updateConfig(messageConfigPojo);
        
        // 重新初始化枚举
        reinitializeEnums();
        
        logger.info("消息配置更新完成");
    }
    
    /**
     * 重新初始化枚举值
     */
    private void reinitializeEnums() {
        logger.info("重新初始化消息枚举");
        
        //定义一个数列存储未找到的枚举实例消息的名字
        List<String> notSuccessFound = new ArrayList<>();
        List<String> notErrorFound = new ArrayList<>();
        
        // 处理错误消息枚举
        for(ErrorMsgEnums errorMsgEnums : ErrorMsgEnums.values()) {
            String configKey = EnumNameConverterUtils.toSnakeCase(errorMsgEnums.name());
            MessageConfigPojo.Message message = msgConfig.getError().get(configKey);
            if (message != null) {
                errorMsgEnums.setCode(message.getCode());
                errorMsgEnums.setMessage(message.getMessage());
            } else {
                notErrorFound.add(errorMsgEnums.name());
            }
        }
        
        // 处理成功消息枚举
        for(SuccessMsgEnums successMsgEnums : SuccessMsgEnums.values()) {
            String configKey = EnumNameConverterUtils.toSnakeCase(successMsgEnums.name());
            MessageConfigPojo.Message message = msgConfig.getSuccess().get(configKey);
            if (message != null) {
                successMsgEnums.setCode(message.getCode());
                successMsgEnums.setMessage(message.getMessage());
            } else {
                notSuccessFound.add(successMsgEnums.name());
            }
        }

        /*
          检查配置加载结果并输出日志
          如果所有配置都加载成功，则打印详细信息
          否则输出警告信息
         */
        if (notErrorFound.isEmpty() && notSuccessFound.isEmpty()) {
            logger.info("=== 枚举实例消息重新加载成功！===");
            printLoadedMessages(); // 调用方法打印详细信息
        } else if (!notSuccessFound.isEmpty()) {
            logger.warn("未找到的 成功消息 枚举实例消息：" + notSuccessFound);
        } else {
            logger.warn("未找到的 失败消息 枚举实例消息：" + notErrorFound);
        }
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