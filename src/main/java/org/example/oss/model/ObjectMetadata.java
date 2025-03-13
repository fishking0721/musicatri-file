package org.example.oss.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
//@Data
//@Table(name = "object_metadata")
public class ObjectMetadata {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 255, nullable = false) // 显式定义长度
    private String fileName;
    @Column(nullable = false, length = 500)
    private String filePath;
    @Column(name = "content_type", length = 100)
    private String contentType;
    @Column(name = "file_size")
    private Long fileSize;
    @Column(name = "upload_time", nullable = false)
    private LocalDateTime uploadTime;

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }
    public void setUploadTime(LocalDateTime uploadTime) {
        this.uploadTime = uploadTime;
    }
    public String getFilePath() {
        return filePath;
    }
    public Long getId() {
        return id;
    }
}
