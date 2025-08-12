package com.playschool.management.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.playschool.management.dto.request.PaymentRequestDTO;
import com.playschool.management.entity.Payment;
import com.playschool.management.service.PaymentService;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;
    public PaymentController(PaymentService paymentService) { // constructor injection
        this.paymentService = paymentService;
    }

    // Create a new payment
    @PostMapping
    public ResponseEntity<Payment> createPayment(@RequestBody PaymentRequestDTO dto) {
        Payment createdPayment = paymentService.createPayment(dto);
        return ResponseEntity.ok(createdPayment);
    }

    // Get all payments for a customer
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<Payment>> getPaymentsByCustomer(@PathVariable String customerId) {
        List<Payment> payments = paymentService.getPaymentsByCustomer(customerId);
        return ResponseEntity.ok(payments);
    }

    // Release payment to driver
    @PostMapping("/{paymentId}/release")
    public ResponseEntity<Payment> releasePaymentToDriver(@PathVariable String paymentId) {
        Payment updatedPayment = paymentService.releasePaymentToDriver(paymentId);
        return ResponseEntity.ok(updatedPayment);
    }
}
