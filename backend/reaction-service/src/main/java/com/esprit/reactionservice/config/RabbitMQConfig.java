package com.esprit.reactionservice.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration RabbitMQ côté reaction-service (CONSOMMATEUR).
 *
 * reaction-service s'abonne à la queue "user.sync.queue.reaction"
 * pour maintenir un cache local des utilisateurs.
 * La queue est dédiée à ce service : chaque message envoyé par
 * user-service est reçu en entier par reaction-service.
 *
 * user-service (producteur) publie dans cette queue à chaque
 * création ou modification d'un utilisateur.
 */
@Configuration
public class RabbitMQConfig {

    /** Nom de la queue — doit correspondre à USER_QUEUE_REACTION dans user-service */
    public static final String USER_QUEUE_REACTION = "user.sync.queue.reaction";

    /**
     * Déclare la queue comme durable (survit au redémarrage du broker).
     */
    @Bean
    public Queue userQueueReaction() {
        return new Queue(USER_QUEUE_REACTION, true);
    }

    /** Convertisseur JSON ↔ POJO */
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * Factory utilisée par @RabbitListener.
     * Injecte le convertisseur pour que Spring désérialise
     * automatiquement le JSON en UserEventDTO.
     */
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory cf, MessageConverter converter) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(cf);
        factory.setMessageConverter(converter);
        factory.setConcurrentConsumers(1);
        factory.setMaxConcurrentConsumers(3);
        return factory;
    }
}