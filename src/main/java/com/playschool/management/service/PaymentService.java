package com.playschool.management.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.playschool.management.dto.request.PaymentRequestDTO;
import com.playschool.management.entity.Customer;
import com.playschool.management.entity.Driver;
import com.playschool.management.entity.Payment;
import com.playschool.management.entity.VehicleOwner;
import com.playschool.management.repository.CustomerRepository;
import com.playschool.management.repository.DriverRepository;
import com.playschool.management.repository.PaymentRepository;
import com.playschool.management.repository.VehicleOwnerRepository;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final CustomerRepository customerRepository;
    private final DriverRepository driverRepository;
    private final VehicleOwnerRepository ownerRepository;

    public PaymentService(PaymentRepository paymentRepository,
                          CustomerRepository customerRepository,
                          DriverRepository driverRepository,
                          VehicleOwnerRepository ownerRepository) {
        this.paymentRepository = paymentRepository;
        this.customerRepository = customerRepository;
        this.driverRepository = driverRepository;
        this.ownerRepository = ownerRepository;
    }

    // 1️⃣ Create new payment from DTO
    public Payment createPayment(PaymentRequestDTO request) {
        Customer customer = customerRepository.findByUserId(request.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        Driver driver = driverRepository.findByUserId(request.getDriverId())
                .orElseThrow(() -> new RuntimeException("Driver not found"));
        VehicleOwner owner = ownerRepository.findByUserId(request.getOwnerId())
                .orElseThrow(() -> new RuntimeException("Owner not found"));

        Payment payment = new Payment();
        payment.setCustomer(customer);
        payment.setDriver(driver);
        payment.setVehicleOwner(owner);
        payment.setPaymentAmount(request.getPaymentAmount());
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setBaseFare(request.getBaseFare());
        payment.setGstAmount(request.getGstAmount());
        payment.setServiceCharge(request.getServiceCharge());
        payment.setInsuranceCharge(request.getInsuranceCharge());
        payment.setCancellationCharge(request.getCancellationCharge());
        payment.setWalletUsed(request.getWalletUsed());
        payment.setWalletAmount(request.getWalletAmount());
        payment.setReferenceId(request.getReferenceId());
        payment.setRemarks(request.getRemarks());
        payment.setPaymentStatus(Payment.PaymentStatus.PENDING); // Default status

        return paymentRepository.save(payment);
    }

    // 2️⃣ Get payment by ID
    public Payment getPayment(String paymentId) {
        return paymentRepository.findByPaymentId(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
    }

    // 3️⃣ List all payments
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    // 4️⃣ Get payments for a specific customer
    public List<Payment> getPaymentsByCustomer(String customerId) {
        return paymentRepository.findByCustomerId(customerId);
    }

    // 5️⃣ Update payment status
    public Payment updatePaymentStatus(String paymentId, String status) {
        Payment payment = getPayment(paymentId);
        payment.setPaymentStatus(Payment.PaymentStatus.valueOf(status.toUpperCase()));
        return paymentRepository.save(payment);
    }

    // Release payment to driver
    public Payment releasePaymentToDriver(String paymentId) {
        Payment payment = paymentRepository.findByPaymentId(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
        payment.setPaidToDriver(true);
        payment.setPaymentStatus(Payment.PaymentStatus.COMPLETED);
        payment.setReleaseDatetime(java.time.LocalDateTime.now());
        return paymentRepository.save(payment);
    }

}
