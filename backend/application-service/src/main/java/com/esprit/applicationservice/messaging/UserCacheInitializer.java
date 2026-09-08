package com.esprit.applicationservice.messaging;

import com.esprit.applicationservice.dto.UserEventDTO;
import com.esprit.applicationservice.services.UserCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserCacheInitializer implements CommandLineRunner {

    private final UserCacheService userCacheService;
    private static final String USER_SERVICE_URL = "http://localhost:8081/users/all-for-cache";

    @Override
    public void run(String... args) {
        log.info("[Cache] Initialisation du cache utilisateurs dans application-service...");
        try {
            RestTemplate restTemplate = new RestTemplate();
            UserEventDTO[] users = restTemplate.getForObject(USER_SERVICE_URL, UserEventDTO[].class);
            if (users != null && users.length > 0) {
                for (UserEventDTO u : users) userCacheService.put(u);
                log.info("[Cache] {} utilisateur(s) chargé(s) dans application-service.", users.length);
            } else {
                log.warn("[Cache] Aucun utilisateur retourné par user-service.");
            }
        } catch (Exception e) {
            log.warn("[Cache] user-service indisponible au démarrage : {}. " +
                    "Cache alimenté progressivement via RabbitMQ.", e.getMessage());
        }
    }
}