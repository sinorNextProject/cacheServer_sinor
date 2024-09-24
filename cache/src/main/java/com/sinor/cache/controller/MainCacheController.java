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
	// TODO 캐시에 있는 데이터르 조회할 때는 굳이 락을 걸 필요가 없는데....
	@GetMapping("/{path:^(?!actuator).*}")
	public ResponseEntity<DataResponse<?>> getDataReadCache(@PathVariable String path,
															@RequestParam(required = false) MultiValueMap<String, String> queryParams,
															@RequestHeader MultiValueMap<String, String> headers) {
		// path, queryString 연결, 한글 인코딩
		String key = URIUtils.queryStringConcatenateToPath(path, queryParams);
		MultiValueMap<String, String> encodedQueryParams = URIUtils.encodingUrl(queryParams);
		
		// 캐시 유무 확인
		log.info("getDataReadCache key 조회 : " + key);
		boolean isExist = mainCacheService.isExist(key);

		// 반환 Response, Lock
		MainCacheResponse pathCache;
		Lock getLock = locks.computeIfAbsent(path, k -> new ReentrantLock());

		// key 잠금
		log.info(key + " Lock 잠금.");
		getLock.lock();

		// 캐시 없으면 메인 요청 Post
		if(!isExist)
			pathCache = mainCacheService.postInCache(path, encodedQueryParams, headers);
		else
			pathCache = mainCacheService.getDataInCache(path, encodedQueryParams, headers);

		// key 잠금 해제
		getLock.unlock();
		locks.remove(key);
		log.info(key + " 잠금 해제.");

		//TODO niginx.conf에 설정해두어서 원래 clientIp가 출력되야 하는데, null값 출력
		//log.info("request info: ip={}\n body={}", headers.getFirst("X-Forwarded-For"), pathCache.getBody());

		DataResponse<?> cacheResponse = DataResponse.from(BaseStatus.OK, pathCache.getBody());

		return ResponseEntity.status(cacheResponse.getStatus()).body(cacheResponse);
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
		String key = URIUtils.queryStringConcatenateToPath(path, queryParams);
		Lock lock = locks.computeIfAbsent(key, k -> new ReentrantLock());
		lock.lock();

		DataResponse<?> cacheResponse = DataResponse.from(BaseStatus.OK, mainCacheService.postMainPathData(path, URIUtils.encodingUrl(queryParams),
				body.getRequestBody(), headers));

		lock.unlock();
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
		String key = URIUtils.queryStringConcatenateToPath(path, queryParams);
		Lock lock = locks.computeIfAbsent(key, k -> new ReentrantLock());
		lock.lock();

		DataResponse<?> cacheResponse = DataResponse.from(BaseStatus.OK, mainCacheService.updateMainPathData(path, URIUtils.encodingUrl(queryParams),
				body.getRequestBody(), headers));

		lock.unlock();
		return ResponseEntity.status(cacheResponse.getStatus()).body(cacheResponse);
	}
}