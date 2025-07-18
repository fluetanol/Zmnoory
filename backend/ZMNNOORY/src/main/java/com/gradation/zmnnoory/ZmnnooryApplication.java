package com.gradation.zmnnoory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@ConfigurationPropertiesScan
public class ZmnnooryApplication {

    public static void main(String[] args) {
        SpringApplication.run(ZmnnooryApplication.class, args);
    }

}
