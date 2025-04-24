package org.fishking0721.oss.pojo.dto;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AudioDownloadTaskCreateDTO {

    @Schema(description = "任务id", example = "1914988861454409728")
    private Long id;

    @Schema(description = "下载链接",
            example = "https://www.bilibili.com/video/BV1mnZAYuEox",
            defaultValue = "https://www.bilibili.com/video/BV1mnZAYuEox")
    private String url;

    @Schema(description = "资源类型", example = "bilibili:audio", defaultValue = "bilibili:audio")
    private String type;

    @Schema(description = "目标图片格式", example = "jpg", defaultValue = "jpg")
    private String thumbnailFormat = "jpg";

    @Schema(description = "目标音频格式", example = "m4a", defaultValue = "m4a")
    private String audioFormat = "m4a";

    @Schema(description = "任务创建者id")
    private String uploaderId;
}
