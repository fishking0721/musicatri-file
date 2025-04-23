package org.fishking0721.oss.config;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.fishking0721.oss.pojo.model.AuditLogRecord;
import org.fishking0721.oss.pojo.model.ObjectMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;

@Aspect
@Component
@Slf4j
public class AuditLogAspect {

//    @Autowired(required = false)
//    private SecurityService securityService; // 自定义安全服务,等待discord鉴权服务完成

    @Autowired
    private HttpServletRequest request;
    @Autowired
    AuditLogQueue auditLogQueue;

    @Around("@annotation(auditLog)")
    public Object logAudit(ProceedingJoinPoint joinPoint, AuditLog auditLog) throws Throwable {
        long startTime = System.currentTimeMillis();
        Object result = null;
        boolean success = true;
        String errorReason = null;
        Object resourceId = null;

        try {
            result = joinPoint.proceed();
            // 获取资源ID
            if (result instanceof ObjectMetadata) {
                resourceId = ((ObjectMetadata) result).getId();
            }
            return result;
        } catch (Exception e) {
            success = false;
            errorReason = e.getClass().getSimpleName() + ": " + e.getMessage();
            throw e;
        } finally {
            long duration = System.currentTimeMillis() - startTime;

            // 构建审计日志对象
            AuditLogRecord logRecord = AuditLogRecord.builder()
                    .timestamp(Instant.now())
//                    .username(getCurrentUser())
                    .operation(auditLog.operation())
                    .resourceType(auditLog.resourceType())
                    .resourceId(resourceId)
                    .resourceName(getResourceName(joinPoint))
                    .clientIp(getClientIp())
                    .success(success)
                    .errorReason(errorReason)
                    .durationMs(duration)
                    .build();

            // 异步写入日志
            auditLogQueue.add(logRecord);
        }
    }

//    private String getCurrentUser() {
//        if (securityService != null) {
//            return securityService.getCurrentUsername();
//        }
//        return "anonymous";
//    }

    private String getClientIp() {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty()) {
            return request.getRemoteAddr();
        }
        return ip.split(",")[0];
    }

    private String getResourceName(ProceedingJoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
            if (arg instanceof MultipartFile) {
                return ((MultipartFile) arg).getOriginalFilename();
            }
        }
        return null;
    }
}
