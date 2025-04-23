package org.fishking0721.oss.repository;

import org.fishking0721.oss.pojo.model.ObjectMetadata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ObjectMetadataRepository
        extends JpaRepository<ObjectMetadata, Long>,
        JpaSpecificationExecutor<ObjectMetadata> {

}
