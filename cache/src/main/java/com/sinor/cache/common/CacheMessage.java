package com.sinor.cache.common;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;

import com.sinor.cache.utils.JsonToStringConverter;
import com.sinor.cache.utils.RedisUtils;

import lombok.extern.slf4j.Slf4j;

/**
 * Redis 메시지가 감지 됬을 때 이벤트를 처리할 메소드
 */
@Service
@Slf4j
public class CacheMessage implements MessageListener {
	private final RedisUtils cacheListRedisUtils;
	private final JsonToStringConverter jsonToStringConverter;

	@Autowired
	public CacheMessage(@Qualifier("cacheListRedisUtils") RedisUtils cacheListRedisUtils,
		JsonToStringConverter jsonToStringConverter) {
		this.cacheListRedisUtils = cacheListRedisUtils;
		this.jsonToStringConverter = jsonToStringConverter;
	}

	/**
	 * Redis 메세지 수신 시 실행 이벤트
	 * @param message message must not be {@literal null}.
	 * @param pattern pattern matching the channel (if specified) - can be {@literal null}.
	 */
	//TODO SET에 대한 redis 메시지 발행을 위한 conf 설정이 시간 관계로 EA로 설정되어 있음 차후 효율성있는 설정으로 변경 필요
	//TODO SET에 대한 메시지를 추가한 이유는 ApiController에서 path별 활성화된 캐시들의 조회와 삭제를 효율성 있게 하기 위해서 인데 시간부족으로 미작성
	@Override
	public void onMessage(Message message, byte[] pattern) {
		System.out.println("pattern : " + new String(pattern));
		System.out.println("Redis Message : " + message);
		switch (new String(pattern)) {
			case "__keyevent@0__:del" -> writeLogDel(message);
			case "__keyevent@0__:expired" -> writeLogExpired(message);
			case "__keyevent@0__:set" -> writeLogSet(message);
		}
	}

	private void writeLogExpired(Message keyName){
		log.info("Received Redis expiration event for key: " + keyName);
		removeCacheList(keyName.toString());
	}

	private void writeLogDel(Message keyName){
		log.info("Received Redis del event for key: " + keyName);
		removeCacheList(keyName.toString());
	}

	private void writeLogSet(Message keyName){
		log.info("Received Redis set event for key: " + keyName);
		insertCacheList(keyName.toString());
	}

	private void insertCacheList(String key){

		// 이후 path 값을 추출한다.
		String path = cacheListRedisUtils.disuniteKey(key);
		String queryString = cacheListRedisUtils.getQueryString(key);

		if(queryString.isEmpty())
			return;
		
		// 해당 path의 캐시 목록을 조회한 뒤 list에서 해당 queryString 삽입
		ArrayList<String> list;
		if(!cacheListRedisUtils.isExist(path)) {
			list = new ArrayList<>();
		}else {
			list = jsonToStringConverter.jsontoClass(cacheListRedisUtils.getRedisData(path),
				ArrayList.class);
		}

		list.add(key);
		cacheListRedisUtils.setRedisData(path, jsonToStringConverter.objectToJson(list));
	}

	private void removeCacheList(String key){

		// 이후 path, queryString 값을 추출한다.
		String path = cacheListRedisUtils.disuniteKey(key);
		String queryString = cacheListRedisUtils.getQueryString(key);

		if(queryString.isEmpty())
			return;

		// 해당 path의 캐시 목록을 조회한 뒤 list에서 해당 queryString 삽입
		ArrayList<String> list;
		if(!cacheListRedisUtils.isExist(path)) {
			list = new ArrayList<>();
		}else {
			list = jsonToStringConverter.jsontoClass(cacheListRedisUtils.getRedisData(path),
				ArrayList.class);
		}

		list.remove(key);
		cacheListRedisUtils.setRedisData(path, jsonToStringConverter.objectToJson(list));
	}
}