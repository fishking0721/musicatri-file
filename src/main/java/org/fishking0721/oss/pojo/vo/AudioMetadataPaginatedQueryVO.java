package org.fishking0721.oss.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.fishking0721.oss.pojo.model.AudioMetadata;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AudioMetadataPaginatedQueryVO {

    //    private String filePath;
    //    private String thumbnailPath;
    //    private String musicLength;

    private Long id;
    private String filename;
    private String artist;
    private Long filesize;
    private String uploaderId;
    private String sourceUrl;
    private String contentType;
    private LocalDateTime uploadTime;
    @Schema(description = "略缩图id")
    private Long thumbnailId;

    public static AudioMetadataPaginatedQueryVO of(final AudioMetadata metadata) {
        AudioMetadataPaginatedQueryVO vo = new AudioMetadataPaginatedQueryVO();
        BeanUtils.copyProperties(metadata, vo);

        if (metadata.getThumbnailMetadata() != null) {
            vo.setThumbnailId(metadata.getThumbnailMetadata().getId());  // 设置 thumbnailId
        }
        return vo;
    }
}
