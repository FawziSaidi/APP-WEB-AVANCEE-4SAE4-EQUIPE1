package com.esprit.activityservice.messaging;

import com.esprit.activityservice.config.RabbitMQConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

/**
 * Producteur RabbitMQ : publie des messages dans la queue "activity-user.queue"
 * pour informer le user-service des changements sur les activités.
 */
@Component
public class ActivityUserProducer {

    private final RabbitTemplate rabbitTemplate;

    public ActivityUserProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * Méthode générique d'envoi.
     */
    public void sendMessage(ActivityUserMessage message) {
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_NAME,
                RabbitMQConfig.ROUTING_KEY,
                message
        );
        System.out.println("[RabbitMQ ▶] Message envoyé → " + message);
    }

    // ── Méthodes utilitaires ─────────────────────────────────────────────────

    public void notifyActivityCreated(Long activityId, String activityName,
                                      Long eventId, Long userId) {
        sendMessage(new ActivityUserMessage(
                activityId, activityName, eventId, userId,
                ActivityUserMessage.ActivityAction.ACTIVITY_CREATED,
                "L'activité \"" + activityName + "\" a été ajoutée à l'événement #" + eventId + "."
        ));
    }

    public void notifyActivityUpdated(Long activityId, String activityName,
                                      Long eventId, Long userId) {
        sendMessage(new ActivityUserMessage(
                activityId, activityName, eventId, userId,
                ActivityUserMessage.ActivityAction.ACTIVITY_UPDATED,
                "L'activité \"" + activityName + "\" a été mise à jour."
        ));
    }

    public void notifyActivityDeleted(Long activityId, String activityName,
                                      Long eventId, Long userId) {
        sendMessage(new ActivityUserMessage(
                activityId, activityName, eventId, userId,
                ActivityUserMessage.ActivityAction.ACTIVITY_DELETED,
                "L'activité \"" + activityName + "\" a été supprimée."
        ));
    }
}