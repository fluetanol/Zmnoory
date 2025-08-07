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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

	private final ObjectMapper objectMapper;

	@Override
	public void handle(HttpServletRequest request,
	                   HttpServletResponse response,
	                   AccessDeniedException accessDeniedException
	) throws IOException, ServletException {
		log.error("Access Failed: URI = {}, message = {}", request.getRequestURI(), accessDeniedException.getMessage());
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");

		BaseResponse<String> baseResponse = BaseResponse.<String>builder()
				.status(HttpStatus.FORBIDDEN)
				.data("권한이 없습니다.")
				.message(accessDeniedException.getMessage())
				.build();

		ResponseEntity<BaseResponse<?>> responseEntity = ResponseEntity.status(HttpStatus.FORBIDDEN).body(baseResponse);

		response.setStatus(HttpStatus.FORBIDDEN.value()); // 실제 HTTP 상태 코드 설정
		String responseBody = objectMapper.writeValueAsString(responseEntity);
		response.getWriter().write(responseBody);
	}
}
