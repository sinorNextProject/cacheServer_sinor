package com.sinor.cache.global.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Getter;

@Getter
@JsonPropertyOrder({"timestamp", "isSuccess", "status", "message", "data"})
public class DataResponse<T> extends BaseResponse{
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final T data;

    // 요청에 성공한 경우
    private DataResponse(BaseStatus status, T data) {
        super(status);
        this.data = data;
    }

    /**
     * DataResponse 생성
     * @param status HttpStatus
     * @param data 따로 저장할 데이터
     */
    public static <T> DataResponse<?> from(BaseStatus status, T data) {
        return new DataResponse<>(status, data);
    }
}

