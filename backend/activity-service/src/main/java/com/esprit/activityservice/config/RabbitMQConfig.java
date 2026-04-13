package com.esprit.activityservice.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // ── Une seule queue pour la communication activity-service ↔ user-service
    public static final String QUEUE_NAME    = "activity-user.queue";
    public static final String EXCHANGE_NAME = "activity-user.exchange";
    public static final String ROUTING_KEY   = "activity.user.notification";

    // ── Queue durable (survit aux redémarrages du broker) ──────────────────
    @Bean
    public Queue activityUserQueue() {
        return QueueBuilder.durable(QUEUE_NAME).build();
    }

    // ── Exchange de type Direct ─────────────────────────────────────────────
    @Bean
    public DirectExchange activityUserExchange() {
        return new DirectExchange(EXCHANGE_NAME);
    }

    // ── Binding : Queue ↔ Exchange via la routing key ──────────────────────
    @Bean
    public Binding activityUserBinding(Queue activityUserQueue,
                                       DirectExchange activityUserExchange) {
        return BindingBuilder
                .bind(activityUserQueue)
                .to(activityUserExchange)
                .with(ROUTING_KEY);
    }

    // ── Convertisseur JSON (sérialise les objets Java en JSON) ─────────────
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    // ── RabbitTemplate configuré avec le convertisseur JSON ────────────────
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
}
