package org.example.oss.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
//审计日志自定义注解
public @interface AuditLog {
    String operation();
    String resourceType() default "FILE";
}
