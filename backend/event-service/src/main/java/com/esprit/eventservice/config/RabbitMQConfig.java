package com.esprit.eventservice.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // ── Noms constants ──────────────────────────────────────────────────────
    public static final String QUEUE_NAME    = "event-user.queue";
    public static final String EXCHANGE_NAME = "event-user.exchange";
    public static final String ROUTING_KEY   = "event.user.notification";

    // ── Queue unique ────────────────────────────────────────────────────────
    @Bean
    public Queue eventUserQueue() {
        return QueueBuilder.durable(QUEUE_NAME).build();
    }

    // ── Exchange de type Direct ─────────────────────────────────────────────
    @Bean
    public DirectExchange eventUserExchange() {
        return new DirectExchange(EXCHANGE_NAME);
    }

    // ── Binding Queue ↔ Exchange via la routing key ────────────────────────
    @Bean
    public Binding eventUserBinding(Queue eventUserQueue, DirectExchange eventUserExchange) {
        return BindingBuilder
                .bind(eventUserQueue)
                .to(eventUserExchange)
                .with(ROUTING_KEY);
    }

    // ── Convertisseur JSON ──────────────────────────────────────────────────
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    // ── Template configuré avec le convertisseur JSON ───────────────────────
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
}
