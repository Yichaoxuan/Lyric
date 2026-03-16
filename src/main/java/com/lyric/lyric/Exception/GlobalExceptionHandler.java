package com.lyric.lyric.Exception;

import com.lyric.lyric.Enums.message.BusinessErrorMsgEnums;
import com.lyric.lyric.Enums.message.SystemErrorMsgEnums;
import com.lyric.lyric.Utils.resultUtils.Result;
import com.lyric.lyric.Utils.resultUtils.ResultBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * 全局异常处理器
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理业务异常
     *
     * @param e 业务异常
     * @return 统一响应结果
     */
    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Void> handleBusinessException(BusinessException e) {
        if (e.getBusinessErrorMsgEnums() != null) {
            log.error("业务异常，错误枚举：{}，错误信息：{}", e.getBusinessErrorMsgEnums().name(), e.getBusinessErrorMsgEnums().getName(), e);
            return ResultBuilder.error(e.getBusinessErrorMsgEnums());
        } else {
            log.error("业务异常，错误信息：{}", e.getMessage(), e);
            return Result.error("BUSINESS_ERROR", e.getMessage());
        }
    }

    /**
     * 处理系统异常
     *
     * @param e 系统异常
     * @return 统一响应结果
     */
    @ExceptionHandler(SystemException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<Void> handleSystemException(SystemException e) {
        log.error("系统异常，错误枚举：{}，错误信息：{}", e.getSystemErrorMsgEnums().name(), e.getSystemErrorMsgEnums().getName(), e);
        return ResultBuilder.error(e.getSystemErrorMsgEnums());
    }

    /**
     * 处理参数校验异常
     *
     * @param e 参数校验异常
     * @return 统一响应结果
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<Map<String, String>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("参数校验异常：", e);
        BindingResult bindingResult = e.getBindingResult();
        Map<String, String> errors = new HashMap<>();
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        return Result.error(BusinessErrorMsgEnums.RESPONSE_MESSAGE_COMMAND_NOT_INPUT.name(), "参数校验失败", errors);
    }

    /**
     * 处理其他未捕获的异常
     *
     * @param e 异常
     * @return 统一响应结果
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<Void> handleException(Exception e) {
        log.error("未知异常：", e);
        return Result.error(SystemErrorMsgEnums.SYSTEM_ERROR.name(), "系统内部错误，请联系管理员");
    }
}