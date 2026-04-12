package com.esprit.skillservice.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "project-service")
public interface ProjectClient {

    @GetMapping("/api/projects/{id}")
    Object getProjectById(
            @PathVariable("id") Long id,
            @RequestHeader("Authorization") String token
    );

    @GetMapping("/api/projects")
    Object getAllProjects(
            @RequestHeader("Authorization") String token
    );
}
