package com.sinor.cache.global.exception.notuse.admin;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AdminException extends RuntimeException {
	private AdminResponseStatus status;
}
