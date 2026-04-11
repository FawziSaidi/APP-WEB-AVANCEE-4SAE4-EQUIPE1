package com.esprit.microservice.adsservice.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

@Slf4j
public class SecurityUtils {

    private SecurityUtils() {}

    public static Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            log.error("[SecurityUtils] No authenticated user in security context");
            return null;
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof Jwt jwt) {
            // Use the Keycloak sub claim hash as a numeric ID
            String sub = jwt.getSubject();
            if (sub != null) {
                return (long) Math.abs(sub.hashCode());
            }
        }

        log.error("[SecurityUtils] Could not extract user ID from JWT");
        return null;
    }

    public static String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof Jwt jwt) {
            String email = jwt.getClaimAsString("email");
            if (email != null) return email;
            return jwt.getClaimAsString("preferred_username");
        }

        return null;
    }
}
