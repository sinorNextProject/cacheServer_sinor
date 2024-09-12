package com.sinor.cache.service;

import com.sinor.cache.global.exception.BaseException;
import com.sinor.cache.global.exception.BaseStatus;
import com.sinor.cache.notuse.admin.AdminException;
import com.sinor.cache.model.Metadata;
import com.sinor.cache.model.MetadataGetResponse;
import com.sinor.cache.repository.MetadataRepository;
import com.sinor.cache.utils.JsonToStringConverter;
import com.sinor.cache.utils.RedisUtils;
import io.swagger.v3.core.util.Json;
import lombok.extern.slf4j.Slf4j;
import org.glassfish.jaxb.core.v2.TODO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.sinor.cache.notuse.admin.AdminResponseStatus.METADATA_NOT_FOUND;

/**
 * MetadataCacheInitializer에 의해 Mysql의 Metadata들이 Redis에 저장된다.
 * 이후 Service에서 이루어지는 CRUD는 Redis에 저장된 캐시를 다룬다.
 * ShutdownEventListener에 의해 프로그램 종료 시 Redis에 저장된 데이터를 Mysql에 저장하고 종료한다.
 * 1. 현재 작성되어 있는 Mysql을 직접적으로 건드리는 메소드는 제거하거나 특정 상황을 제외하면 사용을 자제
 * 2. Find : Redis에 저장된 특정 Path의 Metadata를 찾아 반환한다. 없다면 Excetpion
 * 3. Create : Redis에 특정 Path에 대한 Metadata를 생성한다. ( 기본 10분, 혹은 지정한 시간 -> Metadata의 ExpiredTime이 필요할까?)
 * 4. Update : Redis에 저장된 특정 Path에 대한 Metadata 값을 수정한다. (대부분 ExpiredTime밖에 없긴하다.)
 * 5. Delete : Delete에 대해서는 Redis뿐만이 아닌 Mysql에 있는 Path도 삭제하는 코드가 필요할 함
 */
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
	 * 옵션 캐시 조회
	 * @param path 조회할 캐시의 path
	 * @return metadata cache value OR null
	 */
	public MetadataGetResponse findMetadataById(String path) throws BaseException {
		// 캐시 검사
		if(!metadataRedisUtils.isExist(path))
			throw new BaseException(BaseStatus.NOT_FOUND, path + "에 대한 Metadata를 찾을 수 없습니다.");

		// 캐시 호출 및 역직렬화
		Metadata cacheMetadata = jsonToStringConverter.jsontoClass(metadataRedisUtils.getRedisData(path),
				Metadata.class);

		return MetadataGetResponse.from(cacheMetadata);
	}

	/**
	 * mysql 옵션 조회
	 * @param path 조회할 Metadata의 path
	 * @return metadata value OR null
	 */
	public MetadataGetResponse findMysqlMetadataById(String path) throws BaseException {
		// 캐시 검사
		Optional<Metadata> metadata = metadataRepository.findById(path);

		if(metadata.isEmpty())
			throw new BaseException(BaseStatus.NOT_FOUND, path + "에 대한 Metadata를 찾을 수 없습니다.");

		return MetadataGetResponse.from(metadata.get());
	}

	/**
	 * 옵션 수정
	 * @param path 옵션 변경할 path 값
	 * @param newExpiredTime 새로 적용할 만료시간
	 */
	public MetadataGetResponse updateMetadata(String path, Long newExpiredTime) throws BaseException {

		// 해당 url 유무 파악
		if(!metadataRedisUtils.isExist(path))
			throw new BaseException(BaseStatus.INTERNAL_SERVER_ERROR, path + "에 대한 metadata가 없습니다.");

		// Redis에서 Metadata 조회
		String data = metadataRedisUtils.getRedisData(path);
		
		// 역직렬화 및 수정된 객체 생성
		Metadata metadata = jsonToStringConverter.jsontoClass(data, Metadata.class);
		Metadata saveMetadata = Metadata.createValue(metadata.getMetadataUrl(), newExpiredTime);
		
		// 수정된 객체 저장
		metadataRedisUtils.setRedisData(path, jsonToStringConverter.objectToJson(saveMetadata));

		// response 반환
		return MetadataGetResponse.from(saveMetadata);
	}

	/**
	 * mysql 옵션 수정
	 * @param path 옵션 변경할 path 값
	 * @param newExpiredTime 새로 적용할 만료시간
	 */
	public MetadataGetResponse updateMysqlMetadata(String path, Long newExpiredTime) throws BaseException {

		// 해당 url 유무 파악
		if(!metadataRepository.existsById(path))
			throw new BaseException(BaseStatus.INTERNAL_SERVER_ERROR, path + "에 대한 metadata가 없습니다.");

		// Redis에서 Metadata 조회
		Optional<Metadata> metadata = metadataRepository.findById(path);

		// 역직렬화 및 수정된 객체 생성
		Metadata saveMetadata = Metadata.createValue(metadata.get().getMetadataUrl(), newExpiredTime);

		// 수정된 객체 저장
		metadataRepository.save(saveMetadata);

		// response 반환
		return MetadataGetResponse.from(saveMetadata);
	}

	/**
	 * 사용 자제 요망.
	 * ShutdownEvent용 수정 메소드.
	 * Mysql의 데이터를 직접 수정한다.
	 */
	public void updateMetadataFromMySQL(Metadata updateMetadata) throws BaseException{
		Metadata metadata = metadataRepository.save(updateMetadata);
		log.info(metadata.getMetadataUrl() + "수정");
	}

	/**
	 * Redis의 옵션 삭제
	 * @param path 삭제할 path
	 */
	public void deleteMetadataById(String path) throws BaseException {
		// 유무 파악
		if (!metadataRedisUtils.isExist(path))
			throw new BaseException(BaseStatus.INTERNAL_SERVER_ERROR, path + "에 대한 Metadata가 없어 삭제할 수 없습니다.");

		// 캐시 삭제
		metadataRedisUtils.deleteCache(path);
	}

	/**
	 * Mysql의 옵션 삭제
	 * @param path 삭제할 path
	 */
	public void deleteMysqlMetadataById(String path) throws BaseException {
		// 유무 파악
		if (!metadataRepository.existsById(path))
			throw new BaseException(BaseStatus.INTERNAL_SERVER_ERROR, path + "에 대한 Metadata가 없어 삭제할 수 없습니다.");

		// 캐시 삭제
		metadataRepository.deleteById(path);
	}

	/**
	 * 옵션 생성 expriedTime 지정 가능
	 * @param path 생성할 path 값
	 * @param expiredTime 적용할 만료시간
	 */
	public MetadataGetResponse createMetadata(String path, Long expiredTime) throws BaseException {
		// url 옵션이 이미 있는지 조회
		if (metadataRedisUtils.isExist(path))
			throw new BaseException(BaseStatus.INTERNAL_SERVER_ERROR, path + "에 대한 Metadata가 이미 있습니다.");

		// 옵션 생성
		Metadata metadata = Metadata.createValue(path, expiredTime);

		// 옵션 Redis 저장
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
		if (metadataRedisUtils.isExist(path))
			throw new BaseException(BaseStatus.INTERNAL_SERVER_ERROR, path + "에 대한 Metadata가 이미 있습니다.");

		// 옵션 생성
		Metadata metadata = Metadata.defaultValue(path);

		// 옵션 Redis 저장
		metadataRedisUtils.setRedisData(path, jsonToStringConverter.objectToJson(metadata));

		// response 반환
		return MetadataGetResponse.from(metadata);
	}

	/**
	 * 옵션 생성 default Value를 활용
	 * @param path 생성하려는 metadata의 URL Path
	 */
	public MetadataGetResponse createMysqlMetadata(String path) throws BaseException {
		// url 옵션이 이미 있는지 조회
		if (metadataRepository.existsById(path)) {
			throw new BaseException(BaseStatus.INTERNAL_SERVER_ERROR, path + "에 대한 Metadata가 이미 있습니다.");
		}

		// 옵션 생성
		Metadata metadata = Metadata.defaultValue(path);

		// 옵션 Redis 저장
		metadataRepository.save(metadata);

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
	 * 옵션이 있는 지 확인
	 * @param path 유무를 확인할 path 값
	 */
	public Boolean isExistById(String path) {
		return metadataRedisUtils.isExist(path);
	}

	/**
	 * page 상관없이 Metadata 전체를 조회하는 메소드
	 * 초기 세팅 이외의 사용 비권장
	 */
	public List<Metadata> findAll() {
		return metadataRepository.findAll();
	}
}
