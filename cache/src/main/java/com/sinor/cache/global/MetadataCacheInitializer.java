package com.sinor.cache.global;

import com.sinor.cache.model.Metadata;
import com.sinor.cache.service.MetadataService;
import com.sinor.cache.utils.JsonToStringConverter;
import com.sinor.cache.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class MetadataCacheInitializer implements ApplicationRunner {
	private final MetadataService metadataService;
	private final JsonToStringConverter jsonToStringConverter;
	private final RedisUtils metadataRedisUtils;

	public MetadataCacheInitializer(MetadataService metadataService, JsonToStringConverter jsonToStringConverter,
		RedisUtils metadataRedisUtils) {
		this.metadataService = metadataService;
		this.jsonToStringConverter = jsonToStringConverter;
		this.metadataRedisUtils = metadataRedisUtils;
	}

	/**
	 * 맨 처음 도커를 실행했을 때 Sql.init보다 먼저 실행되는 것인지 데이터가 들어가지 않음
	 */
	@Override
	public void run(ApplicationArguments args) throws Exception {
		log.info("데이터 Warmup 시작");

		// 애플리케이션 실행 후 Mysql의 Metadata redis 캐시 저장
		DataWarmUp();
	}

	private void DataWarmUp(){
		List<Metadata> list = metadataService.findAll();

		for (Metadata metadata : list) {
			String cacheData = jsonToStringConverter.objectToJson(metadata);
			metadataRedisUtils.setRedisData(metadata.getMetadataUrl(), cacheData);
			log.info(metadata.getMetadataUrl() + " Cache Created : " + LocalDateTime.now());
		}
	}
}
