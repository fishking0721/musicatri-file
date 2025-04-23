package org.fishking0721.oss.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.fishking0721.oss.pojo.model.ObjectMetadata;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MetadataPaginatedQueryVO {
    private Long id;
    private String fileName;
//    private String filePath;
//    private String thumbnailPath;
    private Long fileSize;
//    private String musicLength;
    private String artist;
    private String uploaderId;
    private String sourceAddress;
    private String contentType;
    private LocalDateTime uploadTime;

    public static MetadataPaginatedQueryVO of(final ObjectMetadata metadata) {
        return MetadataPaginatedQueryVO.builder()
                .id(metadata.getId())
                .fileName(metadata.getFileName())
                .fileSize(metadata.getFileSize())
                .artist(metadata.getArtist())
                .uploaderId(metadata.getUploaderId())
                .sourceAddress(metadata.getSourceAddress())
                .contentType(metadata.getContentType())
                .uploadTime(metadata.getUploadTime())
                .build();
    }
}
