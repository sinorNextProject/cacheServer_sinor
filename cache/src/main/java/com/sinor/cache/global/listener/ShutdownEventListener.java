package com.sinor.cache.global.listener;

import com.sinor.cache.model.Metadata;
import com.sinor.cache.service.MetadataService;
import com.sinor.cache.utils.JsonToStringConverter;
import com.sinor.cache.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

import java.util.Set;

@Slf4j
@Component
public class ShutdownEventListener implements ApplicationListener<ContextClosedEvent> {
    private final MetadataService metadataService;
    private final RedisUtils metadataRedisUtils;
    private final JsonToStringConverter jsonToStringConverter;

    @Autowired
    public ShutdownEventListener(MetadataService metadataService,
                                 @Qualifier("metadataRedisUtils") RedisUtils metadataRedisUtils, JsonToStringConverter jsonToStringConverter) {
        this.metadataService = metadataService;
        this.metadataRedisUtils = metadataRedisUtils;
        this.jsonToStringConverter = jsonToStringConverter;
    }

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        log.info("Application Down, Metadata Update Start!");
        // Redis Metadata 목록 조회
        Set<String> list = metadataRedisUtils.getKeys();
        // 각 메타데이터 Mysql 갱신
        for(String s : list){
            log.info("Path : ["+ s + "] 업데이트 시작");
            Metadata metadata = jsonToStringConverter.jsontoClass(metadataRedisUtils.getRedisData(s), Metadata.class);
            metadataService.updateMetadataFromMySQL(metadata);
            log.info("Path : ["+ s + "] 업데이트 완료");
        }
        log.info("Metadata Update Finish!");
    }
}
