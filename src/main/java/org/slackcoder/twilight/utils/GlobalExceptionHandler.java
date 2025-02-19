package org.slackcoder.twilight.utils;

import org.slackcoder.twilight.dto.ApiResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 处理所有未知异常
    @ExceptionHandler(Exception.class)
    public ApiResponse<String> handleException(Exception ex) {
        return new ApiResponse<>(500, "服务器内部错误: " + ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ApiResponse<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        return new ApiResponse<>(400, "请求参数错误: " + ex.getMessage());
    }

    @ExceptionHandler(org.slackcoder.twilight.exception.ResourceNotFoundException.class)
    public ApiResponse<String> handleResourceNotFoundException(org.slackcoder.twilight.exception.ResourceNotFoundException ex) {
        return new ApiResponse<>(404, ex.getMessage());
    }

}
