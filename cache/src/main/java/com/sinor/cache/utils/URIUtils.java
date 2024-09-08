package com.sinor.cache.utils;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

public class URIUtils {

	public static UriComponentsBuilder uriComponentsBuilder(String path, MultiValueMap<String, String> queryParams) {
		UriComponentsBuilder builder = UriComponentsBuilder.fromUriString("http://mainHost:8080/");
		builder.path(path);

		if (queryParams != null)
			builder.queryParams(queryParams);

		return builder;
	}

	public static String getResponseKey(String path, MultiValueMap<String, String> queryParams) {
		UriComponentsBuilder uriComponents = UriComponentsBuilder.fromPath(path);

		if (queryParams != null)
			uriComponents.queryParams(decodingUrl(queryParams));

		return uriComponents.toUriString();
	}

	/**
	 * url에 포함되어있는 한글 등을 인코딩
	 * @param queryParams 요청에 전달될 값
	 * @return 인코딩되 결과값
	 */
	public static MultiValueMap<String, String> encodingUrl(MultiValueMap<String, String> queryParams) {

		MultiValueMap<String, String> encodedQueryParams = new LinkedMultiValueMap<>();

		for (String key : queryParams.keySet()) {
			List<String> encodedValues = queryParams.get(key).stream()
				.map(value -> URLEncoder.encode(value, StandardCharsets.UTF_8))
				.collect(Collectors.toList());
			encodedQueryParams.put(key, encodedValues);
		}

		return encodedQueryParams;
	}

	/**
	 * map value UTF-8 decoding
	 * @param queryParams 디코딩 하려는 map 객체
	 * @return value가 디코딩된 map
	 */
	public static MultiValueMap<String, String> decodingUrl(MultiValueMap<String, String> queryParams) {

		MultiValueMap<String, String> decodedQueryParams = new LinkedMultiValueMap<>();

		for (String key : queryParams.keySet()) {
			List<String> decodedValues = queryParams.get(key).stream()
				.map(value -> URLDecoder.decode(value, StandardCharsets.UTF_8))
				.collect(Collectors.toList());
			decodedQueryParams.put(key, decodedValues);
		}

		return decodedQueryParams;
	}

	/**
	 * Response Cache key 생성
	 * @param key Cache Name
	 * @param version Metadata Version
	 * @return key + /V + version
	 */
/*	public static String getUriPathQuery(String key, int version) {
		return key + "/V" + version;
	}*/
}
