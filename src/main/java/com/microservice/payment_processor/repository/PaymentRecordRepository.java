package com.microservice.payment_processor.repository;

import com.microservice.payment_processor.entity.PaymentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRecordRepository extends JpaRepository<PaymentTransaction, Long> {

    PaymentTransaction findByTransactionId (String stripePaymentRecord);
}
