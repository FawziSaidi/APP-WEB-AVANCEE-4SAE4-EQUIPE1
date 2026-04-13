package com.esprit.eventservice.messaging;

import com.esprit.eventservice.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Consommateur RabbitMQ : écoute la queue "event-user.queue".
 * Utilisé pour recevoir les réponses ou demandes en provenance du user-service.
 *
 * Note : Dans une architecture événementielle, le user-service publie sur
 * cette même queue ; ce listener traite ces messages côté event-service.
 */
@Component
public class EventUserConsumer {

    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void receiveMessage(EventUserMessage message) {
        System.out.println("[RabbitMQ] Message reçu ← " + message);

        if (message == null || message.getAction() == null) {
            System.err.println("[RabbitMQ] Message invalide ou action nulle, ignoré.");
            return;
        }

        switch (message.getAction()) {
            case EVENT_CREATED:
                handleEventCreated(message);
                break;
            case EVENT_UPDATED:
                handleEventUpdated(message);
                break;
            case EVENT_ARCHIVED:
                handleEventArchived(message);
                break;
            case EVENT_CANCELLED:
                handleEventCancelled(message);
                break;
            default:
                System.out.println("[RabbitMQ] Action inconnue : " + message.getAction());
        }
    }

    private void handleEventCreated(EventUserMessage msg) {
        System.out.println("[RabbitMQ] Traitement EVENT_CREATED — eventId=" + msg.getEventId()
                + ", userId=" + msg.getUserId());
        // TODO : envoyer un email, déclencher une logique métier, etc.
    }

    private void handleEventUpdated(EventUserMessage msg) {
        System.out.println("[RabbitMQ] Traitement EVENT_UPDATED — eventId=" + msg.getEventId());
        // TODO : notifier les participants
    }

    private void handleEventArchived(EventUserMessage msg) {
        System.out.println("[RabbitMQ] Traitement EVENT_ARCHIVED — eventId=" + msg.getEventId());
        // TODO : informer les inscrits de l'annulation
    }

    private void handleEventCancelled(EventUserMessage msg) {
        System.out.println("[RabbitMQ] Traitement EVENT_CANCELLED — eventId=" + msg.getEventId());
        // TODO : rembourser / notifier
    }
}