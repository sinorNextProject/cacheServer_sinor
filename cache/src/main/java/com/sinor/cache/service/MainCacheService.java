package com.sinor.cache.service;

import java.util.Map;

import com.sinor.cache.model.ApiGetResponse;
import com.sinor.cache.model.MainCacheResponse;
import com.sinor.cache.model.MetadataGetResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.sinor.cache.common.admin.AdminException;
import com.sinor.cache.common.main.MainException;
import com.sinor.cache.common.main.MainResponseStatus;
import com.sinor.cache.utils.JsonToStringConverter;
import com.sinor.cache.utils.RedisUtils;
import com.sinor.cache.utils.URIUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class MainCacheService implements IMainCacheServiceV1 {

	private final WebClient webClient;
	private final MetadataService metadataService;
	private final JsonToStringConverter jsonToStringConverter;
	private final RedisUtils responseRedisUtils;

	@Autowired
	public MainCacheService(WebClient webClient, MetadataService metadataService,
		JsonToStringConverter jsonToStringConverter,
		RedisUtils responseRedisUtils) {
		this.webClient = webClient;
		this.metadataService = metadataService;
		this.jsonToStringConverter = jsonToStringConverter;
		this.responseRedisUtils = responseRedisUtils;
	}

	/**
	 * Main 서버에 요청을 보내는 메서드
	 *
	 * @param path        요청 path
	 * @param queryString 요청 queryString
	 */
	public ResponseEntity<String> getMainPathData(String path, MultiValueMap<String, String> queryString,
		MultiValueMap<String, String> headers) {

		//테스트 Main uri
		try {
			ResponseEntity<String> response = webClient.get()
				.uri(URIUtils.uriComponentsBuilder(path, queryString).build().toUri())
				.headers(header -> header.addAll(headers))
				.retrieve()
				.toEntity(String.class)
				.block();

			//TRANSFER_ENCODING 헤더 제거
			HttpHeaders modifiedHeaders = new HttpHeaders();
			modifiedHeaders.addAll(response.getHeaders());
			modifiedHeaders.remove(HttpHeaders.TRANSFER_ENCODING);

			ResponseEntity<String> modifiedResponse = ResponseEntity
				.status(response.getStatusCode())
				.headers(modifiedHeaders)
				.body(response.getBody());

			return modifiedResponse;
		} catch (WebClientResponseException e) {
			throw new MainException(MainResponseStatus.CONNECTION_FAILED);
		}
	}

	/**
	 * Main 서버에 요청을 보내는 메서드
	 *
	 * @param path        요청 path
	 * @param queryString 요청 queryString
	 * @param body        Requestbody
	 */
	public ResponseEntity<String> postMainPathData(String path, MultiValueMap<String, String> queryString,
		Map<String, String> body, MultiValueMap<String, String> headers) {

		try {
			ResponseEntity<String> response = webClient.post()
				.uri(URIUtils.uriComponentsBuilder(path, queryString).build().toUri())
				.bodyValue(body)
				.headers(header -> header.addAll(headers))
				.retrieve()
				.toEntity(String.class)
				.block();

			//TRANSFER_ENCODING 헤더 제거
			HttpHeaders modifiedHeaders = new HttpHeaders();
			modifiedHeaders.addAll(response.getHeaders());
			modifiedHeaders.remove(HttpHeaders.TRANSFER_ENCODING);

			ResponseEntity<String> modifiedResponse = ResponseEntity
				.status(response.getStatusCode())
				.headers(modifiedHeaders)
				.body(response.getBody());

			return modifiedResponse;
		} catch (WebClientResponseException e) {
			throw new MainException(MainResponseStatus.CONNECTION_FAILED);
		}
	}

	/**
	 * Main 서버에 요청을 보내는 메서드
	 *
	 * @param path        요청 path
	 * @param queryString 요청 queryString
	 */
	public ResponseEntity<String> deleteMainPathData(String path, MultiValueMap<String, String> queryString,
		MultiValueMap<String, String> headers) {

		try {
			ResponseEntity<String> response = webClient.delete()
				.uri(URIUtils.uriComponentsBuilder(path, queryString).build().toUri())
				.headers(header -> header.addAll(headers))
				.retrieve()
				.toEntity(String.class)
				.block();

			//TRANSFER_ENCODING 헤더 제거
			HttpHeaders modifiedHeaders = new HttpHeaders();
			modifiedHeaders.addAll(response.getHeaders());
			modifiedHeaders.remove(HttpHeaders.TRANSFER_ENCODING);

			ResponseEntity<String> modifiedResponse = ResponseEntity
				.status(response.getStatusCode())
				.headers(modifiedHeaders)
				.body(response.getBody());

			return modifiedResponse;
		} catch (WebClientResponseException e) {
			throw new MainException(MainResponseStatus.CONNECTION_FAILED);
		}
	}

	/**
	 * Main 서버에 요청을 보내는 메서드
	 *
	 * @param path        요청 path
	 * @param queryString 요청 queryString
	 * @param body        Requestbody
	 */
	public ResponseEntity<String> updateMainPathData(String path, MultiValueMap<String, String> queryString,
		Map<String, String> body, MultiValueMap<String, String> headers) {
		try {
			ResponseEntity<String> response = webClient.put()
				.uri(URIUtils.uriComponentsBuilder(path, queryString).build().toUri())
				.bodyValue(body)
				.headers(header -> header.addAll(headers))
				.retrieve()
				.toEntity(String.class)
				.block();

			//TRANSFER_ENCODING 헤더 제거
			HttpHeaders modifiedHeaders = new HttpHeaders();
			modifiedHeaders.addAll(response.getHeaders());
			modifiedHeaders.remove(HttpHeaders.TRANSFER_ENCODING);

			ResponseEntity<String> modifiedResponse = ResponseEntity
				.status(response.getStatusCode())
				.headers(modifiedHeaders)
				.body(response.getBody());

			return modifiedResponse;
		} catch (WebClientResponseException e) {
			throw new MainException(MainResponseStatus.CONNECTION_FAILED);
		}
	}

	/**
	 * 캐시에 데이터가 있는지 확인하고 없으면 데이터를 조회해서 있으면 데이터를 조회해서 반환해주는 메소드
	 * opsForValue() - Strings 를 쉽게 Serialize / Deserialize 해주는 Interface
	 *
	 * @param path 특정 path에 캐시가 있나 확인하기 위한 파라미터
	 * @return 값이 있다면 value, 없다면 null
	 */
	public MainCacheResponse getDataInCache(String path, MultiValueMap<String, String> queryParams,
											MultiValueMap<String, String> headers) throws AdminException {

		// metadata 확인
		// Metadata 조회
		MetadataGetResponse metadata = metadataService.findMetadataCacheById(path);

		if (metadata == null)
			return null;

		// URI 조합
		String key = URIUtils.getResponseKey(path, queryParams, metadata.getVersion());

		// response 확인
		if (!responseRedisUtils.isExist(key))
			return null;

		// response 조회
		ApiGetResponse cachedData = jsonToStringConverter.jsontoClass(responseRedisUtils.getRedisData(key),
			ApiGetResponse.class);

		return cachedData.getResponse();
	}

	/**
	 * 캐시에 데이터가 없으면 메인 데이터를 조회해서 캐시에 저장하고 반환해주는 메소드
	 *
	 * @param path        검색할 캐시의 Path
	 * @param queryParams 각 캐시의 구별을 위한 QueryString
	 */
	public MainCacheResponse postInCache(String path, MultiValueMap<String, String> queryParams,
		MultiValueMap<String, String> headers) {

		// Main에서 받은 값 CustomResponse로 Body, Header, Status 분할
		ResponseEntity<String> data = getMainPathData(path, queryParams, headers);

		MainCacheResponse mainCacheResponse = MainCacheResponse.from(data);

		// 옵션 값 찾기 or 생성
		MetadataGetResponse metadata = metadataService.findOrCreateMetadataById(path);

		// 캐시 Response 객체를 위에 값을 이용해 생성하고 직렬화
		ApiGetResponse apiGetResponse = ApiGetResponse.from(metadata, mainCacheResponse);
		String response = jsonToStringConverter.objectToJson(apiGetResponse);

		// path + queryString + metadata version 형태의 Key 이름 생성
		String cacheKeyName = URIUtils.getResponseKey(path, queryParams, metadata.getVersion());
		// 캐시 저장
		responseRedisUtils.setRedisData(cacheKeyName, response, metadata.getMetadataTtlSecond());

		// Response만 반환
		return mainCacheResponse;
	}
}
