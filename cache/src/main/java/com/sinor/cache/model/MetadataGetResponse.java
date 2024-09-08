package com.sinor.cache.model;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class MetadataGetResponse implements Serializable {
	private String metadataUrl; // URL Key 값

	private Long metadataTtlSecond; // URL 별 설정 만료시간

	@JsonCreator
	public MetadataGetResponse(@JsonProperty("metadataUrl") String metadataUrl, @JsonProperty("metadataTtlSecond") Long metadataTtlSecond) {
		this.metadataUrl = metadataUrl;
		this.metadataTtlSecond = metadataTtlSecond;
	}

	public static MetadataGetResponse from(Metadata metadata){
		return MetadataGetResponse.builder()
			.metadataUrl(metadata.getMetadataUrl())
			.metadataTtlSecond(metadata.getMetadataTtlSecond())
			.build();
	}
}
