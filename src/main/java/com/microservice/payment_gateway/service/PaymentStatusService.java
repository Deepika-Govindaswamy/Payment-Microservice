package com.microservice.payment_gateway.service;

import com.microservice.payment_gateway.dto.PaymentResponseDto;
import com.microservice.payment_gateway.webclient.PaymentProcessorClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentStatusService {

    private final PaymentProcessorClient paymentProcessorClient;

    public ResponseEntity<PaymentResponseDto> retrievePaymentStatus(String transactionId){
        return paymentProcessorClient.getPaymentStatus(transactionId);
    }
}
