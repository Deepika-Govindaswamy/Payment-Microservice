package com.microservice.payment_processor.dto;

import lombok.Builder;

@Builder
public record PaymentSuccess (String transactionId, String stripePaymentIntentId) {}
