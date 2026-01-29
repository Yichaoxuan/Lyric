package com.lyric.lyric.Utils.text;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 占位符替换工具类
 * 提供多种方式的占位符替换功能
 *
 * @author Lyric
 * @since 2025-12-12
 */
public class PlaceholderUtils {

    // 更健壮的正则表达式，匹配 {{placeholder}} 格式的占位符
    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\{\\{\\s*([^{}]+?)\\s*}}");

    /**
     * 替换单个占位符
     *
     * @param text        原始文本
     * @param placeholder 占位符名称（不需要包含{{}}）
     * @param value       要替换的值
     * @return 替换后的文本
     */
    public static String replacePlaceholder(String text, String placeholder, String value) {
        if (text == null || placeholder == null) {
            return text;
        }
        
        // 构造匹配 {{placeholder}} 的正则表达式，支持占位符周围有空格的情况
        String placeholderPattern = "\\{\\{\\s*" + Pattern.quote(placeholder) + "\\s*}}";
        return text.replaceAll(placeholderPattern, value != null ? value : "");
    }

    /**
     * 批量替换多个占位符
     *
     * @param text        原始文本
     * @param placeholders 占位符和对应值的映射
     * @return 替换后的文本
     */
    public static String replacePlaceholders(String text, Map<String, String> placeholders) {
        if (text == null || placeholders == null || placeholders.isEmpty()) {
            return text;
        }

        String result = text;
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            result = replacePlaceholder(result, entry.getKey(), entry.getValue());
        }
        return result;
    }

    /**
     * 使用对象属性批量替换占位符
     * 占位符格式应为 {{propertyName}}
     *
     * @param text   原始文本
     * @param params 参数对象，其属性将用于替换对应的占位符
     * @return 替换后的文本
     */
    public static String replacePlaceholders(String text, Object params) {
        if (text == null || params == null) {
            return text;
        }

        Matcher matcher = PLACEHOLDER_PATTERN.matcher(text);
        StringBuffer result = new StringBuffer(); // 使用StringBuffer以避免转义问题

        while (matcher.find()) {
            String placeholder = matcher.group(1).trim(); // 获取占位符名称并去除空格
            String value = getPropertyValue(params, placeholder);
            // 使用Matcher.quoteReplacement避免特殊字符导致的异常
            matcher.appendReplacement(result, Matcher.quoteReplacement(value != null ? value : ""));
        }
        matcher.appendTail(result);

        return result.toString();
    }

    /**
     * 获取对象的属性值
     *
     * @param obj    对象
     * @param fieldName 属性名
     * @return 属性值
     */
    private static String getPropertyValue(Object obj, String fieldName) {
        try {
            // 这里使用简单的反射来获取属性值
            java.lang.reflect.Field field = obj.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            Object value = field.get(obj);
            return value != null ? value.toString() : "";
        } catch (NoSuchFieldException | IllegalAccessException e) {
            // 如果找不到字段，返回原占位符
            return "{{" + fieldName + "}}";
        } catch (Exception e) {
            return "{{" + fieldName + "}}";
        }
    }

    /**
     * 验证文本中的占位符是否都有对应的值
     *
     * @param text        原始文本
     * @param placeholders 占位符和对应值的映射
     * @return 如果所有占位符都有对应的值则返回true，否则返回false
     */
    public static boolean validatePlaceholders(String text, Map<String, String> placeholders) {
        if (text == null) {
            return true;
        }

        Matcher matcher = PLACEHOLDER_PATTERN.matcher(text);
        while (matcher.find()) {
            String placeholder = matcher.group(1).trim();
            if (placeholders == null || !placeholders.containsKey(placeholder)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 获取文本中所有占位符的列表
     *
     * @param text 原始文本
     * @return 占位符列表
     */
    public static java.util.List<String> getPlaceholders(String text) {
        java.util.List<String> placeholders = new java.util.ArrayList<>();
        if (text == null) {
            return placeholders;
        }

        Matcher matcher = PLACEHOLDER_PATTERN.matcher(text);
        while (matcher.find()) {
            placeholders.add(matcher.group(1).trim()); // 去除占位符周围的空格
        }
        return placeholders;
    }
}