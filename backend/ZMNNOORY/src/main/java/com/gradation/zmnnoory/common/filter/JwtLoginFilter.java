package com.gradation.zmnnoory.common.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gradation.zmnnoory.common.dto.BaseResponse;
import com.gradation.zmnnoory.common.jwt.JwtProvider;
import com.gradation.zmnnoory.domain.member.dto.request.LoginRequest;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Date;

@Slf4j
public class JwtLoginFilter extends UsernamePasswordAuthenticationFilter {

    private final ObjectMapper objectMapper;
    private final JwtProvider jwtProvider;

    public JwtLoginFilter(ObjectMapper objectMapper,
                          JwtProvider jwtProvider,
                          AuthenticationManager authenticationManager) {
        super.setAuthenticationManager(authenticationManager);
        this.objectMapper = objectMapper;
        this.jwtProvider = jwtProvider;
        setFilterProcessesUrl("/api/member/login");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response
    ) throws AuthenticationException {
        try {
            LoginRequest loginRequest = objectMapper.readValue(request.getInputStream(), LoginRequest.class);
            UsernamePasswordAuthenticationToken authRequest =
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword());

            return this.getAuthenticationManager().authenticate(authRequest);
        } catch (Exception e) {
            throw new AuthenticationServiceException("Invalid username or password", e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult
    ) throws IOException, ServletException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        UserDetails userDetails = (UserDetails) authResult.getPrincipal();
        String token = jwtProvider.createToken(userDetails, new Date()); // 이메일
        BaseResponse<String> baseResponse = BaseResponse.<String>builder()
                .status(HttpStatus.ACCEPTED)
                .data(token)
                .build();

        response.setStatus(HttpStatus.ACCEPTED.value()); // 실제 HTTP 상태 코드 설정
        ResponseEntity<BaseResponse<?>> responseEntity = ResponseEntity.accepted().body(baseResponse);
        String responseBody = objectMapper.writeValueAsString(responseEntity);
        response.getWriter().write(responseBody);
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request,
                                              HttpServletResponse response,
                                              AuthenticationException failed
    ) throws IOException, ServletException {
        log.error("Login failed: {}", failed.getMessage());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        BaseResponse<String> baseResponse = BaseResponse.<String>builder()
                .status(HttpStatus.UNAUTHORIZED)
                .data("Invalid username or password")
                .build();

        ResponseEntity<BaseResponse<?>> responseEntity = ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(baseResponse);

        response.setStatus(HttpStatus.UNAUTHORIZED.value()); // 실제 HTTP 상태 코드 설정
        String responseBody = objectMapper.writeValueAsString(responseEntity);
        response.getWriter().write(responseBody);
    }
}
