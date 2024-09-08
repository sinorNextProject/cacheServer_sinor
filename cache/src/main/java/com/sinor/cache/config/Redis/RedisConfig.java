/*
package com.sinor.cache.config.Redis;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sinor.cache.common.CacheMessage;
import com.sinor.cache.utils.JsonToStringConverter;
import com.sinor.cache.utils.RedisUtils;

@Configuration
public class RedisConfig {

	@Bean
	public RedisConnectionFactory redisConnectionFactory(int index) {
		RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
		redisStandaloneConfiguration.setHostName("redisHost");
		redisStandaloneConfiguration.setPort(6379);
		redisStandaloneConfiguration.setDatabase(index);

		return new LettuceConnectionFactory(redisStandaloneConfiguration); // 여러 다른 Redis 연결 방법이 있을 수 있습니다.
	}

	*/
/**
	 * Cache Data 저장을 위한 Redis 0번 데이터베이스
	 * 사용 시 Redis-cli 내부에서 SELECT 0 접속
 	 *//*

	@Bean
	public RedisConnectionFactory responseRedisConnectionFactory() {
		RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
		redisStandaloneConfiguration.setHostName("redisHost");
		redisStandaloneConfiguration.setPort(6379);
		redisStandaloneConfiguration.setDatabase(0);

		return new LettuceConnectionFactory(redisStandaloneConfiguration); // 여러 다른 Redis 연결 방법이 있을 수 있습니다.
	}

	*/
/**
	 * Metadata 저장을 위한 Redis 1번 데이터베이스
	 * 사용 시 Redis-cli 내부에서 SELECT 1 접속
	 *//*

	@Bean
	public RedisConnectionFactory metadataRedisConnectionFactory() {
		RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
		redisStandaloneConfiguration.setHostName("redisHost");
		redisStandaloneConfiguration.setPort(6379);
		redisStandaloneConfiguration.setDatabase(1);

		return new LettuceConnectionFactory(redisStandaloneConfiguration); // 여러 다른 Redis 연결 방법이 있을 수 있습니다.
	}

	*/
/**
	 * Path 별 활성화된 QueryString 캐시 목록의 저장을 위한 Redis 2번 데이터베이스
	 * 사용 시 Redis-cli 내부에서 SELECT 2 접속
	 * 미사용 예정
	 *//*

	@Bean
	public RedisConnectionFactory cacheListRedisConnectionFactory() {
		RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
		redisStandaloneConfiguration.setHostName("redisHost");
		redisStandaloneConfiguration.setPort(6379);
		redisStandaloneConfiguration.setDatabase(2);

		return new LettuceConnectionFactory(redisStandaloneConfiguration); // 여러 다른 Redis 연결 방법이 있을 수 있습니다.
	}

	*/
/**
	 * 스프링에서 Redis에 접근하기 위한 Template 객체
	 * 0 : response, 1 : metadata, 2 : cacheList, 3 : token
	 *//*

	@Bean(name = "redisTemplate")
	public RedisTemplate<String, String> responseRedisTemplate() {
		RedisTemplate<String, String> template = new RedisTemplate<>();
		template.setConnectionFactory(responseRedisConnectionFactory());
		template.setKeySerializer(new StringRedisSerializer());
		template.setValueSerializer(new StringRedisSerializer());
		return template;
	}

	@Bean(name = "metadataRedisTemplate")
	public RedisTemplate<String, String> metadataRedisTemplate() {
		RedisTemplate<String, String> template = new RedisTemplate<>();
		template.setConnectionFactory(metadataRedisConnectionFactory());
		template.setKeySerializer(new StringRedisSerializer());
		template.setValueSerializer(new StringRedisSerializer());
		return template;
	}

	@Bean(name = "cacheListRedisTemplate")
	public RedisTemplate<String, String> cacheListRedisTemplate() {
		RedisTemplate<String, String> template = new RedisTemplate<>();
		template.setConnectionFactory(cacheListRedisConnectionFactory());
		template.setKeySerializer(new StringRedisSerializer());
		template.setValueSerializer(new StringRedisSerializer());
		return template;
	}

	*/
/**
	 * redis에서 캐시 만료에 대한 메시지를 전달받을 Listener
	 * Response 캐시에 대한 만료를 감지 한다.
	 *//*

	@Bean
	public RedisMessageListenerContainer redisMessageListenerContainer() {
		RedisMessageListenerContainer container = new RedisMessageListenerContainer();
		container.setConnectionFactory(responseRedisConnectionFactory());

		MessageListenerAdapter listenerAdapter = new MessageListenerAdapter(new CacheMessage(cacheListRedisUtils(), jsonToStringConverter()));
		container.addMessageListener(listenerAdapter, new ChannelTopic("__keyevent@0__:expired"));
		container.addMessageListener(listenerAdapter, new ChannelTopic("__keyevent@0__:del"));
		container.addMessageListener(listenerAdapter, new ChannelTopic("__keyevent@0__:set"));

		return container;
	}

	*/
/**
	 * 객체들의 직렬화와 역직렬화를 담당할 ObjectMapper 클래스
	 *//*

	@Bean
	public ObjectMapper objectMapper() {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.registerModule(new JavaTimeModule())
				.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
		return objectMapper;
	}

	*/
/**
	 * ObjectMapper를 실질적으로 사용할 객체 변환기 클래스
	 *//*

	@Bean
	public JsonToStringConverter jsonToStringConverter(){
		return new JsonToStringConverter(objectMapper());
	}

	*/
/**
	 * RedisTemplate를 사용하기 위한 클래스
	 * Redis에 대한 예외처리 등이 처리되어 있으며 Redis 0번 데이터베이스에 접근하여
	 * Response에 대한 접근을 담당한다.
	 *//*

	@Bean(name = "responseRedisUtils")
	public RedisUtils responseRedisUtils(){
		return new RedisUtils(responseRedisTemplate());
	}

	*/
/**
	 * RedisTemplate를 사용하기 위한 클래스
	 * Redis에 대한 예외처리 등이 처리되어 있으며 Redis 1번 데이터베이스에 접근하여
	 * Metadata에 대한 접근을 담당한다.
	 *//*

	@Bean(name = "metadataRedisUtils")
	public RedisUtils metadataRedisUtils(){
		return new RedisUtils(metadataRedisTemplate());
	}

	*/
/**
	 * RedisTemplate를 사용하기 위한 클래스
	 * Redis에 대한 예외처리 등이 처리되어 있으며 Redis 2번 데이터베이스에 접근하여
	 * path 별 캐시 목록에 대한 접근을 담당한다.
	 *//*

	@Bean(name = "cacheListRedisUtils")
	public RedisUtils cacheListRedisUtils(){
		return new RedisUtils(cacheListRedisTemplate());
	}
}
*/
