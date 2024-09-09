package com.sinor.cache.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum BaseStatus {
    OK(true, HttpStatus.OK, "요청을 처리하였습니다."),
    FORBIDDEN(false, HttpStatus.FORBIDDEN, "권한이 없습니다."),
    NOT_FOUND(false, HttpStatus.NOT_FOUND, "데이터를 찾을 수 없습니다."),
    INTERNAL_SERVER_ERROR(false, HttpStatus.INTERNAL_SERVER_ERROR, "내부 서버의 오류가 발생했습니다.");

    private final boolean isSuccess;
    private final HttpStatus status;
    private final String message;

    BaseStatus(boolean isSuccess, HttpStatus status, String message) {
        this.isSuccess = isSuccess;
        this.status = status;
        this.message = message;
    }
}
