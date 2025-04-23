package org.fishking0721.oss.pojo.vo;

import io.swagger.v3.oas.annotations.Parameter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 分页查询结果
 * @param <T>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PageResultVO<T> {

    @Parameter(description = "查询结果总数")
    private Long count;
    @Parameter(description = "查询结果实体列表")
    private List<T> rows;

}
