package org.example.oss.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "object_metadata")
public class ObjectMetadata {
    @Id
    private Long id;

    @Column(length = 255, nullable = false) // 显式定义长度
    private String fileName;
    @Column(nullable = false, length = 500)
    private String filePath;
    private String thumbnailPath;
    private Long fileSize;
    private String musicLength;
    private String artist;
    private String uploaderId;
    private String sourceAddress;
    @Column(name = "content_type", length = 100)
    private String contentType;
    @Column(name = "upload_time", nullable = false)
    private LocalDateTime uploadTime;

}
