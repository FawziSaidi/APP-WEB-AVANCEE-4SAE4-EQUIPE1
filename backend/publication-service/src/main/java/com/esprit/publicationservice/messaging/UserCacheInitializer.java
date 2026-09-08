package com.esprit.publicationservice.messaging;

import com.esprit.publicationservice.dto.UserEventDTO;
import com.esprit.publicationservice.services.UserCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * Initialise le cache local des utilisateurs au démarrage de publication-service.
 *
 * Problème résolu :
 *   Les utilisateurs déjà existants en base de données n'ont jamais publié
 *   d'événement RabbitMQ → le cache UserCacheService est vide au démarrage
 *   → enrichWithUser() ne trouve rien → affiche "User #3" au lieu du vrai nom.
 *
 * Solution :
 *   Au démarrage, on appelle GET /users/all-for-cache sur user-service
 *   (endpoint public, pas de token requis) pour charger TOUS les utilisateurs
 *   existants dans le cache. Ensuite, RabbitMQ prend le relais pour les
 *   nouveaux utilisateurs et les mises à jour.
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
            // Ne pas bloquer le démarrage si user-service est indisponible
            // RabbitMQ prendra le relais dès que les users se connecteront
            log.warn("[Cache] Impossible de contacter user-service au démarrage : {}. " +
                    "Le cache sera alimenté progressivement via RabbitMQ.", e.getMessage());
        }
    }
}