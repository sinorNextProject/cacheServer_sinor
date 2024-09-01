package com.sinor.cache.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApiGetResponse implements Serializable {
	// 캐시를 생성하거나, Admin측에서 조회하기 위한 Response
	// 유저에게는 하위에 들어갈 response만 반환
	//@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
	private LocalDateTime createAt; // 생성시간
	private Long ttl; // 설정 만료 시간 (Metadata value)
	private String url; // 상위 URL
	private MainCacheResponse response; // 해당 캐시에 대한 응답

	public static ApiGetResponse from(MetadataGetResponse metadataGetResponse, MainCacheResponse response) {
		return ApiGetResponse.builder()
			.createAt(LocalDateTime.now(ZoneId.of("Asia/Seoul")))
			.ttl(metadataGetResponse.getMetadataTtlSecond())
			.url(metadataGetResponse.getMetadataUrl())
			.response(response)
			.build();
	}
}
