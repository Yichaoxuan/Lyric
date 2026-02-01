package com.lyric.lyric.Config.message;

import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.lyric.lyric.POJO.message.MessageConfigPojo;
import lombok.Data;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import com.lyric.lyric.Utils.config.ConfigLoggerUtil;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


/**
 * 消息枚举类的配置类
 * 
 * @since 2026-01-31
 */
@Data
@Getter
@Component
@ConfigurationProperties(prefix = "response-message")
public class MsgConfig {

    private static final Logger logger = LoggerFactory.getLogger(MsgConfig.class);

    /**
     *  将业务错误消息配置项保存为Map
     *
     */
    private Map<String, MessageConfigPojo.Message> businessError = new HashMap<>();
    
    /**
     *  将系统错误消息配置项保存为Map
     *
     */
    private Map<String, MessageConfigPojo.Message> systemError = new HashMap<>();
    
    /**
     *  将成功消息配置项保存为Map
     *
     */
    private Map<String, MessageConfigPojo.Message> success = new HashMap<>();

    /**
     * 在组件构造完成后打印配置信息
     * 该方法使用@PostConstruct注解，确保在依赖注入完成后自动执行
     */
    @PostConstruct
    public void printConfigurations() {
        logger.info("消息配置加载状态检查:");

        // 检查业务错误消息配置
        ConfigLoggerUtil.logConfigStatusSafely(logger, "  业务错误消息配置", businessError);
        
        // 检查系统错误消息配置
        ConfigLoggerUtil.logConfigStatusSafely(logger, "  系统错误消息配置", systemError);

        // 检查成功消息配置
        ConfigLoggerUtil.logConfigStatusSafely(logger, "  成功消息配置", success);
    }

    /**
     * 根据消息类型获取对应的配置映射
     * @param messageType 消息类型 ("businessError", "systemError" 或 "success")
     * @return 对应的配置映射
     */
    public Map<String, MessageConfigPojo.Message> getItems(String messageType) {
        return switch (messageType) {
            case "businessError" -> this.businessError;
            case "systemError" -> this.systemError;
            case "success" -> this.success;
            default -> new HashMap<>();
        };
    }

    /**
     * 更新消息配置并保存到配置文件
     * @param configPojo 包含新配置的POJO对象
     * @throws IOException 文件操作异常
     */
    public void updateConfigAndSaveToFile(MessageConfigPojo configPojo) throws IOException {
        // 先更新内存中的配置
        this.businessError.clear();
        this.businessError.putAll(configPojo.getBusinessError());
        
        this.systemError.clear();
        this.systemError.putAll(configPojo.getSystemError());

        this.success.clear();
        this.success.putAll(configPojo.getSuccess());

        // 保存到配置文件
        saveToFile();
    }

    /**
     * 将当前配置保存到YAML文件
     * @throws IOException 文件操作异常
     */
    public void saveToFile() throws IOException {
        // 创建YAML映射器
        YAMLMapper yamlMapper = new YAMLMapper();

        // 构造要保存的数据结构，匹配YAML文件格式
        Map<String, Object> yamlData = new HashMap<>();
        Map<String, Object> responseMessage = new HashMap<>();
        responseMessage.put("business-error", this.businessError);
        responseMessage.put("system-error", this.systemError);
        responseMessage.put("success", this.success);
        yamlData.put("response-message", responseMessage);

        // 写入文件
        File configFile = new File("src/main/resources/message-config.yml");
        yamlMapper.writeValue(configFile, yamlData);
    }

    /**
     * 获取最新的消息配置
     * @return 包含当前所有消息配置的MessageConfigPojo对象
     */
    public MessageConfigPojo getLatestConfig() {
        MessageConfigPojo config = new MessageConfigPojo();
        config.setBusinessError(new HashMap<>(this.businessError));
        config.setSystemError(new HashMap<>(this.systemError));
        config.setSuccess(new HashMap<>(this.success));
        return config;
    }

    /**
     * 获取最新的业务错误消息配置
     * @return 包含当前所有业务错误消息配置的Map
     */
    public MessageConfigPojo getLatestBusinessErrorConfig() {
        MessageConfigPojo config = new MessageConfigPojo();
        config.setBusinessError(new HashMap<>(this.businessError));
        return config;
    }
    
    /**
     * 获取最新的系统错误消息配置
     * @return 包含当前所有系统错误消息配置的Map
     */
    public MessageConfigPojo getLatestSystemErrorConfig() {
        MessageConfigPojo config = new MessageConfigPojo();
        config.setSystemError(new HashMap<>(this.systemError));
        return config;
    }

    /**
     * 获取最新的成功消息配置
     * @return 包含当前所有成功消息配置的Map
     */
    public MessageConfigPojo getLatestSuccessConfig() {
        MessageConfigPojo config = new MessageConfigPojo();
        config.setSuccess(new HashMap<>(this.success));
        return config;
    }
}