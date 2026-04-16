package com.esprit.courseservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // Course events exchange
    @Value("${rabbitmq.exchange.course:course.exchange}")
    private String courseExchange;

    // Queues
    @Value("${rabbitmq.queue.course.enrolled:course.enrolled.queue}")
    private String courseEnrolledQueue;

    @Value("${rabbitmq.queue.lesson.completed:lesson.completed.queue}")
    private String lessonCompletedQueue;

    // Routing keys
    @Value("${rabbitmq.routing-key.course.enrolled:course.enrolled}")
    private String courseEnrolledRoutingKey;

    @Value("${rabbitmq.routing-key.lesson.completed:lesson.completed}")
    private String lessonCompletedRoutingKey;

    @Bean
    public TopicExchange courseExchange() {
        return new TopicExchange(courseExchange);
    }

    @Bean
    public Queue courseEnrolledQueue() {
        return QueueBuilder.durable(courseEnrolledQueue).build();
    }

    @Bean
    public Queue lessonCompletedQueue() {
        return QueueBuilder.durable(lessonCompletedQueue).build();
    }

    @Bean
    public Binding courseEnrolledBinding() {
        return BindingBuilder
                .bind(courseEnrolledQueue())
                .to(courseExchange())
                .with(courseEnrolledRoutingKey);
    }

    @Bean
    public Binding lessonCompletedBinding() {
        return BindingBuilder
                .bind(lessonCompletedQueue())
                .to(courseExchange())
                .with(lessonCompletedRoutingKey);
    }

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