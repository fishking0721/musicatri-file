package org.fishking0721.oss.aop;

import jakarta.annotation.Priority;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.fishking0721.oss.auth.PermissionChecker;
import org.fishking0721.oss.auth.RequiredRole;
import org.fishking0721.oss.exception.PermissionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;

@Aspect
@Component
@Priority(1) // 保证优先执行
public class RequiredRoleAspect {

    @Autowired
    private PermissionChecker permissionChecker;

    @Before("@annotation(org.fishking0721.oss.auth.RequiredRole)")
    public void checkRole(JoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        RequiredRole requiredRole = method.getAnnotation(RequiredRole.class);

        String requiredRoleValue = requiredRole.value(); // 取到 "user" 或 "admin"

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !permissionChecker.check(authentication, requiredRoleValue)) {
            throw new PermissionException("Access Denied: Missing required role");
        }
    }
}

