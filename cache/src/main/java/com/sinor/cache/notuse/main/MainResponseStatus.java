package com.sinor.cache.notuse.main;

import lombok.Getter;

@Getter
public enum MainResponseStatus {

	//캐시 관련
	CACHE_CREATED(true, 201, "캐시 생성에 성공하였습니다."),
	CACHE_NOT_FOUND(false, 500, "캐시를 찾을 수 없습니다."),
	CACHE_CREATION_FAILED(false, 500, "캐시를 생성할 수 없습니다."),

	CONNECTION_FAILED(false, 404, "통신에 실패했습니다.");

	private final boolean isSuccess;
	private final int code;
	private final String message;

	MainResponseStatus(boolean isSuccess, int code, String message) {
		this.isSuccess = isSuccess;
		this.code = code;
		this.message = message;
	}
}
