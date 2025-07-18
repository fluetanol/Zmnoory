package com.gradation.zmnnoory.common.jwt;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@Getter
@AllArgsConstructor
@ConfigurationProperties("jwt")
public class JwtProperties {

    private final String secretKey;
    private final String issuer;
    private final Duration expirationTime;
    private final String tokenPrefix;
    private final String header;
}
