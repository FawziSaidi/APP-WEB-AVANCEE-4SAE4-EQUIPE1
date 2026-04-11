package com.esprit.userservice.services;

import com.esprit.userservice.dto.UserEventDTO;
import com.esprit.userservice.entities.User;
import com.esprit.userservice.messaging.UserProducer;
import com.esprit.userservice.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * UserService modifié : publie un événement RabbitMQ après chaque
 * opération de création / mise à jour / désactivation d'un utilisateur.
 * publication-service maintient ainsi un cache local des users et
 * n'a plus besoin d'appeler Feign à chaque requête.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository     repo;
    private final KeycloakService    keycloakService;
    private final UserProducer       userProducer;   // ← nouveau

    public User getById(Integer id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found: " + id));
    }

    /**
     * Désactive le user dans la BDD et dans Keycloak,
     * puis publie l'événement de mise à jour dans RabbitMQ.
     */
    public void deactivate(Integer id) {
        User user = getById(id);
        user.setEnabled(false);
        repo.save(user);
        keycloakService.disableUser(user.getEmail());

        // Notifier publication-service que le user est désactivé
        userProducer.publishUser(toDTO(user));
        log.info("[UserService] User {} désactivé et événement publié.", id);
    }

    public List<User> searchByName(String query) {
        if (query == null || query.trim().isEmpty()) return List.of();
        return repo.searchByName(query.trim());
    }

    public List<User> getAll() {
        return repo.findAll();
    }

    /**
     * Publie les données d'un utilisateur existant dans RabbitMQ.
     * À appeler depuis AuthService après l'enregistrement d'un nouveau user.
     */
    public void publishUserEvent(User user) {
        userProducer.publishUser(toDTO(user));
    }

    // ── Helper ────────────────────────────────────────────────────────────────

    private UserEventDTO toDTO(User u) {
        return new UserEventDTO(
                u.getId(),
                u.getName(),
                u.getLastName(),
                u.getEmail(),
                u.isEnabled()
        );
    }
}