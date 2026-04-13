package com.esprit.inscriptionservice.messaging;

import com.esprit.inscriptionservice.config.RabbitMQConfig;
import com.esprit.inscriptionservice.dto.EventDTO;
import com.esprit.inscriptionservice.entities.EventInscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * Publie des événements d'inscription dans la queue RabbitMQ.
 * Le user-service (ou tout autre service abonné) reçoit ces messages
 * via {@link InscriptionEventListener}.
 */
@Component
public class InscriptionEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(InscriptionEventPublisher.class);

    private final RabbitTemplate rabbitTemplate;

    public InscriptionEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    // ─── Publication ──────────────────────────────────────────────────────

    public void publishInscriptionSubmitted(EventInscription inscription, EventDTO event) {
        InscriptionEvent message = buildBaseEvent(inscription, event);
        message.setAction(InscriptionEvent.Action.INSCRIPTION_SUBMITTED);
        publish(message);
    }

    public void publishInscriptionAccepted(EventInscription inscription, EventDTO event) {
        InscriptionEvent message = buildBaseEvent(inscription, event);
        message.setAction(InscriptionEvent.Action.INSCRIPTION_ACCEPTED);
        message.setBadgeImagePath(inscription.getBadgeImagePath());
        publish(message);
    }

    public void publishInscriptionRejected(EventInscription inscription, EventDTO event) {
        InscriptionEvent message = buildBaseEvent(inscription, event);
        message.setAction(InscriptionEvent.Action.INSCRIPTION_REJECTED);
        publish(message);
    }

    // ─── Helpers ──────────────────────────────────────────────────────────

    private void publish(InscriptionEvent event) {
        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.INSCRIPTION_EXCHANGE,
                    RabbitMQConfig.INSCRIPTION_ROUTING_KEY,
                    event
            );
            log.info("[RabbitMQ] Message publié → action={}, inscriptionId={}, userId={}",
                    event.getAction(), event.getInscriptionId(), event.getUserId());
        } catch (Exception e) {
            log.error("[RabbitMQ] Échec de publication du message : {}", e.getMessage(), e);
        }
    }

    private InscriptionEvent buildBaseEvent(EventInscription inscription, EventDTO event) {
        InscriptionEvent msg = new InscriptionEvent();
        msg.setInscriptionId(inscription.getId());
        msg.setUserId(inscription.getUserId());
        msg.setEventId(inscription.getEventId());
        msg.setParticipantNom(inscription.getParticipantNom());
        msg.setParticipantPrenom(inscription.getParticipantPrenom());
        msg.setParticipantEmail(inscription.getParticipantEmail());
        msg.setStatus(inscription.getStatus());
        msg.setRegistrationDate(inscription.getRegistrationDate());
        msg.setEventTitle(event != null ? event.getTitle() : null);
        return msg;
    }
}
