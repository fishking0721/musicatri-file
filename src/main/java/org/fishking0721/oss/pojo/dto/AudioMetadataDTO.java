package org.fishking0721.oss.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AudioMetadataDTO {

    @Schema(description = "文件id，同时也是任务id", example = "1914988861454409728")
    private Long id;
    @Schema(description = "文件名，即音频文件名", example = "我是妈妈的小狗❤只爱妈妈一个人❤.m4a")
    private String filename;
    @Schema(description = "文件路径，即音频文件路径", example = ".\\storage\\1914988861454409728.m4a")
    private String filepath;
    @Schema(description = "文件大小，单位默认为byte", example = "1461257")
    private Long filesize;
    @Schema(description = "音频长度，即duration，单位默认为秒", example = "34.422")
    private String duration;

    @Schema(description = "曲作者", example = "永雏塔菲")
    private String artist;

    @Schema(description = "上传者id，或是任务提交者id")
    private String uploaderId;

    @Schema(description = "音频来源",
            example = "https://www.bilibili.com/video/BV1mnZAYuEox?t=0.4",
            defaultValue = "https://www.bilibili.com/video/BV1mnZAYuEox?t=0.4")
    private String sourceUrl;

    @Schema(description = "文件类型", example = "m4a", defaultValue = "m4a")
    private String contentType;
    @Schema(description = "文件上传时间", example = "2025-04-23 18:25:15.957109")
    private LocalDateTime uploadTime;

}
