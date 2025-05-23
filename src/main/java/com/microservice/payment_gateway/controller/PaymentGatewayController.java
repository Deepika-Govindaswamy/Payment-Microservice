package com.microservice.payment_gateway.controller;


import com.microservice.payment_gateway.dto.PaymentRequestDto;
import com.microservice.payment_gateway.dto.PaymentResponseDto;
import com.microservice.payment_gateway.service.KafkaPaymentRequestProducer;
import com.microservice.payment_gateway.service.PaymentStatusService;
import com.microservice.payment_gateway.service.RequestValidationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/payment-gateway")
@RequiredArgsConstructor
public class PaymentGatewayController {

    private final KafkaPaymentRequestProducer kafkaPaymentRequestProducer;

    private final RequestValidationService requestValidationService;

    private final PaymentStatusService paymentStatusService;

    @PostMapping("/initiate-payment")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity<PaymentResponseDto> initiateTransaction (@RequestBody PaymentRequestDto paymentRequestDto){

        log.info("details: {}", paymentRequestDto);
        String validationResponse = requestValidationService.validateRequest(paymentRequestDto);

        if (!validationResponse.equals("Valid")) {
            var responseBody = new PaymentResponseDto(null, "Invalid Request: " + validationResponse);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseBody);
        }

        PaymentResponseDto paymentInitialStatus = kafkaPaymentRequestProducer.initiatePaymentRequest(paymentRequestDto);

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(paymentInitialStatus);
    }


    @GetMapping ("/getTransactionStatus")
    public ResponseEntity<PaymentResponseDto> getTransactionStatus(@RequestParam("transactionId") String transactionId){
        return paymentStatusService.retrievePaymentStatus(transactionId);
    }
}
