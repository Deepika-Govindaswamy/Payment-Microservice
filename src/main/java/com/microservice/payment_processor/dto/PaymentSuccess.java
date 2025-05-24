package com.microservice.payment_processor.dto;


public record PaymentSuccess (String transactionId, String stripePaymentIntentId) {}
