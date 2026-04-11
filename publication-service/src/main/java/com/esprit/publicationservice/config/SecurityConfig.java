package com.esprit.publicationservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Swagger, Actuator, uploads publics
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/actuator/**", "/uploads/**").permitAll()
                        // Toutes les routes publications sont ouvertes :
                        // la sécurité métier (userId, blocage) est gérée dans le service
                        .anyRequest().permitAll()
                );
        return http.build();
    }
}