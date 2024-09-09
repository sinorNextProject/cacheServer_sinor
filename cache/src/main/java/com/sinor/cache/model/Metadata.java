package com.sinor.cache.model;
import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Cacheable
@Table(name = "metadata")
public class Metadata {
	@Id
	private String metadataUrl;

	@Column(nullable = false)
	private Long metadataTtlSecond;

	public static Metadata defaultValue(String metadataUrl){
		return Metadata.builder()
			.metadataUrl(metadataUrl)
			.metadataTtlSecond(60 * 10L)
			.build();
	}

	public static Metadata createValue(String metadataUrl, Long metadataTtlSecond){
		return Metadata.builder()
			.metadataUrl(metadataUrl)
			.metadataTtlSecond(metadataTtlSecond)
			.build();
	}
}