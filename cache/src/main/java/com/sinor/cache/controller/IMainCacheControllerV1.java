package com.sinor.cache.controller;

import com.sinor.cache.model.MainCacheRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

// https://www.baeldung.com/jackson-mapping-dynamic-object#using-jsonanysetter
// 어떤 형태로 요청이 들어오든 json 타입이라는 가정만 있으면 모두 Map<String, Object> 형식으로 저장 가능하다고 생각
// 기술적인 부분은 시간이 없어서 검증을 못했고, 일단 모든 Dto를 구분하는 기존 방식으로 작성

/**
 * 각 API domainPath에 해당하는 내용을 정적으로 구현
 */
public interface IMainCacheControllerV1 {
	/**
	 * 데이터 조회 및 캐시 조회
	 *
	 * @param path        요청에 전달된 path
	 * @param queryParams 요청에 전달된 queryString
	 * @apiNote <a href="https://www.baeldung.com/spring-request-response-body#@requestbody">reference</a>
	 */
	@GetMapping("/{path}")
	ResponseEntity<?> getDataReadCache(@PathVariable String path,
		@RequestParam(required = false) MultiValueMap<String, String> queryParams,
		@RequestHeader MultiValueMap<String, String> headers);

	/**
	 * 데이터 조회 또는 생성 및 캐시 조회
	 *
	 * @param path        요청에 전달된 path
	 * @param queryParams 요청에 전달된 queryString
	 * @param body        요청에 전달된 RequestBody 내용에 매핑된 RequestBodyDto 객체
	 * @apiNote <a href="https://www.baeldung.com/spring-request-response-body#@requestbody">reference</a>
	 */
	@PostMapping("/{path}")
	ResponseEntity<String> postDataReadCache(@PathVariable String path,
		@RequestParam(required = false) MultiValueMap<String, String> queryParams,
		MainCacheRequest body, @RequestHeader MultiValueMap<String, String> headers);

	/**
	 * 데이터 삭제 및 캐시 갱신
	 *
	 * @param path        요청에 전달된 path
	 * @param queryParams 요청에 전달된 queryString
	 * @apiNote <a href="https://www.baeldung.com/spring-request-response-body#@requestbody">reference</a>
	 */
	@DeleteMapping("/{path}")
	ResponseEntity<String> deleteDataRefreshCache(@PathVariable String path,
		@RequestParam(required = false) MultiValueMap<String, String> queryParams,
		@RequestHeader MultiValueMap<String, String> headers);

	/**
	 * 데이터 수정 및 캐시 갱신
	 *
	 * @param path        요청에 전달된 path
	 * @param queryParams 요청에 전달된 queryString
	 * @param body        요청에 전달된 RequestBody 내용에 매핑된 RequestBodyDto 객체
	 * @apiNote <a href="https://www.baeldung.com/spring-request-response-body#@requestbody">reference</a>
	 */
	@PutMapping("/{path}")
	ResponseEntity<String> updateDataRefreshCache(@PathVariable String path,
												  @RequestParam(required = false) MultiValueMap<String, String> queryParams,
												  MainCacheRequest body, @RequestHeader MultiValueMap<String, String> headers);
}
