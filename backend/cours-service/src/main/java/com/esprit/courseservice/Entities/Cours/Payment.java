package com.esprit.courseservice.Entities.Cours;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @JoinColumn(name = "user_id", nullable = false)
    private Integer userId;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false, length = 3)
    @Builder.Default
    private String currency = "USD";

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private Status status = Status.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Method method;

    /** Optional coupon applied at checkout */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id")
    private Coupon coupon;

    /** Amount actually discounted (0 if no coupon) */
    @Column(name = "discount_amount", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal discountAmount = BigDecimal.ZERO;

    /**
     * External payment-provider reference (Stripe charge ID, PayPal order ID…).
     * Null for bank-transfer payments awaiting manual confirmation.
     */
    @Column(name = "provider_reference", length = 255)
    private String providerReference;

    /** URL to the payment receipt (set once COMPLETED) */
    @Column(name = "receipt_url", length = 500)
    private String receiptUrl;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    /** Error message from the payment provider when status = FAILED */
    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    // ── Lifecycle ──────────────────────────────────────────────────────────────

    @PrePersist
    void prePersist() {
        if (createdAt == null) createdAt = LocalDateTime.now();
    }

    // ── Enums ──────────────────────────────────────────────────────────────────

    public enum Status {
        PENDING,
        COMPLETED,
        FAILED,
        REFUNDED
    }

    public enum Method {
        CARD,
        PAYPAL,
        BANK_TRANSFER
    }
}
