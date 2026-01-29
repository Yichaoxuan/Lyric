package com.lyric.lyric.Utils.stringProcessing;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 字符串处理工具类
 */
public class stringUtils {

    /**
     * 列表转字符串
     * @param list 待转换的列表
     * @return 转换后的字符串，元素间用逗号分隔
     */
    public static String listToString(List<String> list) {
        if (list == null || list.isEmpty()) {
            return "";
        }
        return String.join(",", list);
    }

    /**
     * 字符串转列表
     * @param str 待转换的字符串
     * @return 转换后的List列表
     */
    public static List<String> stringToList(String str) {
        if (str == null || str.isEmpty()) {
            return new ArrayList<>();
        }
        return Arrays.stream(str.split(",")).collect(Collectors.toList());
    }

    /**
     * 字符串转Map
     * @param str 待转换的字符串，格式为 "key1:value1,key2:value2"
     * @return 转换后的Map
     */
    public static Map<String, String> stringToMap(String str) {
        if (str == null || str.isEmpty()) {
            return new HashMap<>();
        }

        return Arrays.stream(str.split(","))
                .map(entry -> entry.split(":"))
                .filter(parts -> parts.length == 2)
                .collect(Collectors.toMap(parts -> parts[0], parts -> parts[1]));
    }

    /**
     * Map转字符串
     * @param map 待转换的Map
     * @return 转换后的字符串，格式为 "key1:value1,key2:value2"
     */
    public static String mapToString(Map<String, String> map) {
        if (map == null || map.isEmpty()) {
            return "";
        }

        return map.entrySet().stream()
                .map(entry -> entry.getKey() + ":" + entry.getValue())
                .collect(Collectors.joining(","));
    }
}
