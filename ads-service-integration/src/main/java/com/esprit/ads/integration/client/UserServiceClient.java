package com.esprit.ads.integration.client;

import com.esprit.ads.integration.config.FeignClientConfig;
import com.esprit.ads.integration.dto.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(
        name = "user-service",
        url = "${services.user-service.url:http://user-service:8082}",
        configuration = FeignClientConfig.class
)
public interface UserServiceClient {

    @GetMapping("/users/{id}")
    UserDto getUserById(@PathVariable("id") String userId);

    @GetMapping("/users/keycloak/{keycloakId}")
    UserDto getUserByKeycloakId(@PathVariable("keycloakId") String keycloakId);
}
