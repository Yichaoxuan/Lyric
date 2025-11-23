package com.lyric.lyric.Utils.stringFormatConversion;

/**
 * 枚举名称转换工具类
 * 提供将枚举名称转换为不同格式字符串的方法
 */
public class EnumNameConverterUtils {

    /**
     * 将枚举名称转换为snake_case格式
     *
     * <p>转换规则：
     * 1. 在小写字母和大写字母之间插入下划线（如：SAVE_SUCCESS -> save_success）
     * 2. 转换为小写
     * 适用于将Java枚举命名规范转换为配置文件中的键名格式
     *
     * @param enumName 枚举名称（如：SAVE_SUCCESS）
     * @return 转换后的snake_case格式字符串（如：save_success）
     */
    public static String toSnakeCase(String enumName) {
        return enumName.replaceAll("([a-z])([A-Z])", "$1_$2")
                .toLowerCase();
    }
}

