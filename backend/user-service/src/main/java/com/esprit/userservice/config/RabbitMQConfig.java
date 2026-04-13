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
    public static final String USER_QUEUE_PROMO        = "user.sync.queue.promo";
    public static final String USER_QUEUE_SUBSCRIPTION = "user.sync.queue.subscription";
    public static final String USER_QUEUE_PROJECT     = "user.sync.queue.project";
    public static final String USER_QUEUE_APPLICATION = "user.sync.queue.application";
    public static final String USER_QUEUE_SKILL       = "user.sync.queue.skill";

    // inscription-service → user-service
    public static final String USER_QUEUE_INSCRIPTION  = "inscription.user.queue";

    // event-service → user-service
    public static final String USER_QUEUE_EVENT        = "event-user.queue";

    // activity-service → user-service
    public static final String USER_QUEUE_ACTIVITY     = "activity-user.queue";

    // ── Nouvelles queues : user-service ÉCOUTE ces services ─────────────────
    public static final String USER_QUEUE_INSCRIPTION_ACCEPTED = "inscription.accepted";
    public static final String USER_QUEUE_INSCRIPTION_REJECTED = "inscription.rejected";
    public static final String USER_QUEUE_EVENT_SYNC           = "user.sync.queue.event";
    public static final String USER_QUEUE_ACTIVITY_SYNC        = "user.sync.queue.activity";


    @Bean public Queue userQueuePublication()  { return new Queue(USER_QUEUE_PUBLICATION,  true); }
    @Bean public Queue userQueueCommentaire()  { return new Queue(USER_QUEUE_COMMENTAIRE,  true); }
    @Bean public Queue userQueueReaction()     { return new Queue(USER_QUEUE_REACTION,     true); }
    @Bean public Queue userQueuePromo()        { return new Queue(USER_QUEUE_PROMO,        true); }
    @Bean public Queue userQueueSubscription() { return new Queue(USER_QUEUE_SUBSCRIPTION, true); }
    @Bean public Queue userQueueProject()     { return new Queue(USER_QUEUE_PROJECT,     true); }
    @Bean public Queue userQueueApplication() { return new Queue(USER_QUEUE_APPLICATION, true); }
    @Bean public Queue userQueueSkill()       { return new Queue(USER_QUEUE_SKILL,       true); }

    @Bean public Queue userQueueInscription()  { return new Queue(USER_QUEUE_INSCRIPTION,  true); }
    @Bean public Queue userQueueEvent()        { return new Queue(USER_QUEUE_EVENT,        true); }
    @Bean public Queue userQueueActivity()     { return new Queue(USER_QUEUE_ACTIVITY,     true); }

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