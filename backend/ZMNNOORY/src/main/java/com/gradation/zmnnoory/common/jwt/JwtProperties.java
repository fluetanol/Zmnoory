package com.gradation.zmnnoory.common.jwt;

import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

@Slf4j
@Getter
@AllArgsConstructor
@ConfigurationProperties("jwt")
public class JwtProperties {

    private final String secretKey;
    private final String issuer;
    private final Duration expirationTime;
    private final String tokenPrefix;
    private final String header;

    @PostConstruct
    public void log() {
        log.info(secretKey);
        log.info(header);
    }
}
