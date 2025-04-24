package org.fishking0721.oss.pojo.dto;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 元数据分页查询传递实体
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AudioMetadataPaginatedQueryDTO {

    @Schema(description = "当前页码，默认从1开始", example = "1")
    private int page = 1;

    @Schema(description = "每页显示条目数", example = "10")
    private int size = 10;

    @Schema(description = "曲作者，对于例如163文件可以通过文件元数据读取获得")
    private String artist;

    @Schema(description = "文件类型", example = "m4a")
    private String contentType;

    @Schema(description = "文件名", example = "哈基米FM之泰拉瑞亚的小曲.m4a")
    private String filename;

}
