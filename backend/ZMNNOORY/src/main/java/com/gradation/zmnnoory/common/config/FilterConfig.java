package com.gradation.zmnnoory.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gradation.zmnnoory.common.filter.JwtAuthenticationEntryPoint;
import com.gradation.zmnnoory.common.filter.JwtAuthenticationFilter;
import com.gradation.zmnnoory.common.filter.JwtLoginFilter;
import com.gradation.zmnnoory.common.jwt.JwtProvider;
import com.gradation.zmnnoory.common.jwt.JwtUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;

@Configuration
public class FilterConfig {

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
}
