package com.esprit.inscriptionservice.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;



@Configuration
public class RabbitMQConfig {

    // ─── Une seule queue pour toute la communication avec user-service ───
    public static final String INSCRIPTION_QUEUE    = "inscription.user.queue";
    public static final String INSCRIPTION_EXCHANGE = "inscription.exchange";
    public static final String INSCRIPTION_ROUTING_KEY = "inscription.user.routing";

    @Bean
    public Queue inscriptionQueue() {
        // durable = true : la queue survit à un redémarrage de RabbitMQ
        return new Queue(INSCRIPTION_QUEUE, true);
    }

    @Bean
    public DirectExchange inscriptionExchange() {
        return new DirectExchange(INSCRIPTION_EXCHANGE);
    }

    @Bean
    public Binding inscriptionBinding(Queue inscriptionQueue, DirectExchange inscriptionExchange) {
        return BindingBuilder
                .bind(inscriptionQueue)
                .to(inscriptionExchange)
                .with(INSCRIPTION_ROUTING_KEY);
    }

    // ─── Convertisseur JSON (sérialise / désérialise les messages en JSON) ───
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }
}
