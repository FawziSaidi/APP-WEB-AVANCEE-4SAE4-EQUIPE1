package com.esprit.courseservice.Repositories.Cours;

import com.esprit.courseservice.Entities.Cours.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {
    Optional<Coupon> findByCodeAndActive(String code, Boolean active);
}
