package com.esprit.userservice.services;

import com.esprit.userservice.dto.AuthRequest;
import com.esprit.userservice.dto.AuthResponse;
import com.esprit.userservice.dto.RegisterRequest;
import com.esprit.userservice.entities.Role;
import com.esprit.userservice.entities.User;
import com.esprit.userservice.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * AuthService modifié : après l'enregistrement d'un nouveau user,
 * on publie un événement RabbitMQ via UserService.publishUserEvent()
 * pour que publication-service mette à jour son cache local.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository  userRepository;
    private final KeycloakService keycloakService;
    private final UserService     userService;   // ← injecté pour publishUserEvent()

    @Value("${keycloak.server-url}")
    private String keycloakServerUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.client-id}")
    private String clientId;

    @Value("${keycloak.client-secret}")
    private String clientSecret;

    // ── Login ─────────────────────────────────────────────────────────────────

    public AuthResponse login(AuthRequest request) {
        if (!keycloakService.userExistsInKeycloak(request.getEmail())) {
            throw new RuntimeException("Accès refusé : cet utilisateur n'existe pas dans Keycloak.");
        }

        String tokenEndpoint = keycloakServerUrl
                + "/realms/" + realm
                + "/protocol/openid-connect/token";

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type",    "password");
        body.add("client_id",     clientId);
        body.add("client_secret", clientSecret);
        body.add("username",      request.getEmail());
        body.add("password",      request.getPassword());
        body.add("scope",         "openid");

        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    tokenEndpoint, HttpMethod.POST,
                    new HttpEntity<>(body, headers), Map.class);

            Map<String, Object> tokenResponse = response.getBody();
            if (tokenResponse == null || !tokenResponse.containsKey("access_token")) {
                throw new RuntimeException("Réponse Keycloak invalide");
            }

            String accessToken  = (String) tokenResponse.get("access_token");
            String refreshToken = (String) tokenResponse.get("refresh_token");
            Object expiresObj   = tokenResponse.get("expires_in");
            Long   expiresIn    = expiresObj != null ? Long.valueOf(expiresObj.toString()) : 300L;

            User localUser = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new RuntimeException(
                            "Utilisateur authentifié dans Keycloak mais absent de la BDD locale."));

            log.info("Login réussi pour: {}", request.getEmail());
            return new AuthResponse(accessToken, localUser.getRole(), localUser.getId(), refreshToken, expiresIn);

        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                throw new RuntimeException("Identifiants incorrects.");
            }
            log.error("Erreur Keycloak lors du login: {}", e.getMessage());
            throw new RuntimeException("Erreur d'authentification : " + e.getMessage());
        }
    }

    // ── Refresh Token ─────────────────────────────────────────────────────────

    public AuthResponse refreshToken(String refreshToken) {
        String tokenEndpoint = keycloakServerUrl
                + "/realms/" + realm
                + "/protocol/openid-connect/token";

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type",    "refresh_token");
        body.add("client_id",     clientId);
        body.add("client_secret", clientSecret);
        body.add("refresh_token", refreshToken);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    tokenEndpoint, HttpMethod.POST,
                    new HttpEntity<>(body, headers), Map.class);

            Map<String, Object> tokenResponse = response.getBody();
            if (tokenResponse == null || !tokenResponse.containsKey("access_token")) {
                throw new RuntimeException("Refresh token invalide");
            }

            String newAccessToken  = (String) tokenResponse.get("access_token");
            String newRefreshToken = (String) tokenResponse.get("refresh_token");
            Object expiresObj      = tokenResponse.get("expires_in");
            Long   expiresIn       = expiresObj != null ? Long.valueOf(expiresObj.toString()) : 300L;

            return new AuthResponse(newAccessToken, null, null, newRefreshToken, expiresIn);

        } catch (HttpClientErrorException e) {
            throw new RuntimeException("Session expirée. Veuillez vous reconnecter.");
        }
    }

    // ── Register ──────────────────────────────────────────────────────────────

    /**
     * Enregistre un nouveau user et publie un événement RabbitMQ.
     * publication-service recevra le UserEventDTO et mettra son cache à jour.
     */
    public void register(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email déjà utilisé.");
        }

        Role role;
        try {
            role = Role.valueOf(request.getRole().toUpperCase());
        } catch (IllegalArgumentException e) {
            role = Role.CLIENT;
        }

        keycloakService.createUser(
                request.getEmail(),
                request.getPassword(),
                request.getName(),
                request.getLastName(),
                role.name()
        );

        User user = new User();
        user.setName(request.getName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPassword("[KEYCLOAK_MANAGED]");
        user.setBirthDate(request.getBirthDate());
        user.setEnabled(true);
        user.setRole(role);
        User savedUser = userRepository.save(user);

        log.info("Utilisateur enregistré : email={} role={}", request.getEmail(), role);

        // ← Nouveau : publier dans RabbitMQ pour alimenter le cache de publication-service
        userService.publishUserEvent(savedUser);
        log.info("[RabbitMQ] Événement publié pour le nouvel utilisateur id={}", savedUser.getId());
    }
}