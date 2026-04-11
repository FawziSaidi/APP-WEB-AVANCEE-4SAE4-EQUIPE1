package com.esprit.ads.integration.listener;

import com.esprit.ads.integration.config.RabbitMQIntegrationConfig;
import com.esprit.ads.integration.dto.TransactionEventDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * Listens for transaction.completed events published by the transaction-service.
 * Drop this class into your ads-service and customize onTransactionCompleted() as needed.
 */
@Component
public class TransactionEventListener {

    private static final Logger log = LoggerFactory.getLogger(TransactionEventListener.class);

    @RabbitListener(queues = RabbitMQIntegrationConfig.TRANSACTION_QUEUE)
    public void onTransactionCompleted(TransactionEventDto event) {
        log.info("Received transaction event: id={}, sender={}, receiver={}, amount={} {}",
                event.getTransactionId(),
                event.getSenderId(),
                event.getReceiverId(),
                event.getAmount(),
                event.getCurrency());

        // TODO: add your ads-service business logic here.
        // Examples:
        //  - flag high-value users for premium ad targeting
        //  - update user activity scores
        //  - trigger a notification to involved users
    }
}
