package com.sinor.cache.controller;

import com.sinor.cache.global.exception.BaseException;
import com.sinor.cache.global.exception.BaseResponse;
import com.sinor.cache.global.exception.BaseStatus;
import com.sinor.cache.global.exception.DataResponse;
import com.sinor.cache.model.MetadataGetResponse;
import com.sinor.cache.service.MetadataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class MetadataController {

	private final MetadataService metadataService;

	@Autowired
	public MetadataController(MetadataService metadataService) {
		this.metadataService = metadataService;
	}

	/**
	 * @param path 조회할 옵션의 path
	 */
	@GetMapping("/admin/metadata")
	public ResponseEntity<DataResponse<?>> getMetadata(@RequestParam String path) {
		DataResponse<?> metadataResponse = DataResponse.from(BaseStatus.OK,
			metadataService.findMetadataById(path));
		return ResponseEntity.status(metadataResponse.getStatus()).body(metadataResponse);
	}

	/**
	 * @param path 생성할 옵션의 path
	 */
	@PostMapping("/admin/metadata")
	public ResponseEntity<DataResponse<?>> createMetadata(@RequestParam String path) {
		DataResponse<?> metadataResponse = DataResponse.from(BaseStatus.OK,
			metadataService.createMetadata(path, 60 * 10L));
		return ResponseEntity.status(metadataResponse.getStatus()).body(metadataResponse);
	}

	/**
	 * @param path 수정할 옵션의 path
	 */
	@PutMapping("/admin/metadata")
	public ResponseEntity<DataResponse<?>> updateMetadata(@RequestParam String path, @RequestParam Long newExpiredTime) {
		// 캐시 수정
		MetadataGetResponse updatedMetadata = metadataService.updateMetadata(path, newExpiredTime);
		DataResponse<?> metadataResponse = DataResponse.from(BaseStatus.OK, updatedMetadata);

		return ResponseEntity.status(metadataResponse.getStatus()).body(metadataResponse);
	}

	/**
	 * @param path 삭제할 옵션의 path
	 */
	@DeleteMapping("/admin/metadata")
	public ResponseEntity<BaseResponse> deleteMetadata(@RequestParam String path) {
		metadataService.deleteMetadataById(path);

		if(metadataService.isExistById(path))
			throw new BaseException(BaseStatus.INTERNAL_SERVER_ERROR, "해당 Metadata의 삭제가 이루어지지 않았습니다.");

		BaseResponse metadataResponse = BaseResponse.from(BaseStatus.OK);
		return ResponseEntity.status(metadataResponse.getStatus()).body(metadataResponse);
	}

	/**
	 * 해당 path의 옵션이 있는지 확인
	 * @param path 유무를 파악할 path 값
	 */
	@GetMapping("/admin/metadata/exist")
	public ResponseEntity<DataResponse<?>> isExistMetadata(@RequestParam String path) {
		DataResponse<?> metadataResponse = DataResponse.from(BaseStatus.OK,
			metadataService.isExistById(path));
		return ResponseEntity.status(metadataResponse.getStatus()).body(metadataResponse);
	}

	/**
	 * Mysql에 있는 Metadata 목록 조회, 10개 씩 Paging
	 * @param page 목록의 Page 번호
	 */
	// TODO 우선 Mysql에 접근하는 애만 만들어놓기
	@GetMapping("/admin/metadata/all")
	public ResponseEntity<DataResponse<?>> getMetadataAll(@RequestParam int page) {
		// 조회할 Metadata Page 설정 1 Page 당 데이터 10개
		PageRequest pageRequest = PageRequest.of(page, 10);
		DataResponse<?> metadataResponse = DataResponse.from(BaseStatus.OK,
				metadataService.findAllByPage(pageRequest));
		return ResponseEntity.status(metadataResponse.getStatus()).body(metadataResponse);
	}
}
