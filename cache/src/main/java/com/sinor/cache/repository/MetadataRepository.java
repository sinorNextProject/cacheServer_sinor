package com.sinor.cache.repository;

import com.sinor.cache.model.Metadata;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MetadataRepository extends JpaRepository<Metadata, String> {

}
