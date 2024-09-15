package com.sinor.cache.controller;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import com.fasterxml.jackson.databind.ser.Serializers;
import com.sinor.cache.global.exception.BaseStatus;
import com.sinor.cache.global.exception.DataResponse;
import com.sinor.cache.model.ApIGetRequest;
import com.sinor.cache.model.ApiGetResponse;
import com.sinor.cache.service.ApiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sinor.cache.notuse.ResponseStatus;
import com.sinor.cache.notuse.SuccessResponse;

@RestController
public class ApiController {
	// 해당 컨트롤러의 API 구조는 캐시의 Key 값에 의해 수정될 필요가 있음
	private final ApiService apiService;

	@Autowired
	public ApiController(ApiService apiService) {
		this.apiService = apiService;
	}

	/**
	 * 단일 캐시 조회
	 *
	 * @param key 조회할 캐시의 Key 값
	 */

	@GetMapping("/admin/cache")
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

	/*@Override
	public ResponseEntity<SuccessResponse<?>> getCacheListByKeyParams(String url) {
		SuccessResponse<?> adminResponse = SuccessResponse.from(ResponseStatus.SUCCESS,
			apiService.findCacheList(url));
		return ResponseEntity.status(ResponseStatus.SUCCESS.getCode()).body(adminResponse);
	}*/

	/**
	 * 단일 캐시 삭제
	 * @param key 삭제할 캐시의 key 값
	 */
	@DeleteMapping("/admin/cache")
	public ResponseEntity<?> deleteCache(String key) {

		//TODO 인코딩된 부분을 해결하기 위해 작성(개선 필요)
		String encodingKey = URLEncoder.encode(key, StandardCharsets.UTF_8)
			.replace("%3F", "?")
			.replace("%26", "&")
			.replace("%3D", "=");
		
		// key 삭제 처리
		Boolean result = apiService.deleteCacheById(encodingKey);
		
		// 처리 결과에 따른 Status 설정
		BaseStatus status;
		if(result)
			status = BaseStatus.OK;
		else
			status = BaseStatus.INTERNAL_SERVER_ERROR;

		// 결과 및 Status 반환
		DataResponse<?> response = DataResponse.from(status, result);
		return ResponseEntity.status(response.getStatus()).body(response);
	}

	/*@Override
	public ResponseEntity<?> deleteCacheList(String url) {
		apiService.deleteCacheList(url);
		return ResponseEntity.status(ResponseStatus.SUCCESS.getCode()).body("삭제 성공");
	}*/

	@PutMapping("/admin/cache")
	public ResponseEntity<?> updateCache(ApIGetRequest request) {
		ApiGetResponse adminResponse = apiService.updateCacheById(request.getKey(), request.getResponse());

		return ResponseEntity.status(BaseStatus.OK.getStatus()).body(adminResponse);
	}
}
