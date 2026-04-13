package com.transport.management.repository;

import java.util.List;
import java.util.Optional;

import com.transport.management.entity.Payment;
import com.transport.management.entity.VehicleOwner;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, String> {
    List<Payment> findByCustomerId(String customerId);
    List<Payment> findByDriverId(String driverId);

    Optional<Payment> findByPaymentId(String paymentId);
    List<Payment> findByVehicleOwner(VehicleOwner vehicleOwner);

}
