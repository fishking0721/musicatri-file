package org.fishking0721.oss.pojo.vo;

import io.swagger.v3.oas.annotations.Parameter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.fishking0721.oss.pojo.model.AudioDownloadTask;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AudioDownloadTaskVO {
    @Parameter(description = "任务id")
    private Long id;
    @Parameter(description = "任务下载url")
    private String url;
    @Parameter(description = "任务当前状态")
    private AudioDownloadTask.Status status; // PENDING, DOWNLOADING, COMPLETED, FAILED
    private LocalDateTime createdAt;
    private String errorMessage;

    public static AudioDownloadTaskVO of(AudioDownloadTask entity) {
        AudioDownloadTaskVO vo = new AudioDownloadTaskVO();
        BeanUtils.copyProperties(entity, vo);
        return vo;
    }
}
