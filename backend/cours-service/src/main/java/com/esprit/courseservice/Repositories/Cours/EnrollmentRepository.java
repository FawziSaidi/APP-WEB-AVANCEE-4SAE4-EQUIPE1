package com.esprit.courseservice.Repositories.Cours;

import com.esprit.courseservice.Entities.Cours.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    List<Enrollment> findByUserId(Integer userId);

    Optional<Enrollment> findByCourseIdAndUserId(Long courseId, Integer userId);

    boolean existsByCourseIdAndUserId(Long courseId, Integer userId);

    long countByCourseId(Long courseId);
}