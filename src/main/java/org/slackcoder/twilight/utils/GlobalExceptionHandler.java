package org.slackcoder.twilight.utils;

import org.slackcoder.twilight.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 处理所有未知异常
    @ExceptionHandler(Exception.class)
    public ApiResponse<String> handleException(Exception ex) {
        return new ApiResponse<>(500, "服务器内部错误: " + ex.getMessage());
    }

    // 处理非法参数异常 (如请求参数错误)
    @ExceptionHandler(IllegalArgumentException.class)
    public ApiResponse<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        return new ApiResponse<>(400, "请求参数错误: " + ex.getMessage());
    }

    // 处理资源未找到异常 (如找不到用户)
    @ExceptionHandler(org.slackcoder.twilight.utils.ResourceNotFoundException.class)
    public ApiResponse<String> handleResourceNotFoundException(org.slackcoder.twilight.utils.ResourceNotFoundException ex) {
        return new ApiResponse<>(404, ex.getMessage());
    }

}
