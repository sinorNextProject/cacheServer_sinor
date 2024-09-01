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

	@Column(nullable = false)
	private int version;


	public static Metadata defaultValue(String metadataUrl){
		return Metadata.builder()
			.metadataUrl(metadataUrl)
			.metadataTtlSecond(60 * 10L)
			.version(0)
			.build();
	}

	public static Metadata createValue(String metadataUrl, Long metadataTtlSecond){
		return Metadata.builder()
			.metadataUrl(metadataUrl)
			.metadataTtlSecond(metadataTtlSecond)
			.version(1)
			.build();
	}

	public static Metadata updateValue(String metadataUrl, Long metadataTtlSecond, int version){
		return Metadata.builder()
			.metadataUrl(metadataUrl)
			.metadataTtlSecond(metadataTtlSecond)
			.version(version + 1)
			.build();
	}
}