package com.esprit.activityservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String QUEUE_NAME    = "activity-user.queue";
    public static final String EXCHANGE_NAME = "activity-user.exchange";
    public static final String ROUTING_KEY   = "activity.user.notification";

    @Bean
    public Queue activityUserQueue() {
        return QueueBuilder.durable(QUEUE_NAME).build();
    }

    @Bean
    public DirectExchange activityUserExchange() {
        return new DirectExchange(EXCHANGE_NAME);
    }

    @Bean
    public Binding activityUserBinding(Queue activityUserQueue,
                                       DirectExchange activityUserExchange) {
        return BindingBuilder
                .bind(activityUserQueue)
                .to(activityUserExchange)
                .with(ROUTING_KEY);
    }

    // ✅ Correction : ajout du JavaTimeModule pour gérer LocalDateTime
    @Bean
    public MessageConverter jsonMessageConverter() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return new Jackson2JsonMessageConverter(mapper);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
}