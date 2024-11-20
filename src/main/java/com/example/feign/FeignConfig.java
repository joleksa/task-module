package com.example.feign;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Base64;

@Configuration
public class FeignConfig {

    @Bean
    public RequestInterceptor requestInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate requestTemplate) {
                String credentials = "admin:admin";
                String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());
                String authorizationHeader = "Basic " + encodedCredentials;

                requestTemplate.header("Authorization", authorizationHeader);
            }

        };
    }
}
