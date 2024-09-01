package com.sinor.cache.service;

import java.util.Map;

import com.sinor.cache.model.MainCacheResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;

import com.sinor.cache.common.main.MainException;

public interface IMainCacheServiceV1 {
	/**
	 * Main 서버에 요청을 보내는 메서드
	 *
	 * @param path        요청 path
	 * @param queryString 요청 queryString
	 */
	ResponseEntity<?> getMainPathData(String path, MultiValueMap<String, String> queryString,
		MultiValueMap<String, String> headers) throws MainException;

	/**
	 * Main 서버에 요청을 보내는 메서드
	 *
	 * @param path        요청 path
	 * @param queryString 요청 queryString
	 * @param body        Requestbody
	 */
	ResponseEntity<String> postMainPathData(String path, MultiValueMap<String, String> queryString,
		Map<String, String> body, MultiValueMap<String, String> headers) throws MainException;

	/**
	 * Main 서버에 요청을 보내는 메서드
	 *
	 * @param path        요청 path
	 * @param queryString 요청 queryString
	 */
	ResponseEntity<String> deleteMainPathData(String path, MultiValueMap<String, String> queryString,
		MultiValueMap<String, String> headers) throws
		MainException;

	/**
	 * Main 서버에 요청을 보내는 메서드
	 *
	 * @param path        요청 path
	 * @param queryString 요청 queryString
	 * @param body        Requestbody
	 */
	ResponseEntity<String> updateMainPathData(String path, MultiValueMap<String, String> queryString,
		Map<String, String> body, MultiValueMap<String, String> headers) throws MainException;

	/**
	 * 캐시에 데이터가 있는지 확인하고 없으면 데이터를 조회해서 있으면 데이터를 조회해서 반환해주는 메소드
	 * opsForValue() - Strings를 쉽게 Serialize / Deserialize 해주는 Interface
	 *
	 * @param path 특정 path에 캐시가 있나 확인하기 위한 파라미터
	 * @return 값이 있다면 value, 없다면 null
	 */
	MainCacheResponse getDataInCache(String path, MultiValueMap<String, String> queryParams,
		MultiValueMap<String, String> headers) throws MainException;

	/**
	 * 캐시에 데이터가 없으면 메인 데이터를 조회해서 캐시에 저장하고 반환해주는 메소드
	 *
	 * @param path        검색할 캐시의 Path
	 * @param queryString 각 캐시의 구별을 위한 QueryString
	 */
	MainCacheResponse postInCache(String path, MultiValueMap<String, String> queryString,
								  MultiValueMap<String, String> headers) throws MainException;
}
