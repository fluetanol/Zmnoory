package com.gradation.zmnnoory.common.config;

import com.gradation.zmnnoory.common.filter.JwtAuthenticationFilter;
import com.gradation.zmnnoory.common.filter.JwtLoginFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            JwtAuthenticationFilter jwtAuthenticationFilter,
            JwtLoginFilter jwtLoginFilter) throws Exception {
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
                                "/",
                                "/h2-console/**",
                                "/actuator/health",
                                "/actuator/health/**",
                                "/actuator/info",
                                "/actuator/metrics",
                                "/actuator/prometheus",
                                "/swagger-ui/**",
                                "/api-docs/**",
                                "/api/members/check-email/**",
                                "/api/members/check-nickname/**"
                        ).permitAll() // 해당 주소는 아무나 접근 가능
                        .requestMatchers(
                                "/api/members/sign-up"
                        ).anonymous() // 해당 주소는 로그인 안 한 사람만 접근 가능
                        .requestMatchers(
                                "/api/members/admin/**",
                                "/api/games/admin/**",
                                "/api/rewards/admin/**",
                                "/api/participations/admin/**",
                                "/api/products/admin/**"
                        )
                        .hasRole("ADMIN")
                        .requestMatchers(
                                "/api/members/**",
                                "/api/games/**",
                                "/api/rewards/**",
                                "/api/participations/**",
                                "/api/products/**",
                                "/api/videos/**"
                        ).authenticated()
                        .anyRequest()
                        .denyAll() // 그외 주소는 로그인 해야만 접근 가능
                );
        http.addFilterBefore(jwtAuthenticationFilter, JwtLoginFilter.class);
        http.addFilterAt(jwtLoginFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public RoleHierarchy roleHierarchy() {
        return RoleHierarchyImpl.fromHierarchy("ROLE_ADMIN > ROLE_USER");
    }
}
