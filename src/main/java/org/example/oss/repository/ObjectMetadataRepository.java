package org.example.oss.repository;

import org.example.oss.pojo.model.ObjectMetadata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ObjectMetadataRepository
        extends JpaRepository<ObjectMetadata, Long>,
        JpaSpecificationExecutor<ObjectMetadata> {

}
