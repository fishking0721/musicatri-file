package org.example.oss.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
//@Data
@Table(name = "object_metadata")
public class ObjectMetadata {
    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 255, nullable = false) // 显式定义长度
    private String fileName;
    @Column(nullable = false, length = 500)
    private String filePath;
    @Column(name = "file_size")
    private Long fileSize;
    private String musicLength;
    private String artist;
    private String uploaderId;
    private String sourceAddress;
    @Column(name = "content_type", length = 100)
    private String contentType;
    @Column(name = "upload_time", nullable = false)
    private LocalDateTime uploadTime;

    public String getSourceAddress() {
        return sourceAddress;
    }

    public void setSourceAddress(String sourceAddress) {
        this.sourceAddress = sourceAddress;
    }

    public String getMusicLength() {
        return musicLength;
    }

    public void setMusicLength(String musicLength) {
        this.musicLength = musicLength;
    }

    public String getUploaderId() {
        return uploaderId;
    }

    public void setUploaderId(String uploaderId) {
        this.uploaderId = uploaderId;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

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
    public void setId(Long id) {
        this.id = id;
    }
    public String getFileName() {
        return fileName;
    }
    public String getContentType() {
        return contentType;
    }
    public Long getFileSize() {
        return fileSize;
    }
    public LocalDateTime getUploadTime() {
        return uploadTime;
    }
}
