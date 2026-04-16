package com.esprit.courseservice.Services.Cours;

import com.esprit.courseservice.client.UserClient;
import com.esprit.courseservice.dto.Cours.*;
import com.esprit.courseservice.dto.Cours.CourseEnrolledEventDTO;
import com.esprit.courseservice.dto.Cours.CourseCompletedEventDTO;
import com.esprit.courseservice.Entities.Cours.*;
import com.esprit.courseservice.Repositories.Cours.*;
import com.esprit.courseservice.events.publishers.CourseEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourseService {

    private final CourseRepository         courseRepository;
    // REMOVED: LessonRepository - now in lesson-service
    private final EnrollmentRepository     enrollmentRepository;
    // REMOVED: LessonProgressRepository - now in lesson-service
    private final PaymentRepository        paymentRepository;
    // REMOVED: LessonNoteRepository - now in lesson-service
    private final CourseReviewRepository   reviewRepository;
    // REMOVED: LessonBookmarkRepository - now in lesson-service
    private final CertificateRepository    certificateRepository;
    private final CouponRepository         couponRepository;

    private final UserClient userClient;
    private final CourseEventPublisher courseEventPublisher;  // ← ADDED

    // =========================================================================
    // COURSES — Admin
    // =========================================================================

    public CourseDTO.Response createCourse(CourseDTO.Request request, Integer adminId) {

        Course course = Course.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .category(request.getCategory())
                .level(Course.Level.valueOf(request.getLevel().name()))
                .price(request.getPrice())
                .status(request.getStatus() != null ? Course.Status.valueOf(request.getStatus().name()) : Course.Status.DRAFT)
                .thumbnail(request.getThumbnail())
                .prerequisites(request.getPrerequisites())
                .objectives(request.getObjectives() != null ? request.getObjectives() : new java.util.ArrayList<>())
                .tags(request.getTags() != null ? request.getTags() : new java.util.ArrayList<>())
                .enrollmentLimit(request.getEnrollmentLimit())
                .discussionsEnabled(request.getDiscussionsEnabled() != null ? request.getDiscussionsEnabled() : true)
                .reviewsEnabled(request.getReviewsEnabled() != null ? request.getReviewsEnabled() : true)
                .createdBy(adminId)
                .build();

        return toResponse(courseRepository.save(course));
    }

    public CourseDTO.Response updateCourse(Long courseId, CourseDTO.Request request) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        course.setTitle(request.getTitle());
        course.setDescription(request.getDescription());
        course.setCategory(request.getCategory());
        course.setLevel(Course.Level.valueOf(request.getLevel().name()));
        course.setPrice(request.getPrice());
        if (request.getStatus() != null) course.setStatus(Course.Status.valueOf(request.getStatus().name()));
        course.setThumbnail(request.getThumbnail());
        if (request.getPrerequisites() != null) course.setPrerequisites(request.getPrerequisites());
        if (request.getObjectives() != null) course.setObjectives(request.getObjectives());
        if (request.getTags() != null) course.setTags(request.getTags());
        if (request.getEnrollmentLimit() != null) course.setEnrollmentLimit(request.getEnrollmentLimit());
        if (request.getDiscussionsEnabled() != null) course.setDiscussionsEnabled(request.getDiscussionsEnabled());
        if (request.getReviewsEnabled() != null) course.setReviewsEnabled(request.getReviewsEnabled());

        return toResponse(courseRepository.save(course));
    }

    public void deleteCourse(Long courseId) {
        courseRepository.deleteById(courseId);
    }

    public List<CourseDTO.Response> getAllCoursesForAdmin() {
        return courseRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // =========================================================================
    // COURSES — Public
    // =========================================================================

    public List<CourseDTO.Summary> getPublishedCourses(String category) {
        return getPublishedCourses(category, null);
    }

    public List<CourseDTO.Summary> getPublishedCourses(String category, String search) {
        List<Course> courses = (category != null && !category.isBlank())
                ? courseRepository.findByCategoryAndStatus(category, Course.Status.PUBLISHED)
                : courseRepository.findByStatus(Course.Status.PUBLISHED);

        if (search != null && !search.isBlank()) {
            String q = search.toLowerCase();
            courses = courses.stream()
                    .filter(c -> c.getTitle().toLowerCase().contains(q)
                            || c.getCategory().toLowerCase().contains(q))
                    .collect(Collectors.toList());
        }
        return courses.stream().map(this::toSummary).collect(Collectors.toList());
    }

    public CourseDTO.Response getCourseById(Long courseId) {
        return toResponse(courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found")));
    }

    // =========================================================================
    // ENROLLMENT
    // =========================================================================

    @Transactional
    public void enroll(Long courseId, Integer userId) {
        if (enrollmentRepository.existsByCourseIdAndUserId(courseId, userId)) {
            throw new RuntimeException("Already enrolled in this course");
        }
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        if (course.getStatus() != Course.Status.PUBLISHED) {
            throw new RuntimeException("Course is not available");
        }

        // Verify user exists via UserClient
        UserClient.UserDTO user = userClient.getUserById(userId);
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        Enrollment enrollment = enrollmentRepository.save(Enrollment.builder()
                .course(course)
                .userId(userId)
                .status(Enrollment.Status.ACTIVE)
                .build());

        // 🚀 PUBLISH RABBITMQ EVENT
        CourseEnrolledEventDTO event = new CourseEnrolledEventDTO(
                enrollment.getId(),
                courseId,
                course.getTitle(),
                userId,
                user.getName() + " " + user.getLastName(),
                user.getEmail(),
                LocalDateTime.now().toString(),
                course.getPrice().doubleValue()
        );
        courseEventPublisher.publishCourseEnrolled(event);
    }

    // NEW: Get enrollment by course and user (for lesson-service Feign call)
    public EnrollmentDTO.Response getEnrollmentByCourseAndUser(Long courseId, Integer userId) {
        Enrollment enrollment = enrollmentRepository.findByCourseIdAndUserId(courseId, userId)
                .orElseThrow(() -> new RuntimeException("Enrollment not found"));

        // Note: totalLessons and completedLessons will be fetched from lesson-service
        // For now, return basic info - lesson-service will calculate progress
        return EnrollmentDTO.Response.builder()
                .id(enrollment.getId())
                .courseId(enrollment.getCourse().getId())
                .courseTitle(enrollment.getCourse().getTitle())
                .courseThumbnail(enrollment.getCourse().getThumbnail())
                .totalLessons(0) // Will be updated by lesson-service
                .completedLessons(0) // Will be updated by lesson-service
                .progressPercent(0) // Will be updated by lesson-service
                .enrolledAt(enrollment.getEnrolledAt())
                .build();
    }

    // =========================================================================
    // PROGRESS UPDATE (from RabbitMQ consumer)
    // =========================================================================

    /**
     * Updates a user's progress in a course when a lesson is completed.
     * Called via RabbitMQ consumer when Lesson Service sends a LessonCompletedEvent.
     *
     * @param courseId the course ID
     * @param userId the user ID
     */
    @Transactional
    public void updateProgress(Long courseId, Integer userId) {
        log.info("📊 Updating progress for user {} in course {}", userId, courseId);

        // Find the enrollment
        Enrollment enrollment = enrollmentRepository
                .findByCourseIdAndUserId(courseId, userId)
                .orElseThrow(() -> new RuntimeException("User not enrolled in course: " + courseId));

        // Since lessons are in lesson-service, we don't have the exact count here
        // The consumer just logs that progress was updated

        // Optionally update a last_activity timestamp if you have that field
        // enrollment.setLastActivity(LocalDateTime.now());
        enrollmentRepository.save(enrollment);

        log.info("✅ Progress recorded for user {} in course {}", userId, courseId);
    }

    // =========================================================================
    // PAYMENT
    // =========================================================================

    @Transactional
    public PaymentDTO.Response initiatePayment(PaymentDTO.Request request) {

        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new RuntimeException("Course not found"));

        BigDecimal amount = course.getPrice();
        Coupon appliedCoupon = null;
        BigDecimal discountAmount = BigDecimal.ZERO;

        if (request.getCouponCode() != null && !request.getCouponCode().isBlank()) {
            Coupon coupon = couponRepository.findByCodeAndActive(request.getCouponCode(), true).orElse(null);
            if (coupon != null
                    && (coupon.getCourse() == null || coupon.getCourse().getId().equals(course.getId()))
                    && (coupon.getExpiresAt() == null || coupon.getExpiresAt().isAfter(LocalDateTime.now()))
                    && (coupon.getMaxUses() == null || coupon.getTimesUsed() < coupon.getMaxUses())) {

                if (coupon.getDiscountPercent() != null) {
                    discountAmount = amount.multiply(BigDecimal.valueOf(coupon.getDiscountPercent() / 100.0));
                    amount = amount.subtract(discountAmount);
                } else if (coupon.getDiscountAmount() != null) {
                    discountAmount = coupon.getDiscountAmount();
                    amount = amount.subtract(discountAmount);
                }
                if (amount.compareTo(BigDecimal.ZERO) < 0) amount = BigDecimal.ZERO;

                appliedCoupon = coupon;
                coupon.setTimesUsed(coupon.getTimesUsed() + 1);
                couponRepository.save(coupon);
            }
        }

        // Verify user exists via UserClient
        UserClient.UserDTO user = userClient.getUserById(request.getUserId());
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        Payment payment = Payment.builder()
                .userId(request.getUserId())
                .course(course)
                .amount(amount)
                .method(Payment.Method.valueOf(request.getMethod()))
                .status(Payment.Status.COMPLETED)
                .discountAmount(discountAmount)
                .coupon(appliedCoupon)
                .completedAt(LocalDateTime.now())
                .build();
        payment = paymentRepository.save(payment);

        if (!enrollmentRepository.existsByCourseIdAndUserId(course.getId(), request.getUserId())) {
            enrollmentRepository.save(
                    Enrollment.builder()
                            .course(course)
                            .userId(request.getUserId())
                            .status(Enrollment.Status.ACTIVE)
                            .build());
        }

        return PaymentDTO.Response.builder()
                .paymentId(payment.getId().toString())
                .status(payment.getStatus().name())
                .amount(payment.getAmount().doubleValue())
                .currency(payment.getCurrency())
                .enrolledAt(payment.getCompletedAt().toString())
                .build();
    }

    // =========================================================================
    // COUPON
    // =========================================================================

    public CouponDTO.ValidateResponse validateCoupon(Long courseId, String code) {
        Coupon coupon = couponRepository.findByCodeAndActive(code, true).orElse(null);
        if (coupon == null)
            return CouponDTO.ValidateResponse.builder().valid(false).errorMessage("Coupon not found.").build();
        if (coupon.getExpiresAt() != null && coupon.getExpiresAt().isBefore(LocalDateTime.now()))
            return CouponDTO.ValidateResponse.builder().valid(false).errorMessage("Coupon has expired.").build();
        if (coupon.getMaxUses() != null && coupon.getTimesUsed() >= coupon.getMaxUses())
            return CouponDTO.ValidateResponse.builder().valid(false).errorMessage("Usage limit reached.").build();
        if (coupon.getCourse() != null && !coupon.getCourse().getId().equals(courseId))
            return CouponDTO.ValidateResponse.builder().valid(false).errorMessage("Not valid for this course.").build();

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        BigDecimal original = course.getPrice();
        BigDecimal discount = BigDecimal.ZERO;
        if (coupon.getDiscountPercent() != null)
            discount = original.multiply(BigDecimal.valueOf(coupon.getDiscountPercent() / 100.0));
        else if (coupon.getDiscountAmount() != null)
            discount = coupon.getDiscountAmount();

        BigDecimal finalPrice = original.subtract(discount);
        if (finalPrice.compareTo(BigDecimal.ZERO) < 0) finalPrice = BigDecimal.ZERO;

        return CouponDTO.ValidateResponse.builder()
                .valid(true)
                .discountAmount(discount.doubleValue())
                .finalPrice(finalPrice.doubleValue())
                .build();
    }

    // =========================================================================
    // REVIEWS
    // =========================================================================

    public List<ReviewDTO.Response> getCourseReviews(Long courseId) {
        return reviewRepository.findByCourseId(courseId).stream().map(r -> {
            UserClient.UserDTO user = userClient.getUserById(r.getUserId());
            String userName = (user != null) ? user.getName() + " " + user.getLastName() : "Unknown User";

            return ReviewDTO.Response.builder()
                    .id(r.getId())
                    .userId(r.getUserId())
                    .userName(userName)
                    .courseId(courseId)
                    .rating(r.getRating())
                    .comment(r.getComment())
                    .createdAt(r.getCreatedAt().toString())
                    .helpful(r.getHelpfulCount())
                    .build();
        }).collect(Collectors.toList());
    }

    @Transactional
    public ReviewDTO.Response submitReview(ReviewDTO.Request request) {
        UserClient.UserDTO user = userClient.getUserById(request.getUserId());
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new RuntimeException("Course not found"));

        CourseReview review = reviewRepository
                .findByCourseIdAndUserId(course.getId(), request.getUserId())
                .orElse(CourseReview.builder()
                        .course(course)
                        .userId(request.getUserId())
                        .build());

        review.setRating(request.getRating());
        review.setComment(request.getComment());
        review = reviewRepository.save(review);

        return ReviewDTO.Response.builder()
                .id(review.getId())
                .userId(request.getUserId())
                .userName(user.getName() + " " + user.getLastName())
                .courseId(course.getId())
                .rating(review.getRating())
                .comment(review.getComment())
                .createdAt(review.getCreatedAt().toString())
                .helpful(review.getHelpfulCount())
                .build();
    }

    @Transactional
    public void markReviewHelpful(Long reviewId) {
        CourseReview review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));
        review.setHelpfulCount(review.getHelpfulCount() + 1);
        reviewRepository.save(review);
    }

    // =========================================================================
    // BOOKMARKS - REMOVED (moved to lesson-service)
    // =========================================================================

    // =========================================================================
    // NOTES - REMOVED (moved to lesson-service)
    // =========================================================================

    // =========================================================================
    // CERTIFICATES
    // =========================================================================

    public List<CertificateDTO.Response> getUserCertificates(Integer userId) {
        UserClient.UserDTO user = userClient.getUserById(userId);
        String userName = (user != null) ? user.getName() + " " + user.getLastName() : "Unknown User";

        return certificateRepository.findByUserId(userId).stream().map(c ->
                CertificateDTO.Response.builder()
                        .id(c.getId())
                        .userId(userId)
                        .userName(userName)
                        .courseId(c.getCourse().getId())
                        .courseTitle(c.getCourse().getTitle())
                        .issuedAt(c.getIssuedAt().toString())
                        .url(c.getPdfUrl() != null ? c.getPdfUrl() : "/api/certificates/" + c.getId() + "/download")
                        .verificationCode(c.getVerificationCode())
                        .build()
        ).collect(Collectors.toList());
    }

    public CertificateDTO.Response verifyCertificate(String code) {
        Certificate c = certificateRepository.findByVerificationCode(code)
                .orElseThrow(() -> new RuntimeException("Certificate not found"));

        UserClient.UserDTO user = userClient.getUserById(c.getUserId());
        String userName = (user != null) ? user.getName() + " " + user.getLastName() : "Unknown User";

        return CertificateDTO.Response.builder()
                .id(c.getId())
                .userId(c.getUserId())
                .userName(userName)
                .courseId(c.getCourse().getId())
                .courseTitle(c.getCourse().getTitle())
                .issuedAt(c.getIssuedAt().toString())
                .url(c.getPdfUrl() != null ? c.getPdfUrl() : "/api/certificates/" + c.getId() + "/download")
                .verificationCode(c.getVerificationCode())
                .build();
    }

    private boolean issueCertificateIfNotExists(Enrollment enrollment) {
        if (certificateRepository.findByCourseIdAndUserId(
                enrollment.getCourse().getId(), enrollment.getUserId()).isPresent()) {
            return false;
        }

        certificateRepository.save(Certificate.builder()
                .course(enrollment.getCourse())
                .userId(enrollment.getUserId())
                .build());
        return true;
    }

    // =========================================================================
    // ADMIN — PAYMENTS (Lesson history moved to lesson-service)
    // =========================================================================

    public List<Payment> getAdminPayments(Long courseId, Integer userId) {
        if (courseId != null && userId != null)
            return paymentRepository.findByCourseIdAndUserId(courseId, userId);
        if (courseId != null) return paymentRepository.findByCourseId(courseId);
        if (userId != null) return paymentRepository.findByUserId(userId);
        return paymentRepository.findAll();
    }

    // =========================================================================
    // MAPPERS
    // =========================================================================

    private CourseDTO.Response toResponse(Course c) {
        return CourseDTO.Response.builder()
                .id(c.getId())
                .title(c.getTitle())
                .description(c.getDescription())
                .category(c.getCategory())
                .level(c.getLevel())
                .price(c.getPrice())
                .status(c.getStatus())
                .thumbnail(c.getThumbnail())
                .lessonCount(0) // Now fetched from lesson-service via Feign
                .enrollmentCount((int) enrollmentRepository.countByCourseId(c.getId()))
                .createdAt(c.getCreatedAt())
                .prerequisites(c.getPrerequisites())
                .objectives(c.getObjectives())
                .tags(c.getTags())
                .build();
    }

    private CourseDTO.Summary toSummary(Course c) {
        return CourseDTO.Summary.builder()
                .id(c.getId())
                .title(c.getTitle())
                .category(c.getCategory())
                .level(c.getLevel())
                .price(c.getPrice())
                .status(c.getStatus())
                .thumbnail(c.getThumbnail())
                .lessonCount(0) // Now fetched from lesson-service via Feign
                .enrollmentCount((int) enrollmentRepository.countByCourseId(c.getId()))
                .build();
    }
}