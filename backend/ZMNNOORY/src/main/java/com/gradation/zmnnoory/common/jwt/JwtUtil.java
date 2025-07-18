package com.gradation.zmnnoory.common.jwt;

import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtUtil {

    private final KeyProvider keyProvider;
    private final JwtProperties jwtProperties;

    public Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(keyProvider.getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String getMemberName(Claims claims) {
        return claims.getSubject();
    }

    public String getHeader() {
        return jwtProperties.getHeader();
    }

    public String getTokenPrefix() {
        return jwtProperties.getTokenPrefix();
    }

    public Long getExpiration() {
        return jwtProperties.getExpirationTime().toMillis();
    }
}
