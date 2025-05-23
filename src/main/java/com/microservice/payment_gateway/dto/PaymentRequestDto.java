package com.microservice.payment_gateway.dto;

import lombok.Builder;

@Builder
public record PaymentRequestDto (Long amount, String paymentMethodId, String idempotencyKey,
                                 String customerEmail, String currency) {}
