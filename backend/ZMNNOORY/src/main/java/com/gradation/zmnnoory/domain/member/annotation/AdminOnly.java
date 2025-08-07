package com.gradation.zmnnoory.domain.member.annotation;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.*;

@PreAuthorize("hasRole('ROLE_ADMIN')")
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AdminOnly {
}
