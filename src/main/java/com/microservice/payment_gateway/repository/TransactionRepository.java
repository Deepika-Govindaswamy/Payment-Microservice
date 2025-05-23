package com.microservice.payment_gateway.repository;

import com.microservice.payment_gateway.entity.PaymentTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<PaymentTransaction, String> {

    boolean existsByIdempotencyKey (String idempotencyKey);
}
