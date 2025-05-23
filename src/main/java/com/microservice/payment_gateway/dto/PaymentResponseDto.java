package com.microservice.payment_gateway.dto;

import lombok.Builder;

@Builder
public record PaymentResponseDto (String transactionId, String status) {}
