package com.esprit.eventservice.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

@Configuration
public class FeignConfig {

    @Bean
    public RequestInterceptor requestInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate template) {
                // Récupérer la requête HTTP actuelle
                ServletRequestAttributes attributes =
                        (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

                if (attributes != null) {
                    HttpServletRequest request = attributes.getRequest();
                    String authorization = request.getHeader("Authorization");

                    // Propager le token JWT
                    if (authorization != null && authorization.startsWith("Bearer ")) {
                        System.out.println("🔑 Propagating token to activity-service: " + authorization.substring(0, 50) + "...");
                        template.header("Authorization", authorization);
                    } else {
                        System.out.println("⚠️ No Authorization header found in current request");
                    }
                } else {
                    System.out.println("⚠️ No request context available");
                }
            }
        };
    }
}
