package com.esprit.applicationservice.feign;

import com.esprit.applicationservice.dto.ProjectDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "project-service")
public interface ProjectClient {

    @GetMapping("/api/projects/{id}")
    ProjectDto getProjectById(
            @PathVariable("id") Long id,
            @RequestHeader("Authorization") String token
    );
}
