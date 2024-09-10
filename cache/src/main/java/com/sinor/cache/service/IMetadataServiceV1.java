package com.sinor.cache.service;

import com.sinor.cache.model.MetadataGetResponse;
import org.springframework.data.domain.PageRequest;

import com.sinor.cache.global.exception.notuse.admin.AdminException;

import java.util.List;

public interface IMetadataServiceV1 {
	/**
	 * 옵션 조회 없으면 기본 10분 생성 후 반환
	 * @param path 조회할 옵션의 path
	 */
	MetadataGetResponse findOrCreateMetadataById(String path) throws AdminException;

	/**
	 * 캐시에 저장된 옵션 조회
	 * @param path 조회할 캐시의 path
	 * @return MetadataGetResponse By path Or null
	 */
	public MetadataGetResponse findMetadataCacheById(String path) throws AdminException;

	/**
	 * 옵션 조회 없으면 예외 발생
	 * @param path 조회할 옵션의 path
	 */
	MetadataGetResponse findMetadataById(String path) throws AdminException;

	/**
	 * 옵션 수정
	 * @param path 옵션 변경할 path 값
	 * @param newExpiredTime 새로 적용할 만료시간
	 */
	MetadataGetResponse updateMetadata(String path, Long newExpiredTime) throws AdminException;

	/**
	 * 옵션 삭제
	 *
	 * @param path 삭제할 path
	 */
	void deleteMetadataById(String path) throws AdminException;

	/**
	 * 옵션 생성
	 * @param path 생성할 path 값
	 * @param expiredTime 적용할 만료시간
	 */
	MetadataGetResponse createMetadata(String path, Long expiredTime) throws AdminException;

	MetadataGetResponse createMetadata(String path) throws AdminException;

	/**
	 * 옵션들의 목록을 조회한다. (10개씩 페이징)
	 * @param pageRequest 조회할 목록의 size, page 번호가 들어 있는 Paging 클래스
	 */
	List<MetadataGetResponse> findAllByPage(PageRequest pageRequest);

	/**
	 * 옵션이 있는 지 확인
	 * @param path 유무를 확인할 path 값
	 */
	Boolean isExistById(String path);
}
