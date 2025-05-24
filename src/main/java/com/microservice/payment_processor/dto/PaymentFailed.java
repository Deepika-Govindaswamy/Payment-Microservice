package com.microservice.payment_processor.dto;

import lombok.Builder;

@Builder
public record PaymentFailed (String transactionId, String failureReason) {}
