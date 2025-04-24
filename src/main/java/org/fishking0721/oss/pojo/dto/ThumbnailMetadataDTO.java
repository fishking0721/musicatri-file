package org.fishking0721.oss.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ThumbnailMetadataDTO {

    @Schema(description = "略缩图id")
    private Long id;
    @Schema(description = "略缩图文件名")
    private String filename;  //
    @Schema(description = "略缩图所在路径")
    private String filepath;  //
    @Schema(description = "略缩图大小")
    private Long filesize;  //

}
