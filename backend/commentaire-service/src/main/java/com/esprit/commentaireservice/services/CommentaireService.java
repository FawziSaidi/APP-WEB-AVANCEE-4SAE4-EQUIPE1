package com.esprit.commentaireservice.services;

import com.esprit.commentaireservice.clients.PublicationClient;
import com.esprit.commentaireservice.dto.PublicationDTO;
import com.esprit.commentaireservice.dto.UserEventDTO;
import com.esprit.commentaireservice.entities.Commentaire;
import com.esprit.commentaireservice.repositories.CommentaireRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * CommentaireService modifié pour utiliser RabbitMQ au lieu de Feign
 * pour la relation avec user-service.
 *
 * Changements :
 *  - Suppression de UserClient (Feign) → remplacé par UserCacheService
 *  - PublicationClient (Feign) → gardé tel quel
 *  - enrichWithUser() lit depuis le cache local alimenté par RabbitMQ
 */
@Service
@RequiredArgsConstructor
public class CommentaireService {

    private final CommentaireRepository commentaireRepository;
    private final PublicationClient     publicationClient;   // ← Feign gardé
    private final UserCacheService      userCacheService;    // ← remplace UserClient

    public List<Commentaire> getAllCommentaires() {
        List<Commentaire> list = commentaireRepository.findAllByOrderByCreateAtDesc();
        list.forEach(this::enrichWithUser);
        return list;
    }

    public List<Commentaire> getByPublicationId(Integer publicationId) {
        List<Commentaire> list = commentaireRepository.findRootByPublicationIdOrderByPinned(publicationId);
        list.forEach(this::enrichWithUser);
        return list;
    }

    public Commentaire getById(Integer id) {
        return commentaireRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Commentaire not found: " + id));
    }

    public Commentaire create(String contenue, Integer publicationId, Integer userId) {
        if (contenue == null || contenue.trim().isEmpty())
            throw new IllegalArgumentException("Content required");

        // PublicationClient Feign gardé pour valider l'existence de la publication
        try {
            publicationClient.getPublicationById(publicationId);
        } catch (Exception e) {
            throw new RuntimeException("Publication not found: " + publicationId);
        }

        Commentaire c = new Commentaire();
        c.setContenue(contenue);
        c.setUserId(userId);
        c.setPublicationId(publicationId);

        Commentaire saved = commentaireRepository.save(c);
        enrichWithUser(saved);
        return saved;
    }

    public Commentaire reply(String contenue, Integer parentId,
                             Integer publicationId, Integer userId) {
        if (contenue == null || contenue.trim().isEmpty())
            throw new IllegalArgumentException("Content required");

        Commentaire parent = getById(parentId);

        Commentaire c = new Commentaire();
        c.setContenue(contenue);
        c.setUserId(userId);
        c.setPublicationId(publicationId);
        c.setParent(parent);

        Commentaire saved = commentaireRepository.save(c);
        enrichWithUser(saved);
        return saved;
    }

    public Commentaire update(Integer id, String contenue, Integer userId) {
        Commentaire c = getById(id);
        if (!c.getUserId().equals(userId)) throw new RuntimeException("Not authorized");
        if (contenue != null && !contenue.trim().isEmpty()) c.setContenue(contenue);
        return commentaireRepository.save(c);
    }

    public void delete(Integer id, Integer userId) {
        Commentaire c = getById(id);
        if (!c.getUserId().equals(userId)) throw new RuntimeException("Not authorized");
        commentaireRepository.delete(c);
    }

    public Commentaire togglePin(Integer id, Integer userId) {
        Commentaire c = getById(id);
        // PublicationClient Feign gardé pour vérifier le propriétaire de la publication
        try {
            PublicationDTO pub = publicationClient.getPublicationById(c.getPublicationId());
            if (!pub.getUserId().equals(userId))
                throw new RuntimeException("Only publication owner can pin");
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Error checking publication");
        }
        c.setPinned(!c.isPinned());
        return commentaireRepository.save(c);
    }

    // ── Helper ────────────────────────────────────────────────────────────────

    /**
     * Enrichit un commentaire et ses réponses avec les données de l'auteur
     * depuis le cache RabbitMQ (remplace userClient.getUserById()).
     */
    private void enrichWithUser(Commentaire c) {
        // Enrichir le commentaire principal
        userCacheService.get(c.getUserId()).ifPresent(c::setUser);

        // Enrichir les réponses (replies)
        if (c.getReplies() != null && !c.getReplies().isEmpty()) {
            c.getReplies().forEach(reply ->
                    userCacheService.get(reply.getUserId()).ifPresent(reply::setUser)
            );
        }
    }
}