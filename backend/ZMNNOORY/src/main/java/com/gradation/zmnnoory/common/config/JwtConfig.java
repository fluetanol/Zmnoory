package com.gradation.zmnnoory.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gradation.zmnnoory.common.jwt.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;

@Configuration
public class JwtConfig {

    @Bean
    public JwtLoginFilter jwtLoginFilter(ObjectMapper objectMapper, JwtProvider jwtProvider, AuthenticationManager authenticationManager) {
        return new JwtLoginFilter(objectMapper, jwtProvider, authenticationManager);
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
        return new JwtAuthenticationFilter(jwtUtil, userDetailsService);
    }

	@Bean
	public JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint(ObjectMapper objectMapper) {
		return new JwtAuthenticationEntryPoint(objectMapper);
	}

	@Bean
	public JwtAccessDeniedHandler jwtAccessDeniedHandler(ObjectMapper objectMapper) {
		return new JwtAccessDeniedHandler(objectMapper);
	}
}
