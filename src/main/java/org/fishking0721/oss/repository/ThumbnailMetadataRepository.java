package org.fishking0721.oss.repository;

import org.fishking0721.oss.pojo.model.ThumbnailMetadata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ThumbnailMetadataRepository
        extends JpaRepository<ThumbnailMetadata, Long>,
        JpaSpecificationExecutor<ThumbnailMetadata> {
}
