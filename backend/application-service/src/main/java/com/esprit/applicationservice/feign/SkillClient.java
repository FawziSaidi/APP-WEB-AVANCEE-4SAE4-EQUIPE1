package com.esprit.applicationservice.feign;

import com.esprit.applicationservice.dto.SkillDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@FeignClient(name = "skill-service")
public interface SkillClient {

    @GetMapping("/api/skills/freelancer/{freelancerId}")
    List<SkillDto> getSkillsByFreelancer(
            @PathVariable("freelancerId") Long freelancerId,
            @RequestHeader("Authorization") String token
    );
}