package com.gradation.zmnnoory.common.jwt;

import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtProvider {

    private final KeyProvider keyProvider;
    private final JwtUtil jwtUtil;

    public String createToken(UserDetails userDetails, Date now) {
        Date expirationDate = new Date(now.getTime() + jwtUtil.getExpiration());
        return Jwts.builder()
                .subject(userDetails.getUsername())
                .issuedAt(now)
                .expiration(expirationDate)
                .signWith(keyProvider.getSecretKey())
                .compact();
    }

}
