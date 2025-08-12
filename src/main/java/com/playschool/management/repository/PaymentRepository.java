package com.playschool.management.repository;

import java.util.List;
import java.util.Optional;

import com.playschool.management.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, String> {
    List<Payment> findByCustomerId(String customerId);
    List<Payment> findByDriverId(String driverId);

    Optional<Payment> findByPaymentId(String paymentId);
}