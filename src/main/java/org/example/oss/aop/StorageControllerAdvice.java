package org.example.oss.aop;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.example.oss.exception.StorageException;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class StorageControllerAdvice {

    @Pointcut("execution(* org.example.oss.controller.StorageController.filteredPaginatedView(..))")
    public void storageControllerFilteredPaginatedViewPointcut() {}

    @Pointcut("execution(* org.example.oss.controller.StorageController.*(..))")
    public void storageControllerPointcut() {}

    @AfterThrowing(pointcut = "storageControllerFilteredPaginatedViewPointcut()", throwing = "tw")
    public void afterFilteredPaginatedViewThrowing(JoinPoint joinPoint, Throwable tw) {
        throw new StorageException("File not exist", tw);
    }

}
