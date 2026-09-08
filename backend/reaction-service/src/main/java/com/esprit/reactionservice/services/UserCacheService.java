package com.esprit.reactionservice.services;

import com.esprit.reactionservice.dto.UserEventDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Cache local des utilisateurs dans reaction-service.
 *
 * Alimenté par RabbitMQ (UserConsumer) à chaque événement user-service.
 * Initialisé au démarrage par UserCacheInitializer via GET /users/all-for-cache.
 *
 * Remplace les appels Feign userClient.getUserById() dans ReactionService.
 * Avantages :
 *  - Zéro appel synchrone vers user-service pour l'affichage des reactors
 *  - Résilience : si user-service est indisponible, le cache continue à fonctionner
 *  - Découplage total entre reaction-service et user-service
 */
@Service
public class UserCacheService {

    private static final Logger log = LoggerFactory.getLogger(UserCacheService.class);

    /** Cache en mémoire : userId → UserEventDTO */
    private final Map<Integer, UserEventDTO> cache = new ConcurrentHashMap<>();

    /**
     * Met à jour (ou insère) un utilisateur dans le cache.
     * Appelé par UserConsumer à la réception d'un message RabbitMQ.
     */
    public void put(UserEventDTO dto) {
        cache.put(dto.getId(), dto);
        log.info("[UserCache] Mis à jour : userId={} ({} {})",
                dto.getId(), dto.getName(), dto.getLastName());
    }

    /**
     * Récupère un utilisateur depuis le cache local.
     *
     * @param userId identifiant de l'utilisateur
     * @return Optional vide si le user n'est pas encore en cache
     */
    public Optional<UserEventDTO> get(Integer userId) {
        return Optional.ofNullable(cache.get(userId));
    }

    /** Nombre d'entrées dans le cache (utile pour les logs/debug). */
    public int size() {
        return cache.size();
    }
}