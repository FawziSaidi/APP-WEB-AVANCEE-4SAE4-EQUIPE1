package com.esprit.publicationservice.services;

import com.esprit.publicationservice.dto.UserEventDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Cache local des utilisateurs dans publication-service.
 *
 * Au lieu d'appeler Feign à chaque requête, publication-service
 * maintient une Map<userId, UserEventDTO> mise à jour par RabbitMQ.
 * Chaque fois que user-service crée ou modifie un utilisateur, il publie
 * un événement RabbitMQ consommé par UserConsumer qui met à jour ce cache.
 *
 * Avantages :
 *  - Zéro appel Feign synchrone pour l'enrichissement des publications
 *  - Résilience : si user-service est temporairement indisponible,
 *    publication-service continue à fonctionner avec les données en cache
 *  - Découplage total entre les deux micro-services
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
        log.info("[UserCache] Mis à jour : userId={} ({} {})", dto.getId(), dto.getName(), dto.getLastName());
    }

    /**
     * Récupère un utilisateur depuis le cache local.
     *
     * @param userId identifiant de l'utilisateur
     * @return Optional vide si le user n'est pas encore en cache
     *         (p. ex. si publication-service a démarré avant que user-service
     *          n'envoie son premier événement)
     */
    public Optional<UserEventDTO> get(Integer userId) {
        return Optional.ofNullable(cache.get(userId));
    }

    /** Nombre d'entrées dans le cache (utile pour les logs/debug). */
    public int size() {
        return cache.size();
    }
}