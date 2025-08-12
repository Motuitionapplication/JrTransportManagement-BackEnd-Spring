package com.playschool.management.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.playschool.management.dto.request.PaymentRequestDTO;
import com.playschool.management.dto.response.PaymentResponseDTO;
import com.playschool.management.entity.Booking.PaymentStatus;
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
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        Driver driver = driverRepository.findById(request.getDriverId())
                .orElseThrow(() -> new RuntimeException("Driver not found"));
        VehicleOwner owner = ownerRepository.findById(request.getOwnerId())
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
    public List<PaymentResponseDTO> getPaymentsByCustomerId(String customerId) {
        // 1. Fetch the raw data from the database
        List<Payment> payments = paymentRepository.findByCustomerId(customerId);

        // 2. Convert each Payment entity into a PaymentResponseDTO
        return payments.stream()
                       .map(this::convertToPaymentResponseDto)
                       .collect(Collectors.toList());
    }
    private PaymentResponseDTO convertToPaymentResponseDto(Payment payment) {
        
        // 1. Get the Enum, convert it to a String with .name(), then make it uppercase.
        String statusString = payment.getPaymentStatus().name().toUpperCase();

        // 2. Convert the String to the DTO's PaymentStatus enum.
        PaymentStatus statusEnum = PaymentStatus.valueOf(statusString);

        // 3. Now call the constructor with the correctly typed enum.
        return new PaymentResponseDTO(
            payment.getPaymentId(),
            payment.getReferenceId(),
            payment.getPaymentAmount(),
            statusEnum, 
            payment.getCreatedAt()
        );
    }

    // 5️⃣ Update payment status
    public Payment updatePaymentStatus(String paymentId, String status) {
        Payment payment = getPayment(paymentId);
        payment.setPaymentStatus(Payment.PaymentStatus.valueOf(status.toUpperCase()));
        return paymentRepository.save(payment);
    }
    
    public List<PaymentResponseDTO> getPaymentsByDriverId(String driverId) {
        // 1. Fetch the raw data from the database
        List<Payment> payments = paymentRepository.findByDriverId(driverId);

        // 2. Convert each Payment entity into a PaymentResponseDTO
        return payments.stream()
                       .map(this::convertToPaymentResponseDto)
                       .collect(Collectors.toList());
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
