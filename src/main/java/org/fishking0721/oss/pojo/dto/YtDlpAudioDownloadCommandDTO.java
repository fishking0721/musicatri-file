package org.fishking0721.oss.pojo.dto;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.nio.file.Path;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "yt-dlp下载命令参数封装", name = "yt-dlp命令行参数实体类")
public class YtDlpAudioDownloadCommandDTO {

    @Schema(description = "资源下载链接")
    private String url;

    @Builder.Default
    @Schema(description = "音频目标格式", example = "m4a")
    private String audioFormat = "m4a";

    @Builder.Default
    @Schema(description = "目标音频质量（0为最佳音质，表示320kbps）", example = "0")
    private String audioQuality = "0";

    @Builder.Default
    @Schema(description = "目标封面略缩图格式", example = "jpg")
    private String convertThumbnails = "jpg";

    @Builder.Default
    @Schema(description = "是否嵌入封面", example = "true")
    private boolean embedThumbnail = true;

    @Builder.Default
    @Schema(description = "是否单纯存储封面", example = "true")
    private boolean writeThumbnail = true;

    @Builder.Default
    @Schema(description = "是否嵌入标题、艺术家等元数据")
    private boolean embedMetadata = true;

    @Schema(description = "输出文件路径")
    private Path outputPath;

    @Builder.Default
    @Schema(description = "是否将结果输出为JSON格式到控制台", example = "false")
    private boolean printJson = false;

}
