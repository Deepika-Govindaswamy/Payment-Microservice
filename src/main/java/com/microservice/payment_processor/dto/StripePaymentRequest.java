package com.microservice.payment_processor.dto;

import lombok.Builder;

@Builder
public record StripePaymentRequest ( Long amount, String currency, String transactionId,
                                    String paymentMethodId, Boolean confirmationCode) {}
