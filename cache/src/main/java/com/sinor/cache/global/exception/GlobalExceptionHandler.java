package com.sinor.cache.global.exception;

import com.sinor.cache.global.exception.notuse.FailureResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.sinor.cache.global.exception.notuse.admin.AdminException;
import com.sinor.cache.global.exception.notuse.main.MainException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
	/**
	 * AdminException Handler
	 */
	@ExceptionHandler(AdminException.class)
	public ResponseEntity<?> adminException(AdminException e) {
		log.error("Custom Exception Occurred : " + e.getStatus().getCode(), e);

		FailureResponse responseBody = FailureResponse.fromByAdmin(e.getStatus());
		return ResponseEntity.status((e).getStatus().getCode()).body(responseBody);
	}

	/**
	 * mainException Handler
	 */
	@ExceptionHandler(MainException.class)
	public ResponseEntity<?> mainException(MainException e) {
		log.error("Custom Exception Occurred : " + e.getStatus().getCode(), e);

		FailureResponse responseBody = FailureResponse.fromByMain(e.getStatus());

		return ResponseEntity.status((e).getStatus().getCode()).body(responseBody);
	}

	@ExceptionHandler(BaseException.class)
	public ResponseEntity<?> baseException(BaseException e) {
		log.error("Base Exception Occurred : " + e.getCause());
		BaseResponse responseBody = BaseResponse.from(e.getStatus());
		return ResponseEntity.status((e).getStatus().getStatus()).body(responseBody);
	}

	/**
	 * Internal Server Error Exception Handler
	 */
	@ExceptionHandler(Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ResponseEntity<?> Exception(Exception e) {
		log.error("Internal Server Error occurred : " + HttpStatus.INTERNAL_SERVER_ERROR.value(), e);

		FailureResponse responseBody = FailureResponse.from(
			com.sinor.cache.global.exception.notuse.ResponseStatus.INTERNAL_SERVER_ERROR);

		return ResponseEntity.internalServerError().body(responseBody);
	}
}