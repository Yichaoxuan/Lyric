package com.lyric.lyric.Service.message;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.lyric.lyric.Mapper.message.ResponseMessageMapper;
import com.lyric.lyric.POJO.message.MessageConfigPojo;
import com.lyric.lyric.POJO.message.ResponseMessagePojo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 响应消息服务类
 * 提供响应消息的数据库存取服务
 */
@Slf4j
@Service
public class ResponseMessageService {

    private final ResponseMessageMapper responseMessageMapper;

    public ResponseMessageService(ResponseMessageMapper responseMessageMapper) {
        this.responseMessageMapper = responseMessageMapper;
        // 启动时检查并初始化响应消息
        initializeIfEmpty();
    }

    /**
     * 获取所有业务错误消息配置
     * @return 业务错误消息 Map
     */
    public Map<String, MessageConfigPojo.Message> getBusinessErrorMessages() {
        return loadMessagesByType("business-error");
    }

    /**
     * 获取所有系统错误消息配置
     * @return 系统错误消息 Map
     */
    public Map<String, MessageConfigPojo.Message> getSystemErrorMessages() {
        return loadMessagesByType("system-error");
    }

    /**
     * 获取所有成功消息配置
     * @return 成功消息 Map
     */
    public Map<String, MessageConfigPojo.Message> getSuccessMessages() {
        return loadMessagesByType("success");
    }

    /**
     * 根据类型加载消息配置
     * @param type 消息类型
     * @return 消息配置 Map
     */
    private Map<String, MessageConfigPojo.Message> loadMessagesByType(String type) {
        List<ResponseMessagePojo> messages = responseMessageMapper.selectByType(type);
        Map<String, MessageConfigPojo.Message> map = new HashMap<>();
        
        for (ResponseMessagePojo pojo : messages) {
            MessageConfigPojo.Message msg = new MessageConfigPojo.Message();
            msg.setCode(pojo.getCode());
            msg.setMessage(pojo.getMessage());
            map.put(pojo.getMessageKey(), msg);
        }
        
        log.info("从数据库加载 {} 类型消息配置 {} 条", type, messages.size());
        return map;
    }

    /**
     * 启动时检查数据库是否为空，如果为空则从 YAML 文件初始化
     */
    @SuppressWarnings("unchecked")
    private void initializeIfEmpty() {
        // 检查数据库是否已有响应消息
        List<ResponseMessagePojo> allMessages = responseMessageMapper.selectAll();
        if (!allMessages.isEmpty()) {
            log.info("数据库中已有 {} 条响应消息，跳过初始化", allMessages.size());
            return;
        }

        log.info("数据库中没有响应消息，开始从 YAML 文件初始化...");
        try {
            // 读取 YAML 配置文件
            ClassPathResource resource = new ClassPathResource("message-config.yml");
            byte[] bytes = resource.getInputStream().readAllBytes();
            String yamlContent = new String(bytes);
            
            ObjectMapper yamlMapper = new ObjectMapper(new YAMLFactory());
            Map<String, Object> rootMap = yamlMapper.readValue(yamlContent, Map.class);
            Map<String, Object> responseMessage = (Map<String, Object>) rootMap.get("response-message");
            
            // 解析并保存成功消息
            Map<String, Object> successMap = (Map<String, Object>) responseMessage.get("success");
            if (successMap != null) {
                saveMessagesToDatabase(successMap, "success");
            }
            
            // 解析并保存系统错误消息
            Map<String, Object> systemErrorMap = (Map<String, Object>) responseMessage.get("system-error");
            if (systemErrorMap != null) {
                saveMessagesToDatabase(systemErrorMap, "system-error");
            }
            
            // 解析并保存业务错误消息
            Map<String, Object> businessErrorMap = (Map<String, Object>) responseMessage.get("business-error");
            if (businessErrorMap != null) {
                saveMessagesToDatabase(businessErrorMap, "business-error");
            }
            
            log.info("响应消息初始化完成！");
        } catch (Exception e) {
            log.error("初始化响应消息失败：{}", e.getMessage(), e);
            throw new RuntimeException("初始化响应消息失败", e);
        }
    }

    /**
     * 将解析后的消息保存到数据库
     * @param messageMap 消息 Map，key 为 message_key，value 为包含 code 和 message 的 Map
     * @param messageType 消息类型
     */
    @SuppressWarnings("unchecked")
    private void saveMessagesToDatabase(Map<String, Object> messageMap, String messageType) {
        int count = 0;
        for (Map.Entry<String, Object> entry : messageMap.entrySet()) {
            String messageKey = entry.getKey();
            Map<String, Object> messageData = (Map<String, Object>) entry.getValue();
            
            String code = (String) messageData.get("code");
            String message = (String) messageData.get("message");
            
            ResponseMessagePojo pojo = new ResponseMessagePojo();
            pojo.setMessageKey(messageKey);
            pojo.setMessageType(messageType);
            pojo.setCode(code);
            pojo.setMessage(message);
            
            responseMessageMapper.insert(pojo);
            count++;
        }
        log.info("保存 {} 类型消息 {} 条到数据库", messageType, count);
    }

    /**
     * 更新或插入单条消息配置
     * @param message 消息内容
     * @param messageKey 消息键
     * @param type 消息类型
     */
    public void updateMessage(MessageConfigPojo.Message message, String messageKey, String type) {
        ResponseMessagePojo existing = responseMessageMapper.selectByKey(messageKey);
        
        if (existing != null) {
            existing.setCode(message.getCode());
            existing.setMessage(message.getMessage());
            responseMessageMapper.update(existing);
            log.info("更新消息配置：{} - {}", messageKey, message.getMessage());
        } else {
            ResponseMessagePojo newPojo = new ResponseMessagePojo();
            newPojo.setMessageKey(messageKey);
            newPojo.setMessageType(type);
            newPojo.setCode(message.getCode());
            newPojo.setMessage(message.getMessage());
            responseMessageMapper.insert(newPojo);
            log.info("新增消息配置：{} - {}", messageKey, message.getMessage());
        }
    }

    /**
     * 批量更新消息配置
     * @param messages 消息配置 Map
     * @param type 消息类型
     */
    public void batchUpdateMessages(Map<String, MessageConfigPojo.Message> messages, String type) {
        for (Map.Entry<String, MessageConfigPojo.Message> entry : messages.entrySet()) {
            updateMessage(entry.getValue(), entry.getKey(), type);
        }
        log.info("批量更新 {} 类型消息配置 {} 条", type, messages.size());
    }
}
