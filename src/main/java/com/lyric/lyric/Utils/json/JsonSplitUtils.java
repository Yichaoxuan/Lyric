package com.lyric.lyric.Utils.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.lyric.lyric.Utils.json.JsonConversionUtils.removeExtraCharacters;

/**
 * JSON工具类
 * 提供JSON字符串与Java对象之间的相互转换功能
 *
 * @author Lyric
 * @since 2025-11-27
 */
public class JsonSplitUtils {

    private static final Logger logger = LoggerFactory.getLogger(JsonSplitUtils.class);

    private static final ObjectMapper objectMapper = new ObjectMapper();

    // 静态初始化，注册JavaTimeModule以支持LocalDateTime等时间类型
    static {
        objectMapper.registerModule(new JavaTimeModule());
    }

    /**
     * 分割复合JSON字符串为三个独立的JSON字符串
     * 分别包含success、business-error和system-error部分
     *
     * @param json 复合JSON字符串
     * @return 包含三个独立JSON字符串的数组，索引0为success部分，索引1为business-error部分，索引2为system-error部分
     * @throws JsonProcessingException 当JSON解析失败时抛出异常
     */
    public static String[] splitResponseJson(String json) throws JsonProcessingException {
        // 清理JSON字符串
        String cleanJson = removeExtraCharacters(json);

        // 解析JSON
        JsonNode rootNode = objectMapper.readTree(cleanJson);

        // 创建三个独立的JSON对象
        ObjectNode successNode = objectMapper.createObjectNode();
        ObjectNode businessErrorNode = objectMapper.createObjectNode();
        ObjectNode systemErrorNode = objectMapper.createObjectNode();

        // 提取各部分数据
        if (rootNode.has("success")) {
            successNode.set("success", rootNode.get("success"));
        }

        if (rootNode.has("error")) {
            JsonNode errorNode = rootNode.get("error");
            if (errorNode.has("business-error")) {
                businessErrorNode.set("business-error", errorNode.get("business-error"));
            }
            if (errorNode.has("system-error")) {
                systemErrorNode.set("system-error", errorNode.get("system-error"));
            }
        }

        // 转换为字符串数组返回
        String[] result = new String[3];
        result[0] = successNode.toString();
        result[1] = businessErrorNode.toString();
        result[2] = systemErrorNode.toString();

        return result;
    }

    /**
     * 解析包含summary和labels的JSON结构
     *
     * @param json 待解析的JSON字符串
     * @return JsonNode对象，包含解析后的结构
     * @throws JsonProcessingException 当JSON解析失败时抛出异常
     */
    public static JsonNode parseSummaryAndLabels(String json) throws JsonProcessingException {
        // 清理JSON字符串
        String cleanJson = removeExtraCharacters(json);

        // 解析JSON
        return objectMapper.readTree(cleanJson);
    }

    /**
     * 从包含summary和labels的JSON结构中提取summary字段
     *
     * @param json 待解析的JSON字符串
     * @return summary字段的值
     * @throws JsonProcessingException 当JSON解析失败时抛出异常
     */
    public static String extractSummary(String json) throws JsonProcessingException {
        JsonNode rootNode = parseSummaryAndLabels(json);
        JsonNode summaryNode = rootNode.get("summary");
        return summaryNode != null ? summaryNode.asText() : null;
    }

    /**
     * 从包含summary和labels的JSON结构中提取labels对象
     *
     * @param json 待解析的JSON字符串
     * @return labels对象节点
     * @throws JsonProcessingException 当JSON解析失败时抛出异常
     */
    public static JsonNode extractLabels(String json) throws JsonProcessingException {
        JsonNode rootNode = parseSummaryAndLabels(json);
        return rootNode.get("labels");
    }

    /**
     * 从JSON结构中提取标签信息
     *
     * @param json 待解析的JSON字符串
     * @return 标签信息的JsonNode对象
     * @throws JsonProcessingException 当JSON解析失败时抛出异常
     */
    public static JsonNode extractTagInfo(String json) throws JsonProcessingException {
        JsonNode labelsNode = extractLabels(json);
        return labelsNode != null ? labelsNode.get("tag") : null;
    }

    /**
     * 从JSON结构中提取实体标签信息
     *
     * @param json 待解析的JSON字符串
     * @return 实体标签信息的JsonNode对象
     * @throws JsonProcessingException 当JSON解析失败时抛出异常
     */
    public static JsonNode extractEntityTagInfo(String json) throws JsonProcessingException {
        JsonNode labelsNode = extractLabels(json);
        return labelsNode != null ? labelsNode.get("entityTag") : null;
    }

}