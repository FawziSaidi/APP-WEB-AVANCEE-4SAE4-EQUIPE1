package com.esprit.projectservice.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@FeignClient(name = "skill-service")
public interface SkillClient {

    @GetMapping("/api/skills/freelancer/{freelancerId}")
    List<Object> getSkillsByFreelancer(
            @PathVariable("freelancerId") Long freelancerId,
            @RequestHeader("Authorization") String token
    );

    @GetMapping("/api/skills/project/{projectId}")
    List<Object> getSkillsByProject(
            @PathVariable("projectId") Long projectId,
            @RequestHeader("Authorization") String token
    );
}