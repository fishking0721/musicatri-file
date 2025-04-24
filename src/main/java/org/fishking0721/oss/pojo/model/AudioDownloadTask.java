package org.fishking0721.oss.pojo.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "audio_download_task")
public class AudioDownloadTask {
    @Id
    private Long id;
    private String url;
    @Enumerated(EnumType.STRING)
    private Status status; // PENDING, DOWNLOADING, COMPLETED, FAILED
    private LocalDateTime createdAt;
    private String errorMessage;

    public enum Status {
        PENDING,
        DOWNLOADING,
        COMPLETED,
        FAILED
    }
}
