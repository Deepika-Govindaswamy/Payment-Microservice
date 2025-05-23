package com.microservice.payment_gateway.service;

import com.microservice.payment_gateway.dto.KafkaPaymentDto;
import com.microservice.payment_gateway.dto.PaymentRequestDto;
import com.microservice.payment_gateway.dto.PaymentResponseDto;
import com.microservice.payment_gateway.entity.PaymentTransaction;
import com.microservice.payment_gateway.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.Instant;
import java.util.UUID;


@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaPaymentRequestProducer {

    private final KafkaTemplate<String, KafkaPaymentDto> kafkaTemplate;

    private final TransactionRepository transactionRepository;


    @Transactional
    public PaymentResponseDto initiatePaymentRequest (PaymentRequestDto paymentRequestDto){

        String transactionId = UUID.randomUUID().toString();

        // creating a dto for publishing message into kafka topic "payments.initiate"
        KafkaPaymentDto kafkaPaymentInitaited = KafkaPaymentDto.builder()
                .amount(paymentRequestDto.amount())
                .idempotencyKey(paymentRequestDto.idempotencyKey())
                .customerEmail(paymentRequestDto.customerEmail())
                .status("Initiated")
                .currency(paymentRequestDto.currency())
                .paymentMethodId(paymentRequestDto.paymentMethodId())
                .transactionId(transactionId)
                .build();


        // Save transaction in database
        PaymentTransaction newPaymentTransaction = PaymentTransaction.builder()
                .transactionId(transactionId)
                .customerEmail(paymentRequestDto.customerEmail())
                .amount(paymentRequestDto.amount())
                .currency(paymentRequestDto.currency())
                .idempotencyKey(paymentRequestDto.idempotencyKey())
                .paymentMethodId(paymentRequestDto.paymentMethodId())
                .status("Initiated")
                .createdAt(Instant.now())
                .build();

        transactionRepository.save(newPaymentTransaction);


        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                log.info("Started - initial payment request {}", kafkaPaymentInitaited);
                kafkaTemplate.send("payments.initiate", kafkaPaymentInitaited);
                log.info("Completed - initial payment request {}", kafkaPaymentInitaited);
            }
        });


        return new PaymentResponseDto (transactionId, kafkaPaymentInitaited.status());

    }

}