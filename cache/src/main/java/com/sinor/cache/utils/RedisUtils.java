package com.sinor.cache.utils;

import static com.sinor.cache.notuse.admin.AdminResponseStatus.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import com.sinor.cache.global.exception.BaseException;
import com.sinor.cache.global.exception.BaseStatus;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.KeyScanOptions;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;

import com.sinor.cache.notuse.admin.AdminException;

/**
 * RedisTemplate을 사용할 때 관련 Exception 처리를 해놓은 클래스
 * 에러를 각 장소에 일일이 처리할 것이라면 굳이 사용할 필요는 없음
 */
public class RedisUtils {
	private final RedisTemplate<String, String> redisTemplate;

	public RedisUtils(RedisTemplate<String, String> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	/**
	 * redistemplate 안의 값을 호출
	 * @param key 호출할 캐시의 key 값
	 * @return 해당 key 값의 value (String 형태)
	 * @throws AdminException 역 직렬화 실패 시 발생 오류
	 */
	public String getRedisData(String key) throws BaseException {
		try {
			return redisTemplate.opsForValue().get(key);
		} catch (NullPointerException e) {
			throw new BaseException(BaseStatus.INTERNAL_SERVER_ERROR, key + "에 대한 캐시가 없습니다.");
		}
	}

	/**
	 * 모든 Keys값 리턴.
	 * 어지간하면 미사용 권장.
	 */
	public Set<String> getKeys(){
        return redisTemplate.keys("*");
	}

	/**
	 * 만료 시간 있는 캐시 저장
	 * @param key 생성할 캐시의 key
	 * @param value 생성할 캐시의 value
	 * @param ttl 생성할 캐시의 만료 시간 (Second)
	 */
	public void setRedisData(String key, String value, Long ttl) {
		redisTemplate.opsForValue().set(key, value, ttl, TimeUnit.SECONDS);
	}

	/**
	 * 만료 시간 기본 10분 캐시 저장
	 * @param key
	 * @param value
	 */
	public void setRedisData(String key, String value) {
		redisTemplate.opsForValue().set(key, value, 10L, TimeUnit.MINUTES);
	}

	/**
	 * 캐시가 있는지 확인
	 * @param key 캐시 유무를 확인 할 캐시의 key
	 * @return 있으면 true 없으면 false
	 */
	public Boolean isExist(String key) throws AdminException {
		try {
			return redisTemplate.hasKey(key);
		} catch (NullPointerException e) {
			throw new AdminException(CACHE_NOT_FOUND);
		}
	}

	/**
	 * 일정 패턴을 가진 캐시 목록을 조회
	 * scan을 사용해서 비동기적으로 작동
	 * @param pattern 찾으려는 key의 일부
	 * @return 찾은 key들의 Cursor
	 */
	public Cursor<byte[]> searchPatternKeys(String pattern) {
		return redisTemplate.executeWithStickyConnection(connection -> {
			ScanOptions options = KeyScanOptions.scanOptions().match("*" + pattern + "*").build();
			return connection.keyCommands().scan(options);
		});
	}

	/**
	 * cache key Name에서 Path 부분만 추출
	 * @param key path만 추출할 key Name
	 * @return path of key
	 */
	public String disuniteKey(String key) {

		if (key.contains("?")) {
			return key.substring(0, key.indexOf("?"));
		} else {
			return key;
		}
	}

	/**
	 * uri 에서 queryString만 추출
	 * @param uri 추출할 key uri
	 */
	public String getQueryString(String uri) {
		if (uri.contains("?")) {
			System.out.println("key의 queryString : " + uri.substring(uri.indexOf("?")));
			return uri.substring(uri.indexOf("?"));
		} else {
			return "";
		}
	}

	/**
	 * 여러 키들 동시 조회
	 * @param keys 조회할 키들의 List
	 * @return keys의 Value List
	 */
	public List<String> mgetRedisData(List<String> keys) throws BaseException{
		List<String> list = redisTemplate.opsForValue().multiGet(keys);

		// 비었으면 빈 List 반환
		if(list == null || Objects.requireNonNull(list).isEmpty())
			list = new ArrayList<>();

		return list;
	}

	public Boolean deleteCache(String key) throws BaseException {
		return redisTemplate.delete(key);
	}

	public Long deleteCache(List<String> keys){
		return redisTemplate.delete(keys);
	}

	public void unlinkCache(String key) throws BaseException {
		redisTemplate.unlink(key);
	}
}
