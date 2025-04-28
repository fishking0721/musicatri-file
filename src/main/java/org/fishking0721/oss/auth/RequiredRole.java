package org.fishking0721.oss.auth;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
//@PreAuthorize("@permissionChecker.check(authentication, requiredRole)")
public @interface RequiredRole {
    String value();
}