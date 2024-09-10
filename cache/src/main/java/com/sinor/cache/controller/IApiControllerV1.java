package com.sinor.cache.controller;

import com.sinor.cache.model.ApIGetRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.sinor.cache.global.exception.notuse.SuccessResponse;

public interface IApiControllerV1 {
	/**
	 * 단일 캐시 조회
	 * @param key 조회할 캐시의 Key 값
	 */
	@GetMapping("/admin/cache")
	ResponseEntity<SuccessResponse<?>> getCache(@RequestParam String key);

	/**
	 * URL 별 캐시 목록 조회
	 * @param url 조회할 캐시들의 공통 url 값
	 */
	/*@GetMapping("/admin/cache/list")
	ResponseEntity<SuccessResponse<?>> getCacheListByKeyParams(@RequestParam String url);*/

	/**
	 * 단일 캐시 삭제
	 * @param key 삭제할 캐시의 key 값
	 */
	@DeleteMapping("/admin/cache")
	ResponseEntity<?> deleteCache(@RequestParam String key);

	/*@DeleteMapping("/admin/cache/list")
	ResponseEntity<?> deleteCacheList(@RequestParam String url);*/

	/**
	 *
	 * @param request
	 * @return
	 */
	@PutMapping("/admin/cache")
	ResponseEntity<?> updateCache(@RequestBody ApIGetRequest request);
}
