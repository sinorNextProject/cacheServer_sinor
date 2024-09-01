package com.sinor.cache.controller;

import com.sinor.cache.model.MetadataGetResponse;
import com.sinor.cache.service.IApiServiceV1;
import com.sinor.cache.service.IMetadataServiceV1;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.sinor.cache.common.ResponseStatus;
import com.sinor.cache.common.SuccessResponse;
import com.sinor.cache.utils.JsonToStringConverter;

@RestController
public class MetadataController implements IMetadataControllerV1 {

	private final IMetadataServiceV1 metadataService;
	private final IApiServiceV1 apiService;
	private final JsonToStringConverter jsonToStringConverter;

	@Autowired
	public MetadataController(IMetadataServiceV1 metadataService, IApiServiceV1 apiService,
		JsonToStringConverter jsonToStringConverter) {
		this.metadataService = metadataService;
		this.apiService = apiService;
		this.jsonToStringConverter = jsonToStringConverter;
	}

	/**
	 * @param path 조회할 옵션의 path
	 */
	@Override
	public ResponseEntity<SuccessResponse<?>> getMetadata(String path) {
		SuccessResponse<?> adminResponse = SuccessResponse.from(ResponseStatus.SUCCESS,
			metadataService.findMetadataById(path));
		return ResponseEntity.status(ResponseStatus.SUCCESS.getCode()).body(adminResponse);
	}

	/**
	 * Metadata 목록 조회, 10개 씩 Paging
	 *
	 * @param page 목록의 Page 번호
	 */
	@Override
	public ResponseEntity<SuccessResponse<?>> getMetadataAll(int page) {
		// 조회할 Metadata Page 설정 1 Page 당 데이터 10개
		PageRequest pageRequest = PageRequest.of(page, 10);
		SuccessResponse<?> adminResponse = SuccessResponse.from(ResponseStatus.SUCCESS,
			metadataService.findAllByPage(pageRequest));
		return ResponseEntity.status(ResponseStatus.SUCCESS.getCode()).body(adminResponse);
	}

	/**
	 * @param path 생성할 옵션의 path
	 */
	@Override
	public ResponseEntity<SuccessResponse<?>> createMetadata(String path) {
		SuccessResponse<?> adminResponse = SuccessResponse.from(ResponseStatus.SUCCESS,
			metadataService.createMetadata(path, 60 * 10L));
		return ResponseEntity.status(ResponseStatus.SUCCESS.getCode()).body(adminResponse);
	}

	/**
	 * @param path 수정할 옵션의 path
	 */
	@Override
	public ResponseEntity<SuccessResponse<?>> updateMetadata(String path, Long newExpiredTime) {
		// 캐시 수정
		MetadataGetResponse updatedMetadata = metadataService.updateMetadata(path, newExpiredTime);
		SuccessResponse<?> adminResponse = SuccessResponse.from(ResponseStatus.SUCCESS, updatedMetadata);
		// 수정된 Path URL 캐시 목록 삭제
		apiService.deleteCacheList(updatedMetadata.getMetadataUrl());

		return ResponseEntity.status(ResponseStatus.SUCCESS.getCode()).body(adminResponse);
	}

	/**
	 * @param path 삭제할 옵션의 path
	 */
	@Override
	public ResponseEntity<SuccessResponse<?>> deleteMetadata(String path) {
		metadataService.deleteMetadataById(path);
		SuccessResponse<?> adminResponse = SuccessResponse.fromNoData(ResponseStatus.SUCCESS);
		return ResponseEntity.status(ResponseStatus.SUCCESS.getCode()).body(adminResponse);
	}

	/**
	 * 해당 path의 옵션이 있는지 확인
	 *
	 * @param path 유무를 파악할 path 값
	 */
	@Override
	public ResponseEntity<SuccessResponse<?>> isExistMetadata(String path) {
		SuccessResponse<?> adminResponse = SuccessResponse.from(ResponseStatus.SUCCESS,
			metadataService.isExistById(path));
		return ResponseEntity.status(ResponseStatus.SUCCESS.getCode()).body(adminResponse);
	}
}
