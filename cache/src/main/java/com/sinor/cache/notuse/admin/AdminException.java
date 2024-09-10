package com.sinor.cache.notuse.admin;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AdminException extends RuntimeException {
	private AdminResponseStatus status;
}
