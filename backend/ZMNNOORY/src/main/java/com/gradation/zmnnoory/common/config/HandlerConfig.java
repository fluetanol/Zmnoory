package com.gradation.zmnnoory.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gradation.zmnnoory.common.handler.AccessDeniedHandlerImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.access.AccessDeniedHandler;

@Configuration
public class HandlerConfig {

    @Bean
    public AccessDeniedHandler accessDeniedHandler(ObjectMapper objectMapper) {
        return new AccessDeniedHandlerImpl(objectMapper);
    }
}
