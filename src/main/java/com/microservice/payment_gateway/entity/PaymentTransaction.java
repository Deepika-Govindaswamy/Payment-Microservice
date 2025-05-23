package com.microservice.payment_gateway.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table (name = "payments_table")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentTransaction {

    @Id
    private String transactionId;

    @NotBlank
    @Email
    private String customerEmail;

    private String stripePaymentIntentId;

    private Long amount;

    private String currency;

    private String paymentMethodId;

    private String idempotencyKey;

    private String status;

    private String failureReason;

    private Instant updatedAt;

    private Instant createdAt;
}
