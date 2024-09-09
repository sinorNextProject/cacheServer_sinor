package com.sinor.cache.utils;

import static com.sinor.cache.global.exception.notuse.admin.AdminResponseStatus.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sinor.cache.global.exception.BaseException;
import com.sinor.cache.global.exception.BaseStatus;
import com.sinor.cache.global.exception.notuse.admin.AdminException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JsonToStringConverter {
	private final ObjectMapper objectMapper;

	public JsonToStringConverter(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	// 역직렬화 Redis value를 ApiGetReponse로 변환
	public <T> T jsontoClass(String jsonValue, Class<T> clazz) throws BaseException {
		try {
			return objectMapper.readValue(jsonValue, clazz);
		} catch (JsonProcessingException e) {
			log.error("JSON deserialization error occurred: {}", e.getMessage(), e);
			throw new BaseException(BaseStatus.INTERNAL_SERVER_ERROR, clazz + "타입 역직렬화 하는 것에 문제가 발생했습니다.");
		}
	}

	// 직렬화
	public <T> String objectToJson(T jsonValue) throws BaseException {
		try {
			return objectMapper.writeValueAsString(jsonValue);
		} catch (JsonProcessingException e) {
			log.error("JSON deserialization error occurred: {}", e.getMessage(), e);
			throw new BaseException(BaseStatus.INTERNAL_SERVER_ERROR, "객체를 직렬화 하는 것에 문제가 발생했습니다.");
		}
	}
}
