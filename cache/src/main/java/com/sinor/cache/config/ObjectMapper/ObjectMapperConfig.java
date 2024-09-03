package com.sinor.cache.config.ObjectMapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.sinor.cache.utils.JsonToStringConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Configuration
public class ObjectMapperConfig {
    /**
     * 객체들의 직렬화와 역직렬화를 담당할 ObjectMapper 클래스
     */
	@Bean
	public ObjectMapper objectMapper() {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule())
				.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
		return objectMapper;
	}

    /**
     * ObjectMapper를 실질적으로 사용할 객체 변환기 클래스
	 * 사용할 때의 예외처리를 같이 담당한다.
     */
	@Bean
	public JsonToStringConverter jsonToStringConverter(){
		return new JsonToStringConverter(objectMapper());
	}
}
