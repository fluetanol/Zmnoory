package com.gradation.zmnnoory.common.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gradation.zmnnoory.common.dto.BaseResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;

@RequiredArgsConstructor
public class AccessDeniedHandlerImpl implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException accessDeniedException
    ) throws IOException, ServletException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        BaseResponse<String> baseResponse = BaseResponse.<String>builder()
                .status(HttpStatus.FORBIDDEN)
                .message("접근 권한이 없습니다.")  // 예외 메시지
                .data(null)
                .build();

        response.setStatus(HttpStatus.FORBIDDEN.value());
        ResponseEntity<BaseResponse<?>> responseEntity = ResponseEntity.status(HttpStatus.FORBIDDEN).body(baseResponse);
        String responseBody = objectMapper.writeValueAsString(responseEntity);
        response.getWriter().write(responseBody);
    }
}
