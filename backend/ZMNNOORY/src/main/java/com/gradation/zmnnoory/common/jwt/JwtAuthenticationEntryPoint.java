package com.gradation.zmnnoory.common.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gradation.zmnnoory.common.dto.BaseResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

	private final ObjectMapper objectMapper;

	@Override
	public void commence(HttpServletRequest request,
	                     HttpServletResponse response,
	                     AuthenticationException authException
	) throws IOException, ServletException {
		log.error("Authentication Failed: URI = {}, message = {}", request.getRequestURI(), authException.getMessage());
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");

		BaseResponse<String> baseResponse = BaseResponse.<String>builder()
				.status(HttpStatus.UNAUTHORIZED)
				.data("Invalid username or password")
				.message(authException.getMessage())
				.build();

		ResponseEntity<BaseResponse<?>> responseEntity = ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(baseResponse);

		response.setStatus(HttpStatus.UNAUTHORIZED.value()); // 실제 HTTP 상태 코드 설정
		String responseBody = objectMapper.writeValueAsString(responseEntity);
		response.getWriter().write(responseBody);
	}
}
