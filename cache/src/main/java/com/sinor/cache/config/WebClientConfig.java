package com.sinor.cache.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;

import io.netty.channel.ChannelOption;
import lombok.extern.slf4j.Slf4j;
import reactor.netty.http.client.HttpClient;


@Configuration
@Slf4j
/**
 * WebClient 빈 등록
 * WebClient 빈을 등록하면, WebClient.Builder 를 주입받아 사용할 수 있다.
 * 2023.12.7 ddongbu
 */
//TODO 해당 설정파일 인식이 안되는 오류
public class WebClientConfig {

	@Bean
	public WebClient webClient() {
		log.info("WebClient bean creation started");

		DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory();

		HttpClient httpClient = HttpClient.create()
				.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000); // 10초

		WebClient webClient = WebClient.builder()
				.uriBuilderFactory(factory)
				.baseUrl("http://mainHost:8080")
				.codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(2 * 1024 * 1024))
				.clientConnector(new ReactorClientHttpConnector(httpClient))
				.build();

		log.info("WebClient bean created successfully");

		return webClient;
	}
}

