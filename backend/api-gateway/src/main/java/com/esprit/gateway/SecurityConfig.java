package com.esprit.gateway;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity serverHttpSecurity) {
        return serverHttpSecurity
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                // ✅ CORS géré par Spring Security en premier
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeExchange(exchange -> exchange
                        .pathMatchers("/eureka/**").permitAll()
                        .pathMatchers("/api/auth/login").permitAll()
                        .pathMatchers("/api/auth/register").permitAll()
                        // ✅ Autoriser toutes les requêtes OPTIONS (preflight CORS)
                        .pathMatchers(org.springframework.http.HttpMethod.OPTIONS, "/**").permitAll()
                        .pathMatchers(
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/webjars/**"
                        ).permitAll()
                        // ✅ Forum : publications, commentaires, réactions accessibles sans JWT
                        .pathMatchers("/api/publications/**").permitAll()
                        .pathMatchers("/api/commentaires/**").permitAll()
                        .pathMatchers("/api/reactions/**").permitAll()
                        .pathMatchers("/uploads/**").permitAll()
                        .pathMatchers("/api/plans/**").permitAll()
                        .pathMatchers("/api/transactions/**").permitAll()
                        .pathMatchers("/api/campaigns/active").permitAll()
                        .anyExchange().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(Customizer.withDefaults())
                )
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:4200"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        config.setExposedHeaders(List.of("Authorization", "Content-Type"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
