package com.sinor.cache.global.exception;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.sinor.cache.global.ResponseStatus;
import com.sinor.cache.global.admin.AdminResponseStatus;
import com.sinor.cache.global.main.MainResponseStatus;

import lombok.Getter;

@Getter
@JsonPropertyOrder({"timestamp", "isSuccess", "code", "message"})
public class BaseResponse {
    private final LocalDateTime timestamp;
    @JsonProperty("isSuccess")
    private final Boolean isSuccess;
    private final int code;
    private final String message;

    protected BaseResponse(ResponseStatus status) {
        this.timestamp = LocalDateTime.now();
        this.isSuccess = status.isSuccess();
        this.message = status.getMessage();
        this.code = status.getCode();
    }

    protected BaseResponse(AdminResponseStatus status) {
        this.timestamp = LocalDateTime.now();
        this.isSuccess = status.isSuccess();
        this.message = status.getMessage();
        this.code = status.getCode();
    }

    protected BaseResponse(MainResponseStatus status) {
        this.timestamp = LocalDateTime.now();
        this.isSuccess = status.isSuccess();
        this.message = status.getMessage();
        this.code = status.getCode();
    }

    public static BaseResponse from(ResponseStatus status) {
        return new BaseResponse(status);
    }

    public static BaseResponse fromByAdmin(AdminResponseStatus status) {
        return new BaseResponse(status);
    }

    public static BaseResponse fromByMain(MainResponseStatus status) {
        return new BaseResponse(status);
    }

}
