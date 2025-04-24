package org.fishking0721.oss.repository;

import org.fishking0721.oss.pojo.model.AudioMetadata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AudioMetadataRepository
        extends JpaRepository<AudioMetadata, Long>,
        JpaSpecificationExecutor<AudioMetadata> {

}
