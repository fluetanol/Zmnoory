package com.gradation.zmnnoory.common.config;

import com.gradation.zmnnoory.common.filter.JwtAuthenticationFilter;
import com.gradation.zmnnoory.common.filter.JwtLoginFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter, JwtLoginFilter jwtLoginFilter) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable); // csrf 무효화
        http.formLogin(AbstractHttpConfigurer::disable); // formLogin 무효화
        http.httpBasic(AbstractHttpConfigurer::disable); // basic 로그인 무효화
        http.cors(Customizer.withDefaults());
        http.headers(header -> header.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable)); // H2 콘솔 iframe 허용

        http
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        // 무상태 세션 사용 설정

        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/api-docs/**",
                                "/h2-console/**"
                        ).permitAll() // 해당 주소는 아무나 접근 가능
                        .requestMatchers(
                                "/api/members/sign-up"
                        ).anonymous() // 해당 주소는 로그인 안 한 사람만 접근 가능
                        .anyRequest()
                        .authenticated() // 그외 주소는 로그인 해야만 접근 가능
                );

        http.addFilterBefore(jwtAuthenticationFilter, JwtLoginFilter.class);
        http.addFilterAt(jwtLoginFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
