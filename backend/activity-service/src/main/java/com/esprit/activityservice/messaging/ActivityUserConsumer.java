package com.esprit.activityservice.messaging;

import com.esprit.activityservice.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Consommateur RabbitMQ : écoute la queue "activity-user.queue".
 * Traite les messages en provenance du user-service (ou d'autres services).
 */
@Component
public class ActivityUserConsumer {

    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void receiveMessage(ActivityUserMessage message) {
        System.out.println("[RabbitMQ ◀] Message reçu ← " + message);

        if (message == null || message.getAction() == null) {
            System.err.println("[RabbitMQ] Message invalide ou action nulle, ignoré.");
            return;
        }

        switch (message.getAction()) {
            case ACTIVITY_CREATED:
                handleActivityCreated(message);
                break;
            case ACTIVITY_UPDATED:
                handleActivityUpdated(message);
                break;
            case ACTIVITY_DELETED:
                handleActivityDeleted(message);
                break;
            default:
                System.out.println("[RabbitMQ] Action inconnue : " + message.getAction());
        }
    }

    private void handleActivityCreated(ActivityUserMessage msg) {
        System.out.println("[RabbitMQ] ACTIVITY_CREATED — activityId=" + msg.getActivityId()
                + ", eventId=" + msg.getEventId()
                + ", userId=" + msg.getUserId());
        // TODO : logique métier (ex. notifier l'organisateur, incrémenter un compteur…)
    }

    private void handleActivityUpdated(ActivityUserMessage msg) {
        System.out.println("[RabbitMQ] ACTIVITY_UPDATED — activityId=" + msg.getActivityId());
        // TODO : notifier les participants concernés
    }

    private void handleActivityDeleted(ActivityUserMessage msg) {
        System.out.println("[RabbitMQ] ACTIVITY_DELETED — activityId=" + msg.getActivityId());
        // TODO : nettoyer les inscriptions liées à cette activité
    }
}