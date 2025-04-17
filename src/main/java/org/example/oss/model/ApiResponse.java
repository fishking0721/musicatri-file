package org.example.oss.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApiResponse<T> {
    private T data;
    private String message;


    // 成功响应的方法
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(data, "Success");
    }

}
