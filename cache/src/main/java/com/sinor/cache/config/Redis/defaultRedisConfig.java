package com.sinor.cache.config.Redis;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import com.sinor.cache.utils.RedisUtils;

@Configuration
public class defaultRedisConfig {
    /**
     * Cache Data 저장을 위한 Redis 0번 데이터베이스
     * 사용 시 Redis-cli 내부에서 SELECT 0 접속
     */
    @Bean
    public RedisConnectionFactory defaultRedisConnectionFactory() {
        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setHostName("redisHost");
        redisStandaloneConfiguration.setPort(6379);
        redisStandaloneConfiguration.setDatabase(0);

        return new LettuceConnectionFactory(redisStandaloneConfiguration); // 여러 다른 Redis 연결 방법이 있을 수 있습니다.
    }

    /**
     * responseRedisConnectionFactory 를 사용하는 RedisTemplate
     */
    @Bean(name = "redisTemplate")
    public RedisTemplate<String, String> defaultRedisTemplate() {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(defaultRedisConnectionFactory());
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        return template;
    }

    /**
     * RedisTemplate를 사용, 예외 처리하기 위한 클래스
     * Redis에 대한 예외처리 등이 처리되어 있으며 Redis 0번 데이터베이스에 접근하여
     * Response에 대한 접근을 담당한다.
     */
    @Bean
	public RedisUtils defaultRedisUtils(){
		return new RedisUtils(defaultRedisTemplate());
	}
}
