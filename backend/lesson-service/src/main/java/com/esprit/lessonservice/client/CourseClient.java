package com.esprit.lessonservice.client;

import com.esprit.lessonservice.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "course-service", configuration = FeignConfig.class)
public interface CourseClient {

    @GetMapping("/api/courses/{courseId}")
    CourseDTO getCourse(@PathVariable Long courseId);

    @GetMapping("/api/courses/{courseId}/enrollment/{userId}")
    EnrollmentDTO getEnrollment(@PathVariable Long courseId, @PathVariable Integer userId);

    @PostMapping("/api/courses/{courseId}/enroll")
    void enroll(@PathVariable Long courseId, @RequestParam Integer userId);

    record CourseDTO(Long id, String title, String description, String category) {}

    record EnrollmentDTO(Long id, Long courseId, Integer userId, String status) {}
}