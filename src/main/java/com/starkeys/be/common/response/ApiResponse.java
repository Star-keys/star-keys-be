package com.starkeys.be.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
        boolean success,
        T data,
        ErrorDetail errorDetail,
        PageMeta pageMeta
) {
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, data, null, null);
    }

    public static <T> ApiResponse<T> fail(String code, String message) {
        return new ApiResponse<>(false, null, new ErrorDetail(code, message, null), null);
    }

    public static <T> ApiResponse<T> fail(String code, String message, Object details) {
        return new ApiResponse<>(false, null, new ErrorDetail(code, message, details), null);
    }

    public static <T> ApiResponse<T> success(T data, PageMeta pageMeta) {
        return new ApiResponse<>(true, data, null, pageMeta);
    }
}
