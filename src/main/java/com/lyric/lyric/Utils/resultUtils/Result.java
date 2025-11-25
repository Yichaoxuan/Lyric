package com.lyric.lyric.Utils.resultUtils;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * 统一响应结果封装类
 * @param <T> 响应数据类型
 *
 * @since 2025-11-23
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> {
    /**
     * 响应状态码
     */
    private String code;
    
    /**
     * 响应消息
     */
    private String message;
    
    /**
     * 响应数据
     */
    private T data;

    
    /**
     * 成功响应
     * @param message 响应消息
     * @return Result对象
     */
    public static Result<Void> success(String code, String message) {
        Result<Void> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }
    
    /**
     * 成功响应
     * @param data 响应数据
     * @param <T> 数据类型
     * @return Result对象
     */
    public static <T> Result<T> success(String code, T data) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage("操作成功");
        result.setData(data);
        return result;
    }
    
    /**
     * 成功响应
     * @param data 响应数据
     * @param message 响应消息
     * @param <T> 数据类型
     * @return Result对象
     */
    public static <T> Result<T> success(String code, T data, String message) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        result.setData(data);
        return result;
    }
    
    /**
     * 失败响应
     * @param code 错误码
     * @param message 错误消息
     * @return Result对象
     */
    public static Result<Void> error(String code, String message) {
        Result<Void> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        return result;
    }
    
    /**
     * 失败响应
     * @param code 错误码
     * @param message 错误消息
     * @param data 数据
     * @param <T> 数据类型
     * @return Result对象
     */
    public static <T> Result<T> error(String code,  String message, T data) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        result.setData(data);
        return result;
    }
}