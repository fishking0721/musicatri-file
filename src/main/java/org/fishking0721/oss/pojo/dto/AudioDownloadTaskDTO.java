package org.fishking0721.oss.pojo.dto;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.fishking0721.oss.pojo.model.AudioDownloadTask;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AudioDownloadTaskDTO {

    @Schema(description = "任务id")
    private Long id;
    @Schema(description = "")
    private String url;
    private AudioDownloadTask.Status status; // PENDING, DOWNLOADING, COMPLETED, FAILED
    private LocalDateTime createdAt;
    private String errorMessage;

}
