package com.esprit.microservice.adsservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.disable())
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/plans/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/campaigns/active").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/campaigns/{id}").permitAll()
                        .requestMatchers("/api/campaigns/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/campaigns/**").authenticated()
                        .anyRequest().permitAll()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
                );

        return http.build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter jwtConverter = new JwtAuthenticationConverter();
        jwtConverter.setJwtGrantedAuthoritiesConverter(jwt -> {
            var roles = new java.util.ArrayList<org.springframework.security.core.GrantedAuthority>();
            var realmAccess = jwt.getClaimAsMap("realm_access");
            if (realmAccess != null) {
                var realmRoles = (java.util.List<String>) realmAccess.get("roles");
                if (realmRoles != null) {
                    realmRoles.forEach(role ->
                        roles.add(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_" + role))
                    );
                }
            }
            return roles;
        });
        return jwtConverter;
    }
}
