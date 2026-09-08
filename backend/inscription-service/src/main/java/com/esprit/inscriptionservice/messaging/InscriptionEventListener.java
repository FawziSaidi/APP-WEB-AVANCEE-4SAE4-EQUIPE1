package com.esprit.inscriptionservice.messaging;

import com.esprit.inscriptionservice.config.RabbitMQConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Écoute la queue RabbitMQ côté inscription-service.
 *
 * Dans une architecture microservices classique, c'est le USER-SERVICE
 * qui consomme cette queue. Ce listener est fourni à titre d'exemple
 * pour que l'inscription-service puisse aussi réagir à ses propres événements
 * (ex : audit, log centralisé, etc.).
 *
 * Si vous ne souhaitez pas que l'inscription-service consomme ses propres messages,
 * désactivez ce listener en commentant l'annotation @RabbitListener
 * ou en ajoutant spring.rabbitmq.listener.simple.auto-startup=false dans application.properties.
 */
@Component
public class InscriptionEventListener {

    private static final Logger log = LoggerFactory.getLogger(InscriptionEventListener.class);

    @RabbitListener(queues = RabbitMQConfig.INSCRIPTION_QUEUE)
    public void handleInscriptionEvent(InscriptionEvent event) {
        if (event == null || event.getAction() == null) {
            log.warn("[RabbitMQ] Message reçu invalide (null ou sans action)");
            return;
        }

        switch (event.getAction()) {
            case INSCRIPTION_SUBMITTED:
                log.info("[RabbitMQ] Nouvelle inscription soumise → inscriptionId={}, userId={}, eventId={}",
                        event.getInscriptionId(), event.getUserId(), event.getEventId());
                // TODO (user-service) : notifier l'utilisateur de la réception de sa demande
                break;

            case INSCRIPTION_ACCEPTED:
                log.info("[RabbitMQ] Inscription acceptée → inscriptionId={}, userId={}, badge={}",
                        event.getInscriptionId(), event.getUserId(), event.getBadgeImagePath());
                // TODO (user-service) : marquer l'inscription comme acceptée dans le profil utilisateur
                break;

            case INSCRIPTION_REJECTED:
                log.info("[RabbitMQ] Inscription rejetée → inscriptionId={}, userId={}",
                        event.getInscriptionId(), event.getUserId());
                // TODO (user-service) : notifier l'utilisateur du refus
                break;

            default:
                log.warn("[RabbitMQ] Action inconnue : {}", event.getAction());
        }
    }
}
