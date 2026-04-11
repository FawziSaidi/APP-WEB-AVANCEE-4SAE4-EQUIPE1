package com.esprit.commentaireservice.services;

import com.esprit.commentaireservice.dto.UserEventDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Cache local des utilisateurs dans commentaire-service.
 *
 * Alimenté par RabbitMQ (UserConsumer) à chaque événement user-service.
 * Initialisé au démarrage par UserCacheInitializer via GET /users/all-for-cache.
 *
 * Remplace les appels Feign userClient.getUserById() dans enrichWithUser().
 */
@Service
public class UserCacheService {

    private static final Logger log = LoggerFactory.getLogger(UserCacheService.class);

    private final Map<Integer, UserEventDTO> cache = new ConcurrentHashMap<>();

    public void put(UserEventDTO dto) {
        cache.put(dto.getId(), dto);
        log.info("[UserCache] Mis à jour : userId={} ({} {})",
                dto.getId(), dto.getName(), dto.getLastName());
    }

    public Optional<UserEventDTO> get(Integer userId) {
        return Optional.ofNullable(cache.get(userId));
    }

    public int size() {
        return cache.size();
    }
}