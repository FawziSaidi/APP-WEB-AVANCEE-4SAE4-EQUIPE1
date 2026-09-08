package com.esprit.skillservice.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@FeignClient(name = "application-service")
public interface ApplicationClient {

    @GetMapping("/api/applications/freelancer/{freelancerId}")
    List<Object> getApplicationsByFreelancer(
            @PathVariable("freelancerId") Long freelancerId,
            @RequestHeader("Authorization") String token
    );

    @GetMapping("/api/applications/project/{projectId}")
    List<Object> getApplicationsByProject(
            @PathVariable("projectId") Long projectId,
            @RequestHeader("Authorization") String token
    );
}