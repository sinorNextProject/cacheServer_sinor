package com.sinor.cache.controller;

import com.sinor.cache.model.MainCacheRequest;
import com.sinor.cache.model.MainCacheResponse;
import com.sinor.cache.service.MainCacheService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;

import com.sinor.cache.utils.URIUtils;

import lombok.extern.slf4j.Slf4j;

//TODO logback-spring.xml파일에 UTF-8설정을 해주었지만 한글 출력X,도커와 관련된 문제가 아닐까 추측
@Slf4j
@RestController
public class MainCacheController {

	private final MainCacheService mainCacheService;

	public MainCacheController(MainCacheService mainCacheService) {
		this.mainCacheService = mainCacheService;
	}

	/**
	 * 데이터 조회 및 캐시 조회
	 *
	 * @param path        요청에 전달된 path
	 * @param queryParams 요청에 전달된 queryString
	 * @apiNote <a href="https://www.baeldung.com/spring-request-response-body#@requestbody">reference</a>
	 */
	@GetMapping("/{path}")
	public ResponseEntity<?> getDataReadCache(String path, MultiValueMap<String, String> queryParams,
		MultiValueMap<String, String> headers) {
		log.info("1. " + queryParams.toString());
		MultiValueMap<String, String> encodedQueryParams = URIUtils.encodingUrl(queryParams);

		MainCacheResponse pathCache = mainCacheService.getDataInCache(path, encodedQueryParams, headers);

		// 메인 요청 및 캐시 생성
		if (pathCache == null)
			pathCache = mainCacheService.postInCache(path, encodedQueryParams, headers);

		//TODO niginx.conf에 설정해두어서 원래 clientIp가 출력되야 하는데, null값 출력
		//log.info("request info: ip={}\n body={}", headers.getFirst("X-Forwarded-For"), pathCache.getBody());

		// 헤더 재조립
		HttpHeaders header = new HttpHeaders();
		MultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<>();
		multiValueMap.setAll(pathCache.getHeaders());
		header.addAll(multiValueMap);

		return ResponseEntity.status(pathCache.getStatusCodeValue()).headers(header).body(pathCache.getBody());
	}

	/**
	 * 데이터 조회 또는 생성 및 캐시 조회
	 *
	 * @param path        요청에 전달된 path
	 * @param queryParams 요청에 전달된 queryString
	 * @param body        요청에 전달된 RequestBody 내용에 매핑된 RequestBodyDto 객체
	 * @apiNote <a href="https://www.baeldung.com/spring-request-response-body#@requestbody">reference</a>
	 */
	@PostMapping("/{path}")
	public ResponseEntity<String> postDataReadCache(String path, MultiValueMap<String, String> queryParams,
													MainCacheRequest body, MultiValueMap<String, String> headers) {

		return mainCacheService.postMainPathData(path, URIUtils.encodingUrl(queryParams),
			body.getRequestBody(), headers);
	}

	/**
	 * 데이터 삭제 및 캐시 갱신
	 *
	 * @param path        요청에 전달된 path
	 * @param queryParams 요청에 전달된 queryString
	 * @apiNote <a href="https://www.baeldung.com/spring-request-response-body#@requestbody">reference</a>
	 */
	@DeleteMapping("/{path}")
	public ResponseEntity<String> deleteDataRefreshCache(String path, MultiValueMap<String, String> queryParams,
		MultiValueMap<String, String> headers) {
		return mainCacheService.deleteMainPathData(path, URIUtils.encodingUrl(queryParams), headers);
	}

	/**
	 * 데이터 수정 및 캐시 갱신
	 *
	 * @param path        요청에 전달된 path
	 * @param queryParams 요청에 전달된 queryString
	 * @param body        요청에 전달된 RequestBody 내용에 매핑된 RequestBodyDto 객체
	 * @apiNote <a href="https://www.baeldung.com/spring-request-response-body#@requestbody">reference</a>
	 */
	@PutMapping("/{path}")
	public ResponseEntity<String> updateDataRefreshCache(String path, MultiValueMap<String, String> queryParams,
		MainCacheRequest body, MultiValueMap<String, String> headers) {
		return mainCacheService.updateMainPathData(path, URIUtils.encodingUrl(queryParams),
			body.getRequestBody(), headers);
	}
}