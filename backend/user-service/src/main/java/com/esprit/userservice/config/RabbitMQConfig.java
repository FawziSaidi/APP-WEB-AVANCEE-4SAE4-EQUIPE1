package com.esprit.userservice.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String USER_QUEUE_PUBLICATION  = "user.sync.queue.publication";
    public static final String USER_QUEUE_COMMENTAIRE  = "user.sync.queue.commentaire";
    public static final String USER_QUEUE_REACTION     = "user.sync.queue.reaction";

    @Bean public Queue userQueuePublication()  { return new Queue(USER_QUEUE_PUBLICATION,  true); }
    @Bean public Queue userQueueCommentaire()  { return new Queue(USER_QUEUE_COMMENTAIRE,  true); }
    @Bean public Queue userQueueReaction()     { return new Queue(USER_QUEUE_REACTION,     true); }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                         MessageConverter converter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(converter);
        return template;
    }
}