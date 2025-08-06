package com.gradation.zmnnoory.domain.member.annotation;

import org.springframework.security.access.prepost.PreAuthorize;

@PreAuthorize("hasRole('ADMIN')")
public @interface AdminOnly {
}
