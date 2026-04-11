package com.esprit.commentaireservice.messaging;

import com.esprit.commentaireservice.dto.UserEventDTO;
import com.esprit.commentaireservice.services.UserCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * Initialise le cache local des utilisateurs au démarrage de commentaire-service.
 *
 * Appelle GET /users/all-for-cache sur user-service (endpoint public, sans token)
 * pour charger tous les utilisateurs existants dans UserCacheService.
 * Ensuite, RabbitMQ prend le relais pour les nouveaux users et les mises à jour.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class UserCacheInitializer implements CommandLineRunner {

    private final UserCacheService userCacheService;

    private static final String USER_SERVICE_URL = "http://localhost:8081/users/all-for-cache";

    @Override
    public void run(String... args) {
        log.info("[Cache] Initialisation du cache utilisateurs depuis user-service...");
        try {
            RestTemplate restTemplate = new RestTemplate();
            UserEventDTO[] users = restTemplate.getForObject(USER_SERVICE_URL, UserEventDTO[].class);

            if (users != null && users.length > 0) {
                for (UserEventDTO u : users) {
                    userCacheService.put(u);
                }
                log.info("[Cache] {} utilisateur(s) chargé(s) avec succès au démarrage.", users.length);
            } else {
                log.warn("[Cache] Aucun utilisateur retourné par user-service.");
            }
        } catch (Exception e) {
            log.warn("[Cache] Impossible de contacter user-service au démarrage : {}. " +
                    "Le cache sera alimenté progressivement via RabbitMQ.", e.getMessage());
        }
    }
}