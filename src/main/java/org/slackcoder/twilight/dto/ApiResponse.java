package org.slackcoder.twilight.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiResponse<T> {
    private int status;
    private T data;

    public ApiResponse(int status, T data) {
        this.status = status;
        this.data = data;
    }
}
