package com.sinor.cache.utils;

import static com.sinor.cache.global.exception.notuse.admin.AdminResponseStatus.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sinor.cache.global.exception.notuse.admin.AdminException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JsonToStringConverter {
	private final ObjectMapper objectMapper;

	public JsonToStringConverter(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	// 역직렬화 Redis value를 ApiGetReponse로 변환
	public <T> T jsontoClass(String jsonValue, Class<T> clazz) throws AdminException {
		try {
			return objectMapper.readValue(jsonValue, clazz);
		} catch (JsonProcessingException e) {
			log.error("JSON deserialization error occurred: {}", e.getMessage(), e);
			throw new AdminException(DESERIALIZATION_ERROR);

		}
	}

	// 직렬화
	public <T> String objectToJson(T jsonValue) throws AdminException {
		try {
			return objectMapper.writeValueAsString(jsonValue);
		} catch (JsonProcessingException e) {
			log.error("JSON deserialization error occurred: {}", e.getMessage(), e);
			throw new AdminException(DESERIALIZATION_ERROR);
		}
	}
}
