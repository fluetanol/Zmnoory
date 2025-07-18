package com.gradation.zmnnoory.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable); // scrf 무효화
        http.formLogin(AbstractHttpConfigurer::disable); // formLogin 무효화
        http.httpBasic(AbstractHttpConfigurer::disable); // basic 로그인 무효화

        http
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        // 무상태 세션 사용 설정

        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/"
                        ).permitAll()
                        .anyRequest().authenticated()
                );

        return http.build();
    }

}
