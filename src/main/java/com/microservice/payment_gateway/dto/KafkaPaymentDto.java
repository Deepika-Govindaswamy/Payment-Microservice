package com.microservice.payment_gateway.dto;

import lombok.Builder;

@Builder
public record KafkaPaymentDto(Long amount, String paymentMethodId, String idempotencyKey, String currency,
                              String customerEmail, String status, String transactionId) {
}
