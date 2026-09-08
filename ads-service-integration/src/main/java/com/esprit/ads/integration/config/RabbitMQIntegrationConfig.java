package com.esprit.ads.integration.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQIntegrationConfig {

    public static final String EXCHANGE = "app.events";
    public static final String TRANSACTION_QUEUE = "transaction.completed.ads";
    public static final String TRANSACTION_ROUTING_KEY = "transaction.completed";

    @Bean
    public TopicExchange appEventsExchange() {
        return ExchangeBuilder.topicExchange(EXCHANGE).durable(true).build();
    }

    @Bean
    public Queue transactionCompletedQueue() {
        return QueueBuilder.durable(TRANSACTION_QUEUE).build();
    }

    @Bean
    public Binding transactionBinding(Queue transactionCompletedQueue, TopicExchange appEventsExchange) {
        return BindingBuilder
                .bind(transactionCompletedQueue)
                .to(appEventsExchange)
                .with(TRANSACTION_ROUTING_KEY);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jsonMessageConverter());
        return template;
    }
}
