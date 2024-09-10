package com.sinor.cache.service;

import com.sinor.cache.global.exception.BaseException;
import com.sinor.cache.global.exception.BaseStatus;
import com.sinor.cache.notuse.admin.AdminException;
import com.sinor.cache.model.Metadata;
import com.sinor.cache.model.MetadataGetResponse;
import com.sinor.cache.repository.MetadataRepository;
import com.sinor.cache.utils.JsonToStringConverter;
import com.sinor.cache.utils.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.sinor.cache.notuse.admin.AdminResponseStatus.METADATA_NOT_FOUND;

@Slf4j
@Service
@Transactional
public class MetadataService {
	private final MetadataRepository metadataRepository;
	private final JsonToStringConverter jsonToStringConverter;
	private final RedisUtils metadataRedisUtils;

	@Autowired
	public MetadataService(MetadataRepository optionRepository, JsonToStringConverter jsonToStringConverter,
						   @Qualifier("metadataRedisUtils") RedisUtils metadataRedisUtils) {
		this.metadataRepository = optionRepository;
		this.jsonToStringConverter = jsonToStringConverter;
		this.metadataRedisUtils = metadataRedisUtils;
	}

	/**
	 * 옵션 조회 없으면 기본 10분 생성 후 반환
	 * @param path 조회할 옵션의 path
	 */
	public MetadataGetResponse findOrCreateMetadataById(String path) throws BaseException {
		// 캐시 검사
		MetadataGetResponse metadataGetResponse = findMetadataCacheById(path);

		if (metadataGetResponse != null)
			return metadataGetResponse;

		// 옵션 조회, 없으면 기본 10분으로 Metadata 생성
		Optional<Metadata> metadata = metadataRepository.findById(path);

		if (metadata.isEmpty())
			return createMetadata(path);

		// response 반환
		return MetadataGetResponse.from(metadata.get());
	}

	/**
	 * 옵션 캐시 조회
	 * @param path 조회할 캐시의 path
	 * @return metadata cache value OR null
	 */
	public MetadataGetResponse findMetadataCacheById(String path) throws BaseException {
		// 캐시 검사
		if(!metadataRedisUtils.isExist(path))
			throw new BaseException(BaseStatus.NOT_FOUND, path + "에 대한 Metadata를 찾을 수 없습니다.");

		// 캐시 호출 및 역직렬화
		Metadata cacheMetadata = jsonToStringConverter.jsontoClass(metadataRedisUtils.getRedisData(path),
				Metadata.class);

		return MetadataGetResponse.from(cacheMetadata);
	}

	/**
	 * 옵션 조회 없으면 예외 발생
	 * @param path 조회할 옵션의 path
	 */
	public MetadataGetResponse findMetadataById(String path) throws BaseException {
		// 캐시 검사
		MetadataGetResponse metadataGetResponse = findMetadataCacheById(path);

		if (metadataGetResponse != null)
			return metadataGetResponse;

		// 옵션 조회
		long startTime = System.currentTimeMillis();
		System.out.println("조회 시작");
		Optional<Metadata> metadata = metadataRepository.findById(path);
		long endTime = System.currentTimeMillis();
		System.out.println("조회 종료 : " + (endTime - startTime) + "밀리초");

		if (metadata.isEmpty())
			throw new AdminException(METADATA_NOT_FOUND);

		// response 반환
		return MetadataGetResponse.from(metadata.get());
	}

	/**
	 * 옵션 수정
	 * @param path 옵션 변경할 path 값
	 * @param newExpiredTime 새로 적용할 만료시간
	 */
	public MetadataGetResponse updateMetadata(String path, Long newExpiredTime) throws BaseException {

		// 해당 url 유무 파악
		long startTime = System.currentTimeMillis();
		Optional<Metadata> metadata = metadataRepository.findById(path);
		long endTime = System.currentTimeMillis();
		System.out.println("조회 종료 : " + (endTime - startTime) + "밀리초");

		if (metadata.isEmpty())
			throw new BaseException(BaseStatus.INTERNAL_SERVER_ERROR, path + "에 대한 metadata가 없습니다.");

		// 변경 값으로 저장
		Metadata saveMetadata = metadataRepository.save(
			Metadata.createValue(metadata.get().getMetadataUrl(), newExpiredTime)
		);
		metadataRedisUtils.setRedisData(path, jsonToStringConverter.objectToJson(saveMetadata));

		// 활성 캐시 목록 초기화
		/*ArrayList<String> list = jsonToStringConverter.jsontoClass(cacheListRedisUtils.getRedisData(path), ArrayList.class);
		list.clear();
		cacheListRedisUtils.setRedisData(path, jsonToStringConverter.objectToJson(list));*/

		// response 반환
		return MetadataGetResponse.from(saveMetadata);
	}

	/**
	 * 옵션 삭제
	 * @param path 삭제할 path
	 */
	public void deleteMetadataById(String path) throws BaseException {
		// 유무 파악
		if (!metadataRepository.existsById(path))
			throw new BaseException(BaseStatus.INTERNAL_SERVER_ERROR, path + "에 대한 Metadata가 없어 삭제할 수 없습니다.");

		// 캐시 삭제
		metadataRepository.deleteById(path);
		metadataRedisUtils.deleteCache(path);
	}

	/**
	 * 옵션 생성 expriedTime 지정 가능
	 * @param path 생성할 path 값
	 * @param expiredTime 적용할 만료시간
	 */
	public MetadataGetResponse createMetadata(String path, Long expiredTime) throws BaseException {
		// url 옵션이 이미 있는지 조회
		if (metadataRepository.existsById(path))
			throw new BaseException(BaseStatus.INTERNAL_SERVER_ERROR, path + "에 대한 Metadata가 이미 있습니다.");

		// 옵션 생성
		Metadata metadata = metadataRepository.save(
			Metadata.createValue(path, expiredTime)
		);

		metadataRedisUtils.setRedisData(path, jsonToStringConverter.objectToJson(metadata));
		// response 반환
		return MetadataGetResponse.from(metadata);
	}

	/**
	 * 옵션 생성 default Value를 활용
	 * @param path 생성하려는 metadata의 URL Path
	 */
	public MetadataGetResponse createMetadata(String path) throws BaseException {
		// url 옵션이 이미 있는지 조회
		if (metadataRepository.existsById(path))
			throw new BaseException(BaseStatus.INTERNAL_SERVER_ERROR, path + "에 대한 옵션 값이 있습니다.");

		// 옵션 생성
		Metadata metadata = metadataRepository.save(
			Metadata.defaultValue(path)
		);

		// 옵션 Redis 저장
		metadataRedisUtils.setRedisData(path,
				jsonToStringConverter.objectToJson(metadata), metadata.getMetadataTtlSecond());

		// response 반환
		return MetadataGetResponse.from(metadata);
	}

	/**
	 * 옵션들의 목록을 조회한다. (10개씩 페이징)
	 * @param pageRequest 조회할 목록의 size, page 번호가 들어 있는 Paging 클래스
	 */
	public List<MetadataGetResponse> findAllByPage(PageRequest pageRequest) {
		return metadataRepository.findAll(pageRequest).stream().map(MetadataGetResponse::from).toList();
	}

	/**
	 * page 상관없이 Metadata 전체를 조회하는 메소드
	 * 초기 세팅 이외의 사용 비권장
	 */
	public List<Metadata> findAll() {
		return metadataRepository.findAll();
	}

	/**
	 * 옵션이 있는 지 확인
	 * @param path 유무를 확인할 path 값
	 */
	public Boolean isExistById(String path) {
		if (metadataRedisUtils.isExist(path))
			return true;

		return metadataRepository.existsById(path);
	}
}
