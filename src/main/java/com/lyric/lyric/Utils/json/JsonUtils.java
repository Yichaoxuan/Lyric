package com.lyric.lyric.Utils.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JSON工具类
 * 提供JSON字符串与Java对象之间的相互转换功能
 *
 * @author Lyric
 * @since 2025-11-27
 */
public class JsonUtils {

    private static final Logger logger = LoggerFactory.getLogger(JsonUtils.class);

    private static final ObjectMapper objectMapper = new ObjectMapper();

    // 静态初始化，注册JavaTimeModule以支持LocalDateTime等时间类型
    static {
        objectMapper.registerModule(new JavaTimeModule());
    }

    /**
     * 将Java对象转换为JSON字符串
     *
     * @param object 待转换的Java对象
     * @param <T>    对象类型
     * @return JSON字符串，转换失败时返回null
     */
    public static <T> String toJson(T object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            logger.error("将对象转换为JSON字符串时发生错误", e);
            return null;
        }
    }

    /**
     * 将JSON字符串转换为指定类型的Java对象
     *
     * @param json  JSON字符串
     * @param clazz 目标Java类
     * @param <T>   目标类型
     * @return 转换后的Java对象，转换失败时返回null
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            //清理json中可能存在的额外字符，仅保留有效的JSON，再进行转换
            return objectMapper.readValue(removeExtraCharacters(json), clazz);
        } catch (JsonProcessingException e) {
            logger.error("将JSON字符串转换为对象时发生错误", e);
            return null;
        }
    }

    /**
     * 验证字符串是否为有效的JSON格式
     *
     * @param json 待验证的字符串
     * @return 如果是有效的JSON格式返回true，否则返回false
     */
    public static boolean isValidJson(String json) {
        try {
            //清理json中可能存在的额外字符，仅保留有效的JSON，再进行验证
            objectMapper.readTree(removeExtraCharacters(json));
            return true;
        } catch (JsonProcessingException e) {
            return false;
        }
    }

    /**
     * 格式化JSON字符串，使其具有良好的可读性
     *
     * @param json 待格式化的JSON字符串
     * @return 格式化后的JSON字符串，格式化失败时返回原字符串
     */
    public static String formatJson(String json) {
        try {
            Object jsonObject = objectMapper.readValue(json, Object.class);
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonObject);
        } catch (JsonProcessingException e) {
            logger.error("格式化JSON字符串时发生错误", e);
            return json;
        }
    }

    /**
     * 去除AI返回时可能包含的除JSON外的额外字符
     *
     * @param json 待处理的JSON字符串
     * @return 处理后的JSON字符串
     */
    public static String removeExtraCharacters(String json) {
        if (json == null || json.isEmpty()) {
            return json;
        }
        
        // 查找第一个 '{' 或 '[' 字符的位置（JSON对象或数组的开始）
        int startIndex = -1;
        for (int i = 0; i < json.length(); i++) {
            char c = json.charAt(i);
            if (c == '{' || c == '[') {
                startIndex = i;
                break;
            }
            // 如果遇到其他非空白字符，则可能不是有效的JSON
            if (!(Character.isWhitespace(c) || c == '`')) {
                break;
            }
        }
        
        // 如果没有找到开始字符，返回原字符串
        if (startIndex == -1) {
            return json;
        }
        
        // 查找最后一个 '}' 或 ']' 字符的位置（JSON对象或数组的结束）
        int endIndex = -1;
        for (int i = json.length() - 1; i >= 0; i--) {
            char c = json.charAt(i);
            if (c == '}' || c == ']') {
                endIndex = i;
                break;
            }
            // 如果遇到其他非空白字符，则可能不是有效的JSON
            if (!(Character.isWhitespace(c) || c == '`')) {
                break;
            }
        }
        
        // 如果没有找到结束字符，返回原字符串
        if (endIndex == -1) {
            return json;
        }
        
        // 提取有效的JSON部分
        if (endIndex >= startIndex) {
            return json.substring(startIndex, endIndex + 1);
        }
        
        // 如果索引不合法，返回原字符串
        return json;
    }
}