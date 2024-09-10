package com.sinor.cache.notuse;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Getter;

@Getter
@JsonPropertyOrder({"timestamp", "isSuccess", "code", "message", "data"})
public class SuccessResponse<T> {
	private final LocalDateTime timestamp;
	@JsonProperty("isSuccess")
	private final Boolean isSuccess;
	private final int code;
	private final String message;
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private final T data;

	// 요청에 성공한 경우
	private SuccessResponse(ResponseStatus status, T data) {
		this.timestamp = LocalDateTime.now();
		this.isSuccess = status.isSuccess();
		this.message = status.getMessage();
		this.code = status.getCode();
		this.data = data;
	}

	private SuccessResponse(ResponseStatus status) {
		this.timestamp = LocalDateTime.now();
		this.isSuccess = status.isSuccess();
		this.message = status.getMessage();
		this.code = status.getCode();
		this.data = null;
	}

	public static SuccessResponse<?> fromNoData(ResponseStatus status) {
		return new SuccessResponse<>(status);
	}

	public static <T> SuccessResponse<?> from(ResponseStatus status, T data) {
		return new SuccessResponse<>(status, data);
	}
}

