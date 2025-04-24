package org.fishking0721.oss.service;

import org.fishking0721.oss.pojo.dto.AudioMetadataDTO;
import org.fishking0721.oss.pojo.dto.ThumbnailMetadataDTO;
import org.fishking0721.oss.pojo.model.AudioMetadata;
import org.fishking0721.oss.pojo.model.ThumbnailMetadata;
import org.fishking0721.oss.repository.AudioMetadataRepository;
import org.fishking0721.oss.repository.ThumbnailMetadataRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@SuppressWarnings("UnusedReturnValue")
public class AudioMetadataService {

    @Autowired
    private AudioMetadataRepository audioMetadataRepository;

    @Autowired
    private ThumbnailMetadataRepository thumbnailMetadataRepository;

    public boolean saveAudioMetadata(AudioMetadataDTO audioMetadataDTO,
                                     ThumbnailMetadataDTO thumbnailMetadataDTO) {
        ThumbnailMetadata thumbnailMetadata = new ThumbnailMetadata();
        BeanUtils.copyProperties(thumbnailMetadataDTO, thumbnailMetadata);
        thumbnailMetadataRepository.save(thumbnailMetadata);

        AudioMetadata audioMetadata = new AudioMetadata();
        BeanUtils.copyProperties(audioMetadataDTO, audioMetadata);
        audioMetadata.setThumbnailMetadata(thumbnailMetadata);
        audioMetadataRepository.save(audioMetadata);
        return true;
    }
}
