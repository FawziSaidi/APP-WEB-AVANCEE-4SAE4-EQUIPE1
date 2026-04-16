package com.esprit.courseservice.Repositories.Cours;

import com.esprit.courseservice.Entities.Cours.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByCourseId(Long courseId);
    List<Payment> findByUserId(Integer userId);
    List<Payment> findByCourseIdAndUserId(Long courseId, Integer userId);
}
