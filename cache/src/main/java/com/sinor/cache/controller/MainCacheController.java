package com.sinor.cache.controller;

import com.sinor.cache.global.exception.BaseStatus;
import com.sinor.cache.global.exception.DataResponse;
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

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

//TODO logback-spring.xml파일에 UTF-8설정을 해주었지만 한글 출력X,도커와 관련된 문제가 아닐까 추측
@Slf4j
@RestController
public class MainCacheController {

	private final ConcurrentHashMap<String, Lock> locks = new ConcurrentHashMap<>();
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
	public ResponseEntity<DataResponse<?>> getDataReadCache(@PathVariable String path,
															@RequestParam(required = false) MultiValueMap<String, String> queryParams,
															@RequestHeader MultiValueMap<String, String> headers) {
		// path, queryString 연결, key 반환
		String key = URIUtils.queryStringConcatenateToPath(path, queryParams);

		// key 잠금
		log.info(key + " 잠금.");
		Lock getLock = locks.computeIfAbsent(path, k -> new ReentrantLock());
		getLock.lock();
		try {
			log.info("1. " + queryParams.toString());
			MultiValueMap<String, String> encodedQueryParams = URIUtils.encodingUrl(queryParams);

			MainCacheResponse pathCache = mainCacheService.getDataInCache(path, encodedQueryParams, headers);

			// 메인 요청 및 캐시 생성
			if (pathCache == null)
				pathCache = mainCacheService.postInCache(path, encodedQueryParams, headers);

			//TODO niginx.conf에 설정해두어서 원래 clientIp가 출력되야 하는데, null값 출력
			//log.info("request info: ip={}\n body={}", headers.getFirst("X-Forwarded-For"), pathCache.getBody());

			// 헤더 재조립
			/*HttpHeaders header = new HttpHeaders();
			MultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<>();
			multiValueMap.setAll(pathCache.getHeaders());
			header.addAll(multiValueMap);*/

			DataResponse<?> cacheResponse = DataResponse.from(BaseStatus.OK, pathCache.getBody());

			return ResponseEntity.status(cacheResponse.getStatus()).body(cacheResponse);
		}finally {
			// key 잠금 해제
			getLock.unlock();
			locks.remove(key);
			log.info(key + " 잠금 해제.");
		}
		//return ResponseEntity.status(pathCache.getStatusCodeValue()).headers(header).body(pathCache.getBody());
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
	public ResponseEntity<DataResponse<?>> postDataReadCache(@PathVariable String path,
													@RequestParam(required = false) MultiValueMap<String, String> queryParams,
													MainCacheRequest body, @RequestHeader MultiValueMap<String, String> headers) {


		DataResponse<?> cacheResponse = DataResponse.from(BaseStatus.OK, mainCacheService.postMainPathData(path, URIUtils.encodingUrl(queryParams),
				body.getRequestBody(), headers));

		return ResponseEntity.status(cacheResponse.getStatus()).body(cacheResponse);
	}

	/**
	 * 데이터 삭제 및 캐시 갱신
	 *
	 * @param path        요청에 전달된 path
	 * @param queryParams 요청에 전달된 queryString
	 * @apiNote <a href="https://www.baeldung.com/spring-request-response-body#@requestbody">reference</a>
	 */
	@DeleteMapping("/{path}")
	public ResponseEntity<DataResponse<?>> deleteDataRefreshCache(@PathVariable String path,
														 @RequestParam(required = false) MultiValueMap<String, String> queryParams,
														 @RequestHeader MultiValueMap<String, String> headers) {

		DataResponse<?> cacheResponse = DataResponse.from(BaseStatus.OK, mainCacheService.deleteMainPathData(path, URIUtils.encodingUrl(queryParams), headers));

		return ResponseEntity.status(cacheResponse.getStatus()).body(cacheResponse);
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
	public ResponseEntity<DataResponse<?>> updateDataRefreshCache(@PathVariable String path,
														 @RequestParam(required = false) MultiValueMap<String, String> queryParams,
														 MainCacheRequest body, @RequestHeader MultiValueMap<String, String> headers) {

		DataResponse<?> cacheResponse = DataResponse.from(BaseStatus.OK, mainCacheService.updateMainPathData(path, URIUtils.encodingUrl(queryParams),
				body.getRequestBody(), headers));

		return ResponseEntity.status(cacheResponse.getStatus()).body(cacheResponse);


	}
}