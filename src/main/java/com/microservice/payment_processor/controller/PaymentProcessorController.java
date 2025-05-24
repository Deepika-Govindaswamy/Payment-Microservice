package com.microservice.payment_processor.controller;

import com.microservice.payment_processor.dto.PaymentResponseDto;
import com.microservice.payment_processor.service.KafkaPaymentConsumer;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payment-processor")
public class PaymentProcessorController {

    @Value("${stripe.secret-key}")
    private String apiKey;

    private final KafkaPaymentConsumer kafkaPaymentConsumer;

    @GetMapping ("/getPaymentStatus")
    public ResponseEntity<PaymentResponseDto> getPaymentStatus (@RequestParam String transactionId) {

        return kafkaPaymentConsumer.getPaymentStatus(transactionId);
    }

}
