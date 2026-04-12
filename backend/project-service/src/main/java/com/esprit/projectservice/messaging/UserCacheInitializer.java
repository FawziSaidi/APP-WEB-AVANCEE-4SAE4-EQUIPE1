package com.esprit.projectservice.messaging;

import com.esprit.projectservice.dto.UserEventDTO;
import com.esprit.projectservice.services.UserCacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component @RequiredArgsConstructor @Slf4j
public class UserCacheInitializer implements CommandLineRunner {
    private final UserCacheService userCacheService;
    private static final String USER_SERVICE_URL = "http://localhost:8081/users/all-for-cache";

    @Override
    public void run(String... args) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            UserEventDTO[] users = restTemplate.getForObject(USER_SERVICE_URL, UserEventDTO[].class);
            if (users != null) {
                for (UserEventDTO u : users) userCacheService.put(u);
                log.info("[Cache] {} users chargés dans project-service.", users.length);
            }
        } catch (Exception e) {
            log.warn("[Cache] user-service indisponible au démarrage : {}", e.getMessage());
        }
    }
}