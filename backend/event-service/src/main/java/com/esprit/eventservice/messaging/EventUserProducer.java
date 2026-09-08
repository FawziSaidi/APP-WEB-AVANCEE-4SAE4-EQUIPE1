package com.esprit.eventservice.messaging;

import com.esprit.eventservice.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * Producteur RabbitMQ : publie des messages dans la queue "event-user.queue"
 * pour notifier le user-service des événements importants.
 */
@Component
public class EventUserProducer {

    private final RabbitTemplate rabbitTemplate;

    public EventUserProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * Envoie un message générique vers la queue.
     */
    public void sendMessage(EventUserMessage message) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_NAME,
                RabbitMQConfig.ROUTING_KEY,
                message
        );
        System.out.println("[RabbitMQ] Message envoyé → " + message);
    }

    // ── Méthodes utilitaires ─────────────────────────────────────────────────

    public void notifyEventCreated(Long eventId, String eventTitle,
                                   Long userId, String userEmail) {
        sendMessage(new EventUserMessage(
                eventId, eventTitle, userId, userEmail,
                EventUserMessage.EventAction.EVENT_CREATED,
                "Votre événement \"" + eventTitle + "\" a été créé avec succès."
        ));
    }

    public void notifyEventUpdated(Long eventId, String eventTitle,
                                   Long userId, String userEmail) {
        sendMessage(new EventUserMessage(
                eventId, eventTitle, userId, userEmail,
                EventUserMessage.EventAction.EVENT_UPDATED,
                "L'événement \"" + eventTitle + "\" a été mis à jour."
        ));
    }

    public void notifyEventArchived(Long eventId, String eventTitle,
                                    Long userId, String userEmail) {
        sendMessage(new EventUserMessage(
                eventId, eventTitle, userId, userEmail,
                EventUserMessage.EventAction.EVENT_ARCHIVED,
                "L'événement \"" + eventTitle + "\" a été archivé."
        ));
    }
}
