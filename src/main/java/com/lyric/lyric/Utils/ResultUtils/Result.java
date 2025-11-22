package com.lyric.lyric.Utils.ResultUtils;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * 统一响应结果封装类
 * @param <T> 响应数据类型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> {
    /**
     * 响应状态码
     */
    private Integer code;
    
    /**
     * 响应消息
     */
    private String message;
    
    /**
     * 响应数据
     */
    private T data;
    
    /**
     * 时间戳
     */
    private Long timestamp;
    
    /**
     * 成功响应（无数据）
     * @param message 响应消息
     * @return Result对象
     */
    public static Result<Void> success(String message) {
        Result<Void> result = new Result<>();
        result.setCode(200);
        result.setMessage(message);
        result.setTimestamp(System.currentTimeMillis());
        return result;
    }
    
    /**
     * 成功响应（有数据）
     * @param data 响应数据
     * @param <T> 数据类型
     * @return Result对象
     */
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage("操作成功");
        result.setData(data);
        result.setTimestamp(System.currentTimeMillis());
        return result;
    }
    
    /**
     * 成功响应（有数据和消息）
     * @param data 响应数据
     * @param message 响应消息
     * @param <T> 数据类型
     * @return Result对象
     */
    public static <T> Result<T> success(T data, String message) {
        Result<T> result = new Result<>();
        result.setCode(200);
        result.setMessage(message);
        result.setData(data);
        result.setTimestamp(System.currentTimeMillis());
        return result;
    }
    
    /**
     * 失败响应（默认错误码）
     * @param message 错误消息
     * @return Result对象
     */
    public static Result<Void> error(String message) {
        Result<Void> result = new Result<>();
        result.setCode(500);
        result.setMessage(message);
        result.setTimestamp(System.currentTimeMillis());
        return result;
    }
    
    /**
     * 失败响应（指定错误码）
     * @param code 错误码
     * @param message 错误消息
     * @return Result对象
     */
    public static Result<Void> error(Integer code, String message) {
        Result<Void> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        result.setTimestamp(System.currentTimeMillis());
        return result;
    }
    
    /**
     * 失败响应（指定错误码、消息和数据）
     * @param code 错误码
     * @param message 错误消息
     * @param data 数据
     * @param <T> 数据类型
     * @return Result对象
     */
    public static <T> Result<T> error(Integer code, String message, T data) {
        Result<T> result = new Result<>();
        result.setCode(code);
        result.setMessage(message);
        result.setData(data);
        result.setTimestamp(System.currentTimeMillis());
        return result;
    }
}