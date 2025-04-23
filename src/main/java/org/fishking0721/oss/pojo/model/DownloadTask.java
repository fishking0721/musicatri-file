package org.fishking0721.oss.pojo.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "download_task")
public class DownloadTask {
    @Id
    private Long id;

    private String url;
    private String status; // PENDING, DOWNLOADING, COMPLETED, FAILED
    private LocalDateTime createdAt;
    private String errorMessage;

}
