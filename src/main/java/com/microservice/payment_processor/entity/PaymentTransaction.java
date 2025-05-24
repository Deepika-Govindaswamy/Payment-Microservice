package com.microservice.payment_processor.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "payments_table")
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
