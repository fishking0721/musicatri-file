package org.fishking0721.oss.pojo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "audio_metadata")
public class AudioMetadata {
    @Id
    private Long id;
    @Column(name = "filename", nullable = false)
    private String filename;
    @Column(name = "filepath", nullable = false, length = 500)
    private String filepath;
    @Column(name = "filesize", nullable = false, length = 500)
    private Long filesize;

    private String duration;  // 时长
    private String artist;  // 作者

    @Column(name = "source_url")
    private String sourceUrl;  // 下载来源链接

    @Column(name = "content_type", length = 100)
    private String contentType;  // 音频文件类型

    @Column(name = "upload_time", nullable = false)
    private LocalDateTime uploadTime;  // 上传时间

    @Column(name = "uploader_id")
    private String uploaderId;  // 上传者id

    @ManyToOne
    @JoinColumn(name = "thumbnail_metadata_id")  // 外键字段
    private ThumbnailMetadata thumbnailMetadata;

}
