package com.esprit.courseservice.Controllers;

import com.esprit.courseservice.client.UserClient;
import com.esprit.courseservice.dto.Cours.*;
import com.esprit.courseservice.Entities.Cours.Payment;
import com.esprit.courseservice.Services.Cours.CourseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;
    private final UserClient userClient;

    // =========================================================================
    // PUBLIC ENDPOINTS (NO AUTHENTICATION NEEDED)
    // =========================================================================

    @GetMapping("/api/courses/public/published")
    public ResponseEntity<List<CourseDTO.Summary>> getPublicPublishedCourses(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String search) {
        return ResponseEntity.ok(courseService.getPublishedCourses(category, search));
    }

    @GetMapping("/api/courses/public/{courseId}")
    public ResponseEntity<CourseDTO.Response> getPublicCourseById(@PathVariable Long courseId) {
        return ResponseEntity.ok(courseService.getCourseById(courseId));
    }

    @GetMapping("/api/courses/public/{courseId}/reviews")
    public ResponseEntity<List<ReviewDTO.Response>> getPublicCourseReviews(@PathVariable Long courseId) {
        return ResponseEntity.ok(courseService.getCourseReviews(courseId));
    }

    // =========================================================================
    // PROTECTED ENDPOINTS (AUTHENTICATION NEEDED)
    // =========================================================================

    @GetMapping("/api/courses")
    public ResponseEntity<List<CourseDTO.Summary>> getPublishedCourses(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String search) {
        return ResponseEntity.ok(courseService.getPublishedCourses(category, search));
    }

    @GetMapping("/api/courses/{courseId}")
    public ResponseEntity<CourseDTO.Response> getCourse(@PathVariable Long courseId) {
        return ResponseEntity.ok(courseService.getCourseById(courseId));
    }

    @PostMapping("/api/courses")
    public ResponseEntity<CourseDTO.Response> createCourse(
            @Valid @RequestBody CourseDTO.Request request,
            @RequestParam(required = false, defaultValue = "1") Integer createdBy) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(courseService.createCourse(request, createdBy));
    }

    @PutMapping("/api/courses/{courseId}")
    public ResponseEntity<CourseDTO.Response> updateCourse(
            @PathVariable Long courseId,
            @Valid @RequestBody CourseDTO.Request request) {
        return ResponseEntity.ok(courseService.updateCourse(courseId, request));
    }

    @DeleteMapping("/api/courses/{courseId}")
    public ResponseEntity<Void> deleteCourse(@PathVariable Long courseId) {
        courseService.deleteCourse(courseId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/api/admin/courses")
    public ResponseEntity<List<CourseDTO.Response>> getAllForAdmin() {
        return ResponseEntity.ok(courseService.getAllCoursesForAdmin());
    }

    // =========================================================================
    // ENROLLMENT
    // =========================================================================

    @PostMapping("/api/courses/{courseId}/enroll")
    public ResponseEntity<Void> enroll(
            @PathVariable Long courseId,
            @RequestBody Map<String, Integer> body) {
        courseService.enroll(courseId, body.get("userId"));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/api/courses/{courseId}/enrollment/{userId}")
    public ResponseEntity<EnrollmentDTO.Response> getEnrollment(
            @PathVariable Long courseId,
            @PathVariable Integer userId) {
        return ResponseEntity.ok(courseService.getEnrollmentByCourseAndUser(courseId, userId));
    }

    // =========================================================================
    // PAYMENT
    // =========================================================================

    @PostMapping("/api/payments/initiate")
    public ResponseEntity<PaymentDTO.Response> initiatePayment(
            @RequestBody PaymentDTO.Request request) {
        return ResponseEntity.ok(courseService.initiatePayment(request));
    }

    // =========================================================================
    // COUPON
    // =========================================================================

    @GetMapping("/api/coupons/validate")
    public ResponseEntity<CouponDTO.ValidateResponse> validateCoupon(
            @RequestParam Long courseId,
            @RequestParam String code) {
        return ResponseEntity.ok(courseService.validateCoupon(courseId, code));
    }

    // =========================================================================
    // REVIEWS
    // =========================================================================

    @GetMapping("/api/courses/{courseId}/reviews")
    public ResponseEntity<List<ReviewDTO.Response>> getCourseReviews(
            @PathVariable Long courseId) {
        return ResponseEntity.ok(courseService.getCourseReviews(courseId));
    }

    @PostMapping("/api/reviews")
    public ResponseEntity<ReviewDTO.Response> submitReview(
            @RequestBody ReviewDTO.Request request) {
        return ResponseEntity.ok(courseService.submitReview(request));
    }

    @PostMapping("/api/reviews/{reviewId}/helpful")
    public ResponseEntity<Void> markReviewHelpful(@PathVariable Long reviewId) {
        courseService.markReviewHelpful(reviewId);
        return ResponseEntity.ok().build();
    }

    // =========================================================================
    // CERTIFICATES
    // =========================================================================

    @GetMapping("/api/users/{userId}/certificates")
    public ResponseEntity<List<CertificateDTO.Response>> getUserCertificates(
            @PathVariable Integer userId) {
        return ResponseEntity.ok(courseService.getUserCertificates(userId));
    }

    @GetMapping("/api/certificates/verify/{code}")
    public ResponseEntity<CertificateDTO.Response> verifyCertificate(
            @PathVariable String code) {
        return ResponseEntity.ok(courseService.verifyCertificate(code));
    }

    // =========================================================================
    // ADMIN — PAYMENTS
    // =========================================================================

    @GetMapping("/api/admin/payments")
    public ResponseEntity<?> getAdminPayments(
            @RequestParam(required = false) Long courseId,
            @RequestParam(required = false) Integer userId) {

        List<Payment> payments = courseService.getAdminPayments(courseId, userId);
        List<Map<String, Object>> result = payments.stream().map(p -> {
            Integer uid = p.getUserId();
            UserClient.UserDTO user = null;
            try {
                user = userClient.getUserById(uid);
            } catch (Exception e) {
                // fallback
            }
            String userName = (user != null) ? user.getName() + " " + user.getLastName() : "User " + uid;
            String userEmail = (user != null) ? user.getEmail() : "unknown@example.com";

            Map<String, Object> r = new LinkedHashMap<>();
            r.put("paymentId", p.getId().toString());
            r.put("userId", uid);
            r.put("userName", userName);
            r.put("userEmail", userEmail);
            r.put("courseId", p.getCourse().getId());
            r.put("courseTitle", p.getCourse().getTitle());
            r.put("amount", p.getAmount());
            r.put("currency", p.getCurrency());
            r.put("method", p.getMethod().name());
            r.put("status", p.getStatus().name());
            r.put("createdAt", p.getCreatedAt());
            r.put("completedAt", p.getCompletedAt());
            return r;
        }).toList();

        return ResponseEntity.ok(result);
    }
}