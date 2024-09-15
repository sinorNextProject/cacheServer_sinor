package com.sinor.cache.service;

import com.sinor.cache.global.exception.BaseException;
import com.sinor.cache.global.exception.BaseStatus;
import com.sinor.cache.model.ApiGetResponse;
import com.sinor.cache.model.MetadataGetResponse;
import com.sinor.cache.notuse.admin.AdminException;
import com.sinor.cache.utils.JsonToStringConverter;
import com.sinor.cache.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.Cursor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

@Slf4j
@Service
@Transactional(readOnly = true)
public class ApiService {

	private final JsonToStringConverter jsonToStringConverter;
	private final MetadataService metadataService;
	private final RedisUtils defaultRedisUtils;

	@Autowired
	public ApiService(MetadataService metadataService, JsonToStringConverter jsonToStringConverter,
					  RedisUtils defaultRedisUtils) {
		this.metadataService = metadataService;
		this.jsonToStringConverter = jsonToStringConverter;
		this.defaultRedisUtils = defaultRedisUtils;
	}

	/**
	 * 캐시 조회
	 * @param key 조회할 캐시의 Key 값
	 */
	public ApiGetResponse findCacheById(String key) throws BaseException {
		String value = defaultRedisUtils.getRedisData(key);

		return jsonToStringConverter.jsontoClass(value, ApiGetResponse.class);
	}

	/**
	 * path의 활성 캐시 목록 조회
	 * @param path 조회할 캐시들의 path
	 */
	/*@Override
	// 활성 캐시가 비어있을 경우 빈 리스트를 반환하도록 설정되어 있음, 에러 처리를 할 거면 mgetRedisData에서 처리할 것.
	public List<ApiGetResponse> findCacheList(String path) throws AdminException {
		// path의 활성 캐시 목록 조회 및 최신 버전 값 붙이기
		ArrayList<String> uriList = jsonToStringConverter.jsontoClass(cacheListRedisUtils.getRedisData(path), ArrayList.class);
		
		// 활성 캐시들의 response 조회 및 ApiGetResponse 역직렬화
		List<ApiGetResponse> response = defaultRedisUtils.mgetRedisData(uriList).stream().map(
			value -> jsonToStringConverter.jsontoClass(value, ApiGetResponse.class)
		).toList();

		return response;
	}*/

	/**
	 * 캐시 생성 및 덮어쓰기
	 * @param key 생성할 캐시의 Key
	 * @param value 생성할 캐시의 Value
	 * @param expiredTime 생성할 캐시의 만료시간
	 */
	@Transactional
	public ApiGetResponse saveOrUpdate(String key, String value, Long expiredTime) throws BaseException {
		// 캐시에 저장된 값이 있으면 수정, 없으면 생성
		defaultRedisUtils.setRedisData(key, value, expiredTime);

		return jsonToStringConverter.jsontoClass(defaultRedisUtils.getRedisData(key), ApiGetResponse.class);
	}

	/**
	 * 캐시 삭제
	 * @param key 삭제할 캐시의 Key
	 */
	public Boolean deleteCacheById(String key) throws BaseException {
		log.info("value of deleted key: " + key);
		return defaultRedisUtils.deleteCache(key);
	}

	/**
	 * URI별 활성 캐시 리스트 삭제
	 * @param path 삭제할 캐시들의 공통 path
	 */
	/*@Override
	public void deleteCacheList(String path) throws AdminException {
		// path의 활성 캐시 목록 조회
		ArrayList<String> uriList = jsonToStringConverter.jsontoClass(cacheListRedisUtils.getRedisData(path), ArrayList.class);

		// 조회한 목록 삭제
		Long count = defaultRedisUtils.deleteCache(uriList);

		log.info(path + " 활성 캐시 " + count + "개 삭제");
	}*/

	/**
	 *
	 * @param key 수정할 value의 키 값
	 * @param response 수정내용
	 * @return 수정된 결과값
	 */
	//TODO Redis에서 업데이트 확인, 출력을 위한 역직렬화 과정에서 오류 발생(response의 형식이 너무 까다로움)
	public ApiGetResponse updateCacheById(String key, String response) {
		// path 추출, 해당 path의 metadata 조회
		MetadataGetResponse metadata = metadataService.findMetadataById(defaultRedisUtils.disuniteKey(key));

		// 해당 캐시가 없으면 에러 반환
		if (!defaultRedisUtils.isExist(key))
			throw new BaseException(BaseStatus.INTERNAL_SERVER_ERROR, "수정하려는 캐시가 없습니다.");
		
		// 추출한 Metadata ttl 값으로 캐시 데이터와 변경
		defaultRedisUtils.setRedisData(key, response, metadata.getMetadataTtlSecond());
		// 변경한 데이터를 추출하여 ApiGetResponse 반환
		return jsonToStringConverter.jsontoClass(defaultRedisUtils.getRedisData(key), ApiGetResponse.class);
	}

	/**
	 * RedisTemplate에서 얻은 byte Cursor 값을 CacheGetResponse List 형태로 담아 반환하는 메소드
	 * @param cursor Redis에서 조회로 얻은 Byte 값
	 * @param list cursor를 역직렬화해서 넣어줄 List 객체
	 * @throws AdminException 역직렬화 시 JsonProcessingException이 발생했을 때 Throw될 BaseException
	 */
	private void processCursor(Cursor<byte[]> cursor, List<ApiGetResponse> list) throws AdminException {
		while (cursor.hasNext()) {
			byte[] keyBytes = cursor.next();
			String key = new String(keyBytes, UTF_8);

			String jsonValue = defaultRedisUtils.getRedisData(key);
			list.add(jsonToStringConverter.jsontoClass(jsonValue, ApiGetResponse.class));
		}
	}
}
