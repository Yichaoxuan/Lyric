package com.lyric.lyric.Service.message;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.lyric.lyric.Config.message.MsgConfig;
import com.lyric.lyric.Enums.message.BusinessErrorMsgEnums;
import com.lyric.lyric.Enums.message.SuccessMsgEnums;
import com.lyric.lyric.Enums.message.SystemErrorMsgEnums;
import com.lyric.lyric.Exception.BusinessException;
import com.lyric.lyric.Exception.SystemException;
import com.lyric.lyric.POJO.message.MessageConfigPojo;
import com.lyric.lyric.Service.contentAnalysis.AIAnalysisService;
import com.lyric.lyric.Utils.resultUtils.ResultBuilder;
import com.lyric.lyric.Utils.stringProcessing.EnumUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;

/**
 * 响应消息服务类
 * 提供响应消息配置的动态更新功能
 *
 * @author Yichaoxuan
 * @since 2026/03/19
 */
@Slf4j
@Service
public class MessageService {
    
    private final MsgConfig msgConfig;

    private final AIAnalysisService aiAnalysisService;
    
    public MessageService(MsgConfig msgConfig, AIAnalysisService aiAnalysisService) {
        this.msgConfig = msgConfig;
        this.aiAnalysisService = aiAnalysisService;
    }

    /**
     * 更新响应消息配置并保存到数据库
     * @param responseStyleInstructions 响应消息的角色设定
     */
    public com.lyric.lyric.Utils.resultUtils.Result<Void> updateMessageConfigAndSaveToFile(String responseStyleInstructions) {

        //判断新响应消息配置命令是否为空
        if (responseStyleInstructions == null || responseStyleInstructions.isEmpty()) {
            log.info("新响应消息配置命令为空，更新操作取消");
            return ResultBuilder.error(BusinessErrorMsgEnums.RESPONSE_MESSAGE_COMMAND_NOT_INPUT);
        }

        //拼接新响应消息配置命令与旧响应消息配置
        responseStyleInstructions = responseStyleInstructions + "\n" + com.lyric.lyric.Utils.json.JsonConversionUtils.toJson(getLatestMessageConfig());

        System.out.println(responseStyleInstructions);

        try {
            log.info("开始更新响应消息配置并保存到数据库");
        
            //调用 AI 模型更新响应消息配置
            MessageConfigPojo messageConfigPojo = aiAnalysisService.generateResponseMessage(responseStyleInstructions);
        
            // 检查 AI 返回的配置是否为 null
            if (messageConfigPojo == null) {
                log.error("AI 生成的消息配置为空，可能是 AI 返回的结果不是有效的 JSON 格式");
                return ResultBuilder.error(BusinessErrorMsgEnums.RESPONSE_MESSAGE_COMMAND_NOT_INPUT);
            }
        
            // 更新配置并保存到数据库
            msgConfig.updateConfigAndSaveToDatabase(messageConfigPojo);
        
            // 重新初始化枚举
            reinitializeEnums();
        
            log.info("响应消息配置更新并保存到数据库完成");
        
            return ResultBuilder.success(SuccessMsgEnums.MESSAGE_CONFIG_SUCCESS);
        } catch (IllegalArgumentException e) {
            log.error("配置参数无效：{}", e.getMessage());
            throw new BusinessException(BusinessErrorMsgEnums.RESPONSE_MESSAGE_COMMAND_NOT_INPUT, e);
        }
    }


    /**
     * 获取最新地响应消息配置
     */
    public MessageConfigPojo getLatestMessageConfig() {
        return msgConfig.getLatestConfig();
    }

    /**
     * 获取最新的业务错误响应消息配置
     */
    public MessageConfigPojo.Message getLatestBusinessErrorMessageConfig(BusinessErrorMsgEnums businessErrorMsgEnums) {
        String configKey = EnumUtils.toSnakeCase(businessErrorMsgEnums.name());
        return msgConfig.getLatestBusinessErrorConfig().getBusinessErrorMessage(configKey);
    }
    
    /**
     * 获取最新的系统错误响应消息配置
     */
    public MessageConfigPojo.Message getLatestSystemErrorMessageConfig(SystemErrorMsgEnums systemErrorMsgEnums) {
        String configKey = EnumUtils.toSnakeCase(systemErrorMsgEnums.name());
        return msgConfig.getLatestSystemErrorConfig().getSystemErrorMessage(configKey);
    }

    /**
     * 获取最新的成功响应消息配置
     */
    public MessageConfigPojo.Message getLatestSuccessMessageConfig(SuccessMsgEnums successMsgEnums) {
        String configKey = EnumUtils.toSnakeCase(successMsgEnums.name());
        return msgConfig.getLatestSuccessConfig().getSuccessMessage(configKey);
    }

    /**
     * 读取 YAML 配置文件内容
     * @return YAML 文件的原始字符串内容
     */
    public String getYamlConfigContent() {
        try {
            ClassPathResource resource = new ClassPathResource("message-config.yml");
            byte[] bytes = resource.getInputStream().readAllBytes();
            return new String(bytes);
        } catch (Exception e) {
            log.error("读取 YAML 配置文件失败：{}", e.getMessage());
            throw new SystemException(SystemErrorMsgEnums.DATABASE_ERROR, e);
        }
    }

    /**
     * 解析 YAML 配置文件并返回结构化的 Map
     * @return 解析后的 Map 对象
     */
    @SuppressWarnings("unchecked")
    public Map<String, Object> parseYamlConfig() {
        try {
            String yamlContent = getYamlConfigContent();
            ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());
            return yamlMapper.readValue(yamlContent, Map.class);
        } catch (Exception e) {
            log.error("解析 YAML 配置文件失败：{}", e.getMessage());
            throw new SystemException(SystemErrorMsgEnums.DATABASE_ERROR, e);
        }
    }

    /**
     * 重新初始化枚举值
     */
    private void reinitializeEnums() {
        log.info("重新初始化响应消息枚举");
        
        //定义一个数列存储未找到的枚举实例响应消息的名字
        List<String> notBusinessErrorFound = new ArrayList<>();
        List<String> notSystemErrorFound = new ArrayList<>();
        List<String> notSuccessFound = new ArrayList<>();
        
        // 处理业务错误响应消息枚举
        for(BusinessErrorMsgEnums businessErrorMsgEnums : BusinessErrorMsgEnums.values()) {
            String configKey = EnumUtils.toSnakeCase(businessErrorMsgEnums.name());
            MessageConfigPojo.Message message = msgConfig.getBusinessError().get(configKey);
            if (message == null) {
                notBusinessErrorFound.add(businessErrorMsgEnums.name());
            }
        }
        
        // 处理系统错误响应消息枚举
        for(SystemErrorMsgEnums systemErrorMsgEnums : SystemErrorMsgEnums.values()) {
            String configKey = EnumUtils.toSnakeCase(systemErrorMsgEnums.name());
            MessageConfigPojo.Message message = msgConfig.getSystemError().get(configKey);
            if (message == null) {
                notSystemErrorFound.add(systemErrorMsgEnums.name());
            }
        }
        
        // 处理成功响应消息枚举
        for(SuccessMsgEnums successMsgEnums : SuccessMsgEnums.values()) {
            String configKey = EnumUtils.toSnakeCase(successMsgEnums.name());
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
        if (notBusinessErrorFound.isEmpty() && notSystemErrorFound.isEmpty() && notSuccessFound.isEmpty()) {
            log.info("=== 枚举实例响应消息重新加载成功！===");
            printLoadedMessages(); // 调用方法打印详细信息
        } else {
            if (!notBusinessErrorFound.isEmpty()) {
                log.warn("未找到的 业务失败响应消息 枚举实例响应消息：{}", notBusinessErrorFound);
            }
            if (!notSystemErrorFound.isEmpty()) {
                log.warn("未找到的 系统失败响应消息 枚举实例响应消息：{}", notSystemErrorFound);
            }
            if (!notSuccessFound.isEmpty()) {
                log.warn("未找到的 成功响应消息 枚举实例响应消息：{}", notSuccessFound);
            }
        }
    }
    
    /**
     * 打印已加载的响应消息配置详情
     *
     * <p>分别打印已加载的错误响应消息和成功响应消息的详细信息</p>
     */
    private void printLoadedMessages() {
        log.info("=== 已加载的业务错误响应消息 ===");
        for (BusinessErrorMsgEnums businessErrorMsgEnum : BusinessErrorMsgEnums.values()) {
            // BusinessErrorMsgEnums 没有 getCode() 和 getMessage() 方法，仅记录枚举名称
            log.info("业务错误枚举: {}", businessErrorMsgEnum.name());
        }
        
        log.info("=== 已加载的系统错误响应消息 ===");
        for (SystemErrorMsgEnums systemErrorMsgEnum : SystemErrorMsgEnums.values()) {
            // SystemErrorMsgEnums 没有 getCode() 和 getMessage() 方法，仅记录枚举名称
            log.info("系统错误枚举: {}", systemErrorMsgEnum.name());
        }
        
        log.info("=== 已加载的成功响应消息 ===");
        for (SuccessMsgEnums successMsgEnum : SuccessMsgEnums.values()) {
            log.info("成功码: {}, 响应消息: {}, 枚举: {}",
                    successMsgEnum.getCode(), successMsgEnum.getMessage(), successMsgEnum.name());
        }
    }
}