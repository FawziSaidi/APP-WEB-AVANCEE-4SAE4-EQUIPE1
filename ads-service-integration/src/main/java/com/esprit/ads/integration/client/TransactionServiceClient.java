package com.esprit.ads.integration.client;

import com.esprit.ads.integration.config.FeignClientConfig;
import com.esprit.ads.integration.dto.TransactionDto;
import com.esprit.ads.integration.dto.TransactionListDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        name = "transaction-service",
        url = "${services.transaction-service.url:http://transaction-service:8084}",
        configuration = FeignClientConfig.class
)
public interface TransactionServiceClient {

    @GetMapping("/api/transactions/history")
    TransactionListDto getHistory(@RequestParam(defaultValue = "0") int skip,
                                  @RequestParam(defaultValue = "20") int limit);

    @GetMapping("/api/transactions/{id}")
    TransactionDto getById(@PathVariable("id") String transactionId);
}
