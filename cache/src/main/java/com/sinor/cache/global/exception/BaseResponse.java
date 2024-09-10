package com.sinor.cache.global.exception;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@JsonPropertyOrder({"timestamp", "isSuccess", "status", "message"})
public class BaseResponse {
    private final LocalDateTime timestamp;
    @JsonProperty("isSuccess")
    private final Boolean isSuccess;
    private final HttpStatus status;
    private final String message;

    protected BaseResponse(BaseStatus status) {
        this.timestamp = LocalDateTime.now();
        this.isSuccess = status.isSuccess();
        this.status = status.getStatus();
        this.message = status.getMessage();
    }

    public static BaseResponse from(BaseStatus status) {
        return new BaseResponse(status);
    }
}
