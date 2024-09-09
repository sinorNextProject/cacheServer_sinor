package com.sinor.cache.global.exception.notuse.main;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MainException extends RuntimeException {
	private MainResponseStatus status;
}
