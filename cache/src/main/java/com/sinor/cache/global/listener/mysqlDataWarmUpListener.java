package com.sinor.cache.global.listener;

import com.sinor.cache.model.Metadata;
import com.sinor.cache.service.MetadataService;
import com.sinor.cache.utils.JsonToStringConverter;
import com.sinor.cache.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.List;

@Configuration
@Slf4j
public class mysqlDataWarmUpListener implements ApplicationListener<ApplicationReadyEvent> {
    private final MetadataService metadataService;
    private final JsonToStringConverter jsonToStringConverter;
    private final RedisUtils metadataRedisUtils;

    public mysqlDataWarmUpListener(MetadataService metadataService, JsonToStringConverter jsonToStringConverter, RedisUtils metadataRedisUtils) {
        this.metadataService = metadataService;
        this.jsonToStringConverter = jsonToStringConverter;
        this.metadataRedisUtils = metadataRedisUtils;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        log.info("ApplicationReady! DataWarmUp!");
        dataWarmUp();
    }

    private void dataWarmUp(){
        List<Metadata> list = metadataService.findAll();

        for (Metadata metadata : list) {
            String cacheData = jsonToStringConverter.objectToJson(metadata);
            metadataRedisUtils.setRedisData(metadata.getMetadataUrl(), cacheData);
            log.info(metadata.getMetadataUrl() + " Cache Created : " + LocalDateTime.now());
        }
    }
}
