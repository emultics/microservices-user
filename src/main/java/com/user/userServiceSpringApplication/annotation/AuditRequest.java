package com.user.userServiceSpringApplication.annotation;

import com.user.userServiceSpringApplication.enums.AuditEventType;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AuditRequest {
}
