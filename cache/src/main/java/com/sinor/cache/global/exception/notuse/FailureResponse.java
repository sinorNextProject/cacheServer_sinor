package com.sinor.cache.global.exception.notuse;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.sinor.cache.global.exception.notuse.admin.AdminResponseStatus;
import com.sinor.cache.global.exception.notuse.main.MainResponseStatus;

import lombok.Getter;

@Getter
@JsonPropertyOrder({"timestamp", "isSuccess", "code", "message"})
public class FailureResponse {
	private final LocalDateTime timestamp;
	@JsonProperty("isSuccess")
	private final Boolean isSuccess;
	private final int code;
	private final String message;

	private FailureResponse(ResponseStatus status) {
		this.timestamp = LocalDateTime.now();
		this.isSuccess = status.isSuccess();
		this.message = status.getMessage();
		this.code = status.getCode();
	}

	private FailureResponse(AdminResponseStatus status) {
		this.timestamp = LocalDateTime.now();
		this.isSuccess = status.isSuccess();
		this.message = status.getMessage();
		this.code = status.getCode();
	}

	private FailureResponse(MainResponseStatus status) {
		this.timestamp = LocalDateTime.now();
		this.isSuccess = status.isSuccess();
		this.message = status.getMessage();
		this.code = status.getCode();
	}

	public static FailureResponse from(ResponseStatus status) {
		return new FailureResponse(status);
	}

	public static FailureResponse fromByAdmin(AdminResponseStatus status) {
		return new FailureResponse(status);
	}

	public static FailureResponse fromByMain(MainResponseStatus status) {
		return new FailureResponse(status);
	}

}
