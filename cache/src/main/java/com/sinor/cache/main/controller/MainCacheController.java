package com.sinor.cache.main.controller;

import com.sinor.cache.main.model.MainCacheRequest;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RestController;

import com.sinor.cache.common.CustomException;
import com.sinor.cache.main.service.MainCacheService;

@RestController
public class MainCacheController implements IMainCacheControllerV1 {

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
	@Override
	public String getDataReadCache(String path, MultiValueMap<String, String> queryParams) {
		String pathCache = mainCacheService.getDataInCache(path);
		if (pathCache == null) {
			return mainCacheService.postInCache(path, queryParams);
		}
		return pathCache;
	}

	/**
	 * 데이터 조회 또는 생성 및 캐시 조회
	 *
	 * @apiNote <a href="https://www.baeldung.com/spring-request-response-body#@requestbody">reference</a>
	 * @param path 요청에 전달된 path
	 * @param queryParams 요청에 전달된 queryString
	 * @param body 요청에 전달된 RequestBody 내용에 매핑된 RequestBodyDto 객체
	 */
	@Override
	public String postDataReadCache(String path, MultiValueMap<String, String> queryParams, MainCacheRequest body) {

		return mainCacheService.postMainPathData(path, queryParams, body.getRequestBody());
	}

	/**
	 * 데이터 삭제 및 캐시 갱신
	 *
	 * @apiNote <a href="https://www.baeldung.com/spring-request-response-body#@requestbody">reference</a>
	 * @param path 요청에 전달된 path
	 * @param queryParams 요청에 전달된 queryString
	 */
	@Override
	public String deleteDataRefreshCache(String path, MultiValueMap<String, String> queryParams) {
		return mainCacheService.deleteMainPathData(path, queryParams);
	}

	/**
	 * 데이터 수정 및 캐시 갱신
	 * @apiNote <a href="https://www.baeldung.com/spring-request-response-body#@requestbody">reference</a>
	 * @param path 요청에 전달된 path
	 * @param queryParams 요청에 전달된 queryString
	 * @param body 요청에 전달된 RequestBody 내용에 매핑된 RequestBodyDto 객체
	 */
	@Override
	public String updateDataRefreshCache(String path, MultiValueMap<String, String> queryParams, MainCacheRequest body) {
		return mainCacheService.updateMainPathData(path, queryParams, body.getRequestBody());
	}
}