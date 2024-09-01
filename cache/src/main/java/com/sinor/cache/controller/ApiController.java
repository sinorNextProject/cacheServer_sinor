package com.sinor.cache.controller;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import com.sinor.cache.model.ApIGetRequest;
import com.sinor.cache.model.ApiGetResponse;
import com.sinor.cache.service.IApiServiceV1;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.sinor.cache.common.ResponseStatus;
import com.sinor.cache.common.SuccessResponse;

@RestController
public class ApiController implements IApiControllerV1 {
	// 해당 컨트롤러의 API 구조는 캐시의 Key 값에 의해 수정될 필요가 있음
	private final IApiServiceV1 apiService;

	@Autowired
	public ApiController(IApiServiceV1 apiService) {
		this.apiService = apiService;
	}

	/**
	 * 단일 캐시 조회
	 *
	 * @param key 조회할 캐시의 Key 값
	 */

	@Override
	public ResponseEntity<SuccessResponse<?>> getCache(String key) {

		//TODO 인코딩된 부분을 해결하기 위해 작성(개선 필요)
		String encodingKey = URLEncoder.encode(key, StandardCharsets.UTF_8)
			.replace("%3F", "?")
			.replace("%26", "&")
			.replace("%3D", "=");

		SuccessResponse<?> adminResponse = SuccessResponse.from(ResponseStatus.SUCCESS,
			apiService.findCacheById(encodingKey));
		return ResponseEntity.status(ResponseStatus.SUCCESS.getCode()).body(adminResponse);
	}

	/**
	 * URL 별 캐시 목록 조회
	 * @param url 조회할 캐시들의 공통 url 값
	 */

	@Override
	public ResponseEntity<SuccessResponse<?>> getCacheListByKeyParams(String url) {
		SuccessResponse<?> adminResponse = SuccessResponse.from(ResponseStatus.SUCCESS,
			apiService.findCacheList(url));
		return ResponseEntity.status(ResponseStatus.SUCCESS.getCode()).body(adminResponse);
	}

	/**
	 * 단일 캐시 삭제
	 * @param key 삭제할 캐시의 key 값
	 */
	@Override
	public ResponseEntity<?> deleteCache(String key) {

		//TODO 인코딩된 부분을 해결하기 위해 작성(개선 필요)
		String encodingKey = URLEncoder.encode(key, StandardCharsets.UTF_8)
			.replace("%3F", "?")
			.replace("%26", "&")
			.replace("%3D", "=");

		return ResponseEntity.status(ResponseStatus.SUCCESS.getCode()).body(apiService.deleteCacheById(encodingKey));
	}

	@Override
	public ResponseEntity<?> deleteCacheList(String url) {
		apiService.deleteCacheList(url);
		return ResponseEntity.status(ResponseStatus.SUCCESS.getCode()).body("삭제 성공");
	}

	@Override
	public ResponseEntity<?> updateCache(ApIGetRequest request) {
		ApiGetResponse adminResponse = apiService.updateCacheById(request.getKey(), request.getResponse());

		return ResponseEntity.status(ResponseStatus.SUCCESS.getCode()).body(adminResponse);
	}
}
