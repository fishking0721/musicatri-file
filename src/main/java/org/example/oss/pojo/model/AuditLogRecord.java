package org.example.oss.pojo.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
//数据传输对象DTO
public class AuditLogRecord {
    private Instant timestamp;     // 操作时间
    private String username;       // 用户标识
    private String operation;      // 操作类型（如FILE_UPLOAD）
    private String resourceType;   // 资源类型（如FILE）
    private Object resourceId;     // 资源ID（支持多种类型）
    private String resourceName;   // 资源名称（原始文件名）
    private String clientIp;       // 客户端IP
    private boolean success;       // 操作是否成功
    private String errorReason;    // 失败原因
    private long durationMs;       // 操作耗时（毫秒）
}
