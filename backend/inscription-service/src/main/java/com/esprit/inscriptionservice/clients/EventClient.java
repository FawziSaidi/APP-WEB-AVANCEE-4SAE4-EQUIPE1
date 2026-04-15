package com.esprit.inscriptionservice.clients;

import com.esprit.inscriptionservice.config.FeignClientconfig;
import com.esprit.inscriptionservice.dto.EventDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "event-service",
        configuration = FeignClientconfig.class )
public interface EventClient {

    @GetMapping("/api/events/{id}")
    EventDTO getEventById(@PathVariable("id") Long id);
}
