package com.lyric.lyric.Config.message;

import com.lyric.lyric.POJO.message.MessageConfigPojo;
import com.lyric.lyric.Service.message.ResponseMessageService;
import lombok.Data;
import lombok.Getter;
import com.lyric.lyric.Utils.config.ConfigLoggerUtil;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;


/**
 * 消息配置类
 * 从数据库加载响应消息配置
 *
 * @author Yichaoxuan
 * @since 2026/03/30
 */
@Data
@Getter
@Component
public class MsgConfig {

    private static final Logger logger = LoggerFactory.getLogger(MsgConfig.class);

    private final ResponseMessageService responseMessageService;

    /**
     *  将业务错误消息配置项保存为 Map
     *
     */
    private Map<String, MessageConfigPojo.Message> businessError = new HashMap<>();
    
    /**
     *  将系统错误消息配置项保存为 Map
     *
     */
    private Map<String, MessageConfigPojo.Message> systemError = new HashMap<>();
    
    /**
     *  将成功消息配置项保存为 Map
     *
     */
    private Map<String, MessageConfigPojo.Message> success = new HashMap<>();

    public MsgConfig(ResponseMessageService responseMessageService) {
        this.responseMessageService = responseMessageService;
        loadFromDatabase();
    }

    /**
     * 在组件构造完成后打印配置信息
     * 该方法使用@PostConstruct 注解，确保在依赖注入完成后自动执行
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
     * 从数据库加载消息配置
     */
    private void loadFromDatabase() {
        logger.info("开始从数据库加载消息配置...");
        
        this.businessError = responseMessageService.getBusinessErrorMessages();
        this.systemError = responseMessageService.getSystemErrorMessages();
        this.success = responseMessageService.getSuccessMessages();
        
        logger.info("数据库消息配置加载完成：业务错误 {} 条，系统错误 {} 条，成功消息 {} 条",
                businessError.size(), systemError.size(), success.size());
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
     * 更新消息配置并保存到数据库
     * @param configPojo 包含新配置的 POJO 对象
     */
    public void updateConfigAndSaveToDatabase(MessageConfigPojo configPojo) {
        // 检查传入的配置对象是否为 null
        if (configPojo == null) {
            throw new IllegalArgumentException("配置对象不能为空");
        }
            
        // 检查各个配置项是否为 null
        if (configPojo.getBusinessError() == null) {
            throw new IllegalArgumentException("业务错误配置不能为空");
        }
        if (configPojo.getSystemError() == null) {
            throw new IllegalArgumentException("系统错误配置不能为空");
        }
        if (configPojo.getSuccess() == null) {
            throw new IllegalArgumentException("成功配置不能为空");
        }
            
        // 先更新内存中的配置
        this.businessError.clear();
        this.businessError.putAll(configPojo.getBusinessError());
            
        this.systemError.clear();
        this.systemError.putAll(configPojo.getSystemError());
    
        this.success.clear();
        this.success.putAll(configPojo.getSuccess());
    
        // 保存到数据库
        responseMessageService.batchUpdateMessages(configPojo.getBusinessError(), "business-error");
        responseMessageService.batchUpdateMessages(configPojo.getSystemError(), "system-error");
        responseMessageService.batchUpdateMessages(configPojo.getSuccess(), "success");
        
        logger.info("消息配置已更新到数据库");
    }

    /**
     * 获取最新的消息配置
     * @return 包含当前所有消息配置的 MessageConfigPojo 对象
     */
    public MessageConfigPojo getLatestConfig() {
        MessageConfigPojo config = new MessageConfigPojo();
        config.setBusinessError(responseMessageService.getBusinessErrorMessages());
        config.setSystemError(responseMessageService.getSystemErrorMessages());
        config.setSuccess(responseMessageService.getSuccessMessages());
        return config;
    }

    /**
     * 获取最新的业务错误消息配置
     * @return 包含当前所有业务错误消息配置的 Map
     */
    public MessageConfigPojo getLatestBusinessErrorConfig() {
        MessageConfigPojo config = new MessageConfigPojo();
        config.setBusinessError(responseMessageService.getBusinessErrorMessages());
        return config;
    }
    
    /**
     * 获取最新的系统错误消息配置
     * @return 包含当前所有系统错误消息配置的 Map
     */
    public MessageConfigPojo getLatestSystemErrorConfig() {
        MessageConfigPojo config = new MessageConfigPojo();
        config.setSystemError(responseMessageService.getSystemErrorMessages());
        return config;
    }

    /**
     * 获取最新的成功消息配置
     * @return 包含当前所有成功消息配置的 Map
     */
    public MessageConfigPojo getLatestSuccessConfig() {
        MessageConfigPojo config = new MessageConfigPojo();
        config.setSuccess(responseMessageService.getSuccessMessages());
        return config;
    }
}
