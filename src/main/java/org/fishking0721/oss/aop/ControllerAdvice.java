package org.fishking0721.oss.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.fishking0721.oss.exception.DownloadException;
import org.fishking0721.oss.exception.StorageException;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class ControllerAdvice {

    @Pointcut("execution(* org.fishking0721.oss.controller.StorageController.filteredPaginatedView(..))")
    public void storageControllerFilteredPaginatedViewPointcut() {}

    @Pointcut("execution(* org.fishking0721.oss.controller.StorageController.*(..))")
    public void storageControllerPointcut() {}

    @Pointcut("execution(* org.fishking0721.oss.controller.DownloadController.*(..))")
    public void downloadControllerPointcut() {}

    @AfterThrowing(pointcut = "storageControllerPointcut()", throwing = "tw")
    public void afterStorageFiltered(JoinPoint joinPoint, Throwable tw) {
        throw new StorageException("File not exist", tw);
    }

    @AfterThrowing(pointcut = "downloadControllerPointcut()", throwing = "tw")
    public void afterDownloadFiltered(JoinPoint joinPoint, Throwable tw) {
        throw new DownloadException("Failed to create download task", tw);
    }

}
