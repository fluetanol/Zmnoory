package com.gradation.zmnnoory.common.filter;

import com.gradation.zmnnoory.common.jwt.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader(jwtUtil.getHeader());
        String token = null;

        if (authHeader != null && authHeader.startsWith(jwtUtil.getTokenPrefix())) {
            token = authHeader.substring(jwtUtil.getTokenPrefix().length()).trim();
        }

        if (token != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                Claims claims = jwtUtil.getClaims(token);
                // 유효하지 않은 토큰이면 exception 던진다
                // runtimeException 이어서 안 보이는 상황임
                String email = jwtUtil.getMemberName(claims); // 이메일을 멤버 이름으로 사용한다.
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                SecurityContextHolder
                        .getContext()
                        .setAuthentication(new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities()
                        )); // credential은 null로 넣는게 안전하다

            } catch (Exception e) {
                log.error("Failed to authenticate JWT token", e);
	            SecurityContextHolder.clearContext();
				throw new InsufficientAuthenticationException("Invalid JWT token", e);
            }
        }

        filterChain.doFilter(request, response);
        // 토큰이 없거나 유효하지 않아도 다음 필터로 넘어감

    }
}
