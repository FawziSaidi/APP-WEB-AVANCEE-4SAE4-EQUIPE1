package com.esprit.projectservice.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@FeignClient(name = "application-service")
public interface ApplicationClient {

    @GetMapping("/api/applications/project/{projectId}")
    List<Object> getApplicationsByProject(
            @PathVariable("projectId") Long projectId,
            @RequestHeader("Authorization") String token
    );

    @GetMapping("/api/applications/check")
    Boolean checkAlreadyApplied(
            @RequestParam("freelancerId") Long freelancerId,
            @RequestParam("projectId") Long projectId,
            @RequestHeader("Authorization") String token
    );

    @PatchMapping("/api/applications/{id}/accept")
    Object acceptApplication(
            @PathVariable("id") Long id,
            @RequestHeader("Authorization") String token
    );

    @GetMapping("/api/applications/count/{freelancerId}")
    Long countByFreelancer(
            @PathVariable("freelancerId") Long freelancerId,
            @RequestHeader("Authorization") String token
    );
}