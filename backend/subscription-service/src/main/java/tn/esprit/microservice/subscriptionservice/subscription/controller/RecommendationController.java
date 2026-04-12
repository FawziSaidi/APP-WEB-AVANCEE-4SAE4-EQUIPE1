package tn.esprit.microservice.subscriptionservice.subscription.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit.microservice.subscriptionservice.subscription.dto.response.AIRecommendationDTO;
import tn.esprit.microservice.subscriptionservice.subscription.dto.response.UserProfileDTO;
import tn.esprit.microservice.subscriptionservice.subscription.service.RecommendationAIService;

@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
public class RecommendationController {

    private final RecommendationAIService recommendationService;

    @PostMapping("/generate/{userId}")
    public ResponseEntity<AIRecommendationDTO> generateRecommendation(@PathVariable Long userId) {
        return ResponseEntity.ok(recommendationService.generateRecommendation(userId));
    }

    @GetMapping("/profile/{userId}")
    public ResponseEntity<UserProfileDTO> getUserProfile(@PathVariable Long userId) {
        return ResponseEntity.ok(recommendationService.buildUserProfile(userId));
    }

    @PostMapping("/track/{recommendationId}")
    public ResponseEntity<Void> trackAction(
            @PathVariable Long recommendationId,
            @RequestParam String action) {
        recommendationService.trackUserAction(recommendationId, action);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/feedback/{recommendationId}")
    public ResponseEntity<Void> submitFeedback(
            @PathVariable Long recommendationId,
            @RequestParam Integer score,
            @RequestParam(required = false) String comment) {
        recommendationService.submitFeedback(recommendationId, score, comment);
        return ResponseEntity.ok().build();
    }
}