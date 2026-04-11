package com.esprit.reactionservice.services;

import com.esprit.reactionservice.clients.PublicationClient;
import com.esprit.reactionservice.dto.ReactionSummaryDTO;
import com.esprit.reactionservice.dto.ReactorDTO;
import com.esprit.reactionservice.dto.UserEventDTO;
import com.esprit.reactionservice.entities.Reaction;
import com.esprit.reactionservice.entities.TypeReaction;
import com.esprit.reactionservice.repositories.ReactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * ReactionService modifié :
 *
 * La communication avec user-service passe désormais par RabbitMQ + cache local.
 *  - UserClient (Feign) est SUPPRIMÉ pour les infos utilisateur.
 *  - UserCacheService (cache local alimenté par RabbitMQ) le REMPLACE.
 *
 * La communication avec publication-service reste via Feign (PublicationClient).
 *
 * Avantages :
 *  - Pas d'appel synchrone vers user-service lors de toggleReaction() ou getSummary()
 *  - Résilience : reaction-service fonctionne même si user-service est indisponible
 *  - Découplage total entre reaction-service et user-service
 */
@Service
@RequiredArgsConstructor
public class ReactionService {

    private final ReactionRepository reactionRepository;
    private final UserCacheService   userCacheService;    // ← remplace UserClient
    private final PublicationClient  publicationClient;  // ← reste Feign

    @Transactional
    public Optional<Reaction> toggleReaction(Integer publicationId, Integer userId, TypeReaction type) {
        // Vérification de l'utilisateur via le cache local (RabbitMQ)
        if (userCacheService.get(userId).isEmpty()) {
            throw new RuntimeException("User not found in cache: " + userId
                    + ". L'utilisateur n'est pas encore synchronisé via RabbitMQ.");
        }

        // Vérification de la publication via Feign (reste synchrone)
        try {
            publicationClient.getPublicationById(publicationId);
        } catch (Exception e) {
            throw new RuntimeException("Publication not found: " + publicationId);
        }

        Optional<Reaction> existing = reactionRepository.findByPublicationIdAndUserId(publicationId, userId);
        if (existing.isPresent()) {
            Reaction r = existing.get();
            if (r.getType() == type) {
                reactionRepository.delete(r);
                return Optional.empty();
            } else {
                r.setType(type);
                return Optional.of(reactionRepository.save(r));
            }
        } else {
            Reaction r = new Reaction();
            r.setUserId(userId);
            r.setPublicationId(publicationId);
            r.setType(type);
            return Optional.of(reactionRepository.save(r));
        }
    }

    public ReactionSummaryDTO getSummary(Integer publicationId, Integer userId) {
        List<Reaction> all = reactionRepository.findByPublicationId(publicationId);

        long likes    = all.stream().filter(r -> r.getType() == TypeReaction.LIKE).count();
        long dislikes = all.stream().filter(r -> r.getType() == TypeReaction.DISLIKE).count();
        long hearts   = all.stream().filter(r -> r.getType() == TypeReaction.HEART).count();

        TypeReaction userReaction = all.stream()
                .filter(r -> r.getUserId().equals(userId))
                .map(Reaction::getType)
                .findFirst()
                .orElse(null);

        List<ReactorDTO> reactors = all.stream().map(r -> {
            // Résolution du nom via cache local RabbitMQ — plus d'appel Feign
            String name = userCacheService.get(r.getUserId())
                    .map(dto -> dto.getName() + " " + dto.getLastName())
                    .orElse("User " + r.getUserId());   // fallback si pas encore en cache
            return new ReactorDTO(r.getUserId(), name, r.getType());
        }).collect(Collectors.toList());

        return new ReactionSummaryDTO(likes, dislikes, hearts, userReaction, reactors);
    }
}