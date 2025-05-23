package com.microservice.payment_gateway.webclient;

import com.microservice.payment_gateway.dto.PaymentResponseDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;

public interface PaymentProcessorClient {

    @GetExchange("/api/payment-processor/getPaymentStatus")
    ResponseEntity<PaymentResponseDto> getPaymentStatus (@RequestParam String transactionId);
}
