package com.esprit.publicationservice.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration RabbitMQ côté publication-service (CONSOMMATEUR).
 *
 * Responsabilités :
 *  - Déclarer la queue "user.sync.queue" (durable = true)
 *  - Configurer Jackson2JsonMessageConverter pour désérialiser automatiquement
 *    les messages JSON en UserEventDTO
 *
 * Le user-service (producteur) publiera dans cette queue à chaque
 * création ou modification d'un utilisateur.
 */
@Configuration
public class RabbitMQConfig {

    /** Nom de la queue – doit être identique à RabbitMQConfig.USER_QUEUE dans user-service */
    public static final String USER_QUEUE = "user.sync.queue";

    /**
     * Déclare la queue comme durable (survit au redémarrage du broker).
     * C'est le consommateur qui déclare la queue selon la meilleure pratique.
     */
    @Bean
    public Queue userQueue() {
        return new Queue(USER_QUEUE, true);
    }

    /** Convertisseur JSON <-> POJO */
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