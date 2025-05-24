package com.microservice.payment_processor.dto;


public record PaymentFailed (String transactionId, String failureReason) {}
