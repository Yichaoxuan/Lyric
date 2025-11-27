package com.lyric.lyric.Service.message;

import com.lyric.lyric.Config.message.MsgConfig;
import com.lyric.lyric.Enums.message.ErrorMsgEnums;
import com.lyric.lyric.Enums.message.SuccessMsgEnums;
import com.lyric.lyric.Pojo.message.MessageConfigPojo;
import com.lyric.lyric.Service.contentAnalysis.AIAnalysisService;
import com.lyric.lyric.Utils.json.JsonUtils;
import com.lyric.lyric.Utils.resultUtils.Result;
import com.lyric.lyric.Utils.resultUtils.ResultBuilder;
import com.lyric.lyric.Utils.stringFormatConversion.EnumNameConverterUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

/**
 * 响应消息服务类
 * 提供响应消息配置的动态更新功能
 */
@Service
public class MessageService {
    
    private static final Logger logger = LoggerFactory.getLogger(MessageService.class);
    
    private final MsgConfig msgConfig;

    private final AIAnalysisService aiAnalysisService;
    
    public MessageService(MsgConfig msgConfig, AIAnalysisService aiAnalysisService) {
        this.msgConfig = msgConfig;
        this.aiAnalysisService = aiAnalysisService;
    }

    /**
     * 更新响应消息配置并保存到配置文件
     * @param newMessageConfigInstructions 新地响应消息配置
     */
    public Result<Void> updateMessageConfigAndSaveToFile(String newMessageConfigInstructions) {

        //判断新响应消息配置命令是否为空
        if (newMessageConfigInstructions == null || newMessageConfigInstructions.isEmpty()) {
            logger.info("新响应消息配置命令为空，更新操作取消");
            return ResultBuilder.error(ErrorMsgEnums.RESPONSE_MESSAGE_COMMAND_NOT_INPUT);
        }

        //拼接新响应消息配置命令与旧响应消息配置
        newMessageConfigInstructions = newMessageConfigInstructions + "\n" + JsonUtils.toJson(getLatestMessageConfig());

        System.out.println(newMessageConfigInstructions);

        try {
            logger.info("开始更新响应消息配置并保存到文件");

            //调用AI 模型更新响应消息配置
            MessageConfigPojo messageConfigPojo = aiAnalysisService.generateResponseMessage(newMessageConfigInstructions);

            // 更新配置并保存到文件
            msgConfig.updateConfigAndSaveToFile(messageConfigPojo);

            // 重新初始化枚举
            reinitializeEnums();

            logger.info("响应消息配置更新并保存到文件完成");

            return ResultBuilder.success(SuccessMsgEnums.MESSAGE_CONFIG_SUCCESS);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 获取最新地响应消息配置
     */
    public MessageConfigPojo getLatestMessageConfig() {
        return msgConfig.getLatestConfig();
    }

    /**
     * 获取最新的错误响应消息配置
     */
    public MessageConfigPojo.Message getLatestErrorMessageConfig(ErrorMsgEnums errorMsgEnums) {
        String configKey = EnumNameConverterUtils.toSnakeCase(errorMsgEnums.name());
        return msgConfig.getLatestErrorConfig().getErrorMessage(configKey);
    }

    /**
     * 获取最新的成功响应消息配置
     */
    public MessageConfigPojo.Message getLatestSuccessMessageConfig(SuccessMsgEnums successMsgEnums) {
        String configKey = EnumNameConverterUtils.toSnakeCase(successMsgEnums.name());
        return msgConfig.getLatestSuccessConfig().getSuccessMessage(configKey);
    }

    /**
     * 重新初始化枚举值
     */
    private void reinitializeEnums() {
        logger.info("重新初始化响应消息枚举");
        
        //定义一个数列存储未找到的枚举实例响应消息的名字
        List<String> notSuccessFound = new ArrayList<>();
        List<String> notErrorFound = new ArrayList<>();
        
        // 处理错误响应消息枚举
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
        
        // 处理成功响应消息枚举
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
            logger.info("=== 枚举实例响应消息重新加载成功！===");
            printLoadedMessages(); // 调用方法打印详细信息
        } else if (!notSuccessFound.isEmpty()) {
            logger.warn("未找到的 成功响应消息 枚举实例响应消息：{}", notSuccessFound);
        } else {
            logger.warn("未找到的 失败响应消息 枚举实例响应消息：{}", notErrorFound);
        }
    }
    
    /**
     * 打印已加载的响应消息配置详情
     *
     * <p>分别打印已加载的错误响应消息和成功响应消息的详细信息</p>
     */
    private void printLoadedMessages() {
        logger.info("=== 已加载的错误响应消息 ===");
        for (ErrorMsgEnums errorMsgEnum : ErrorMsgEnums.values()) {
            logger.info("错误码: {}, 响应消息: {}, 枚举: {}",
                    errorMsgEnum.getCode(), errorMsgEnum.getMessage(), errorMsgEnum.name());
        }
        
        logger.info("=== 已加载的成功响应消息 ===");
        for (SuccessMsgEnums successMsgEnum : SuccessMsgEnums.values()) {
            logger.info("成功码: {}, 响应消息: {}, 枚举: {}",
                    successMsgEnum.getCode(), successMsgEnum.getMessage(), successMsgEnum.name());
        }
    }
}