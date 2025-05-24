package com.microservice.payment_processor.dto;

import lombok.Builder;

@Builder
public record PaymentResponseDto (String transactionId, String status) {}
