package com.esprit.courseservice.client;

import lombok.Getter;
import lombok.Setter;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Calls user-service via Eureka to fetch user info.
 * Replaces the old UserRepository that was used directly.
 */
@FeignClient(name = "user-service")
public interface UserClient {

    @GetMapping("/api/users/{id}")
    UserDTO getUserById(@PathVariable("id") Integer id);

    /** Minimal DTO — only the fields course-service actually needs */
    @Getter
    @Setter
    class UserDTO {
        private Integer id;
        private String name;
        private String lastName;
        private String email;
    }
}