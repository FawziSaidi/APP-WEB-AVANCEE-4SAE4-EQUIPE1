package com.esprit.courseservice.Repositories.Cours;

import com.esprit.courseservice.Entities.Cours.CourseReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseReviewRepository extends JpaRepository<CourseReview, Long> {
    List<CourseReview> findByCourseId(Long courseId);
    Optional<CourseReview> findByCourseIdAndUserId(Long courseId, Integer userId);
}
