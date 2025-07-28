package com.gradation.zmnnoory.common.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.UUID;

@Slf4j
@Component
public class LoggingInterceptor implements HandlerInterceptor {

    private static final String REQUEST_ID = "REQUEST_ID";
    private static final String START_TIME = "START_TIME";

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler
    ) throws Exception {

        String requestId = UUID.randomUUID().toString();
        Long startTime = System.currentTimeMillis();

        request.setAttribute(REQUEST_ID, requestId);
        request.setAttribute(START_TIME, startTime);

        log.info("[{}] >>> {} {}", requestId, request.getMethod(), request.getRequestURI());

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response,
                                Object handler,
                                Exception ex
    ) throws Exception {
        String requestId = (String) request.getAttribute(REQUEST_ID);
        Long startTime = (Long) request.getAttribute(START_TIME);
        Long endTime = System.currentTimeMillis();
        Long duration = (startTime != null) ? endTime - startTime : -1;

        log.info("[{}] <<< {} {} ({} ms)", requestId, request.getMethod(), request.getRequestURI(), duration);
    }
}
