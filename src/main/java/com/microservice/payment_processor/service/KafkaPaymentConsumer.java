package com.microservice.payment_processor.service;

import com.microservice.payment_gateway.dto.KafkaPaymentDto;
import com.microservice.payment_processor.dto.PaymentFailed;
import com.microservice.payment_processor.dto.PaymentResponseDto;
import com.microservice.payment_processor.dto.PaymentSuccess;
import com.microservice.payment_processor.entity.PaymentTransaction;
import com.microservice.payment_processor.repository.PaymentRecordRepository;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.PaymentIntent;
import com.stripe.model.PaymentMethod;
import com.stripe.net.RequestOptions;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.PaymentMethodAttachParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaPaymentConsumer {

    private final PaymentRecordRepository paymentRecordRepository;

    private final KafkaTemplate <String, PaymentSuccess> kafkaSuccessTemplate;

    private final KafkaTemplate <String, PaymentFailed> kafkaFailedTemplate;

    @Value("${stripe.secret-key}")
    private String stripeApiKey;

    @Transactional
    @KafkaListener(topics = "payments.initiate")
    public void consumePaymentInfo (KafkaPaymentDto paymentDetails) {

        try {

            Stripe.apiKey = stripeApiKey;

            log.info("Consumed payment details: {}", paymentDetails.paymentMethodId());

            PaymentTransaction currentTransaction = paymentRecordRepository.findByTransactionId(paymentDetails.transactionId());

            if (currentTransaction == null) {
                throw new IllegalArgumentException("No such transaction: " + paymentDetails.transactionId());
            }

            currentTransaction.setStatus("Pending");
            currentTransaction.setUpdatedAt(Instant.now());


            // Create an intent to initiate payment
            PaymentIntent intent = createPaymentIntent(paymentDetails, currentTransaction);

            if (intent == null) {

                return;
            }

            // update the final status of payment in repository

            updateTransactionStatus (intent, currentTransaction);

            paymentRecordRepository.save(currentTransaction);

        }
        catch (Exception e) {

            e.printStackTrace();
        }


    }




//    private KafkaPaymentDto savePaymentRecord (KafkaPaymentDto paymentDetails) {
//
//        PaymentTransaction existingPaymentRecord = paymentRecordRepository.findByTransactionId(paymentDetails.transactionId());
//
//        try {
//
//            PaymentTransaction paymentTransaction = existingPaymentRecord;
//            paymentTransaction.setStatus("Pending");
//            paymentTransaction.setUpdatedAt(Instant.now());
//            paymentRecordRepository.save(paymentTransaction);
//            log.info("Saved payment details: {}", paymentTransaction);
//         }
//
//        catch (Exception e) {
//           e.printStackTrace();
//        }
//
//
//        KafkaPaymentDto updatedPaymentRecord = KafkaPaymentDto.builder()
//                .amount(paymentDetails.amount())
//                .currency(paymentDetails.currency())
//                .paymentMethodId(paymentDetails.paymentMethodId())
//                .idempotencyKey(paymentDetails.idempotencyKey())
//                .customerEmail(paymentDetails.customerEmail())
//                .status("Pending")
//                .transactionId(paymentDetails.transactionId())
//                .build();
//
//        return updatedPaymentRecord;
//
//    }

    private PaymentIntent createPaymentIntent (KafkaPaymentDto paymentDetails, PaymentTransaction currentTransaction) {

        PaymentIntent intent = null;

        try{

            Customer customer = Customer.create(CustomerCreateParams.builder()
                    .setEmail(paymentDetails.customerEmail())
                    .build());

            log.info("Creating Customer: {}", customer);

            PaymentMethod paymentMethod = PaymentMethod.retrieve(paymentDetails.paymentMethodId());
            paymentMethod.attach(PaymentMethodAttachParams.builder()
                    .setCustomer(customer.getId())
                    .build());

            log.info("Creating PaymentMethod: {}", paymentMethod);

            log.info("Creating payment intent: {}", paymentDetails);

            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount(paymentDetails.amount())
                    .setCurrency(paymentDetails.currency())
                    .setCustomer(customer.getId())
                    .setPaymentMethod(paymentDetails.paymentMethodId())
                    .setConfirm(true)
                    .setAutomaticPaymentMethods(
                            PaymentIntentCreateParams.AutomaticPaymentMethods.builder()
                                    .setEnabled(true)
                                    .setAllowRedirects(PaymentIntentCreateParams.AutomaticPaymentMethods.AllowRedirects.NEVER)
                                    .build()
                    )
                    .build();

            intent = PaymentIntent.create(params, RequestOptions.builder()
                    .setIdempotencyKey(paymentDetails.transactionId())
                    .build());

            log.info("Created payment intent: {}", paymentDetails);
        }
        catch (StripeException e) {

            currentTransaction.setUpdatedAt(Instant.now());
            currentTransaction.setStatus("FAILED");
            currentTransaction.setFailureReason(e.getStripeError().getMessage());
//            paymentRecordRepository.save(failedTransaction);

            PaymentFailed failedPayment = PaymentFailed.builder()
                    .transactionId(paymentDetails.transactionId())
                    .failureReason(e.getStripeError().getMessage())
                    .build();

            kafkaFailedTemplate.send("payments.failed", failedPayment);
        }

        return intent;
    }



    private void updateTransactionStatus (PaymentIntent intent, PaymentTransaction currentTransaction) {


        switch (intent.getStatus()) {

            case "succeeded" -> {

                currentTransaction.setStripePaymentIntentId(intent.getId());
                currentTransaction.setUpdatedAt(Instant.now());
                currentTransaction.setStatus("SUCCESS");
//                paymentRecordRepository.save(currentTransaction);

                // pushing successful transaction to kafka
                PaymentSuccess successfulPayment = PaymentSuccess.builder()
                        .transactionId(currentTransaction.getTransactionId())
                        .stripePaymentIntentId(intent.getId())
                        .build();

                kafkaSuccessTemplate.send("payments.success", successfulPayment);
            }

            case "requires_action", "requires_payment_method" -> {

                currentTransaction.setStripePaymentIntentId(intent.getId());
                currentTransaction.setUpdatedAt(Instant.now());
                currentTransaction.setStatus("FAILED");
//                paymentRecordRepository.save(currentTransaction);

                // pushing failed transaction to kafka
                PaymentFailed failedPayment = PaymentFailed.builder()
                        .transactionId(currentTransaction.getTransactionId())
                        .failureReason(intent.getStatus())
                        .build();

                kafkaFailedTemplate.send("payments.failed", failedPayment);
            }

        }

        log.info("Transaction {} marked {}", currentTransaction.getTransactionId(), intent.getStatus());
    }

    public ResponseEntity<PaymentResponseDto> getPaymentStatus (String transactionId) {
        PaymentTransaction paymentRecord = paymentRecordRepository.findByTransactionId(transactionId);
        if (paymentRecord == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(new PaymentResponseDto(paymentRecord.getTransactionId(), paymentRecord.getStatus()));
    }

}
