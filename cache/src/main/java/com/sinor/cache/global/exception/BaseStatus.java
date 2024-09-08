package com.sinor.cache.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum BaseStatus {
    OK(true, HttpStatus.OK),
    INTERNAL_SERVER_ERROR(false, HttpStatus.INTERNAL_SERVER_ERROR);


    private final boolean isSuccess;
    private final HttpStatus status;

    BaseStatus(boolean isSuccess, HttpStatus status) {
        this.isSuccess = isSuccess;
        this.status = status;
    }
}
