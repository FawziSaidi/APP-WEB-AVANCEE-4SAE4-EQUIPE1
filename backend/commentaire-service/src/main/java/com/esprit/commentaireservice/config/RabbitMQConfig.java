package com.esprit.commentaireservice.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration RabbitMQ côté commentaire-service (CONSOMMATEUR).
 *
 * commentaire-service s'abonne à la même queue "user.sync.queue"
 * que publication-service pour maintenir son propre cache local des users.
 *
 * La queue est déjà déclarée par publication-service (consommateur principal).
 * RabbitMQ supporte plusieurs consommateurs sur la même queue,
 * mais ici chaque service a besoin de TOUS les messages →
 * il faut utiliser deux queues distinctes avec un fanout exchange,
 * OU simplement une deuxième queue dédiée à commentaire-service.
 *
 * Solution choisie : queue dédiée "user.sync.queue.commentaire"
 * user-service publie dans les deux queues.
 */
@Configuration
public class RabbitMQConfig {

    /** Queue dédiée à commentaire-service pour la synchro des users */
    public static final String USER_QUEUE_COMMENTAIRE = "user.sync.queue.commentaire";

    @Bean
    public Queue userQueueCommentaire() {
        return new Queue(USER_QUEUE_COMMENTAIRE, true);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

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