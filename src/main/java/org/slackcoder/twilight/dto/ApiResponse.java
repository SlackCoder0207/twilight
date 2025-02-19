package org.slackcoder.twilight.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApiResponse<T> {
    private int status;  // HTTP 状态码
    private T data;  // 接口返回数据
}
