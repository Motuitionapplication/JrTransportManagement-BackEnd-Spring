package com.playschool.management.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.playschool.management.dto.request.BookingRequest;
import com.playschool.management.entity.Booking;
import com.playschool.management.entity.Customer;
import com.playschool.management.repository.BookingRepository;
import com.playschool.management.repository.CustomerRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;


@Service
public class BookingService {

	
	@Autowired
	private BookingRepository bookingrepo;
	
	@Autowired
    private CustomerRepository customerRepository;

	 @Transactional
	    public Booking newBooking(String customerId, BookingRequest bookingRequest) {
	        Customer customer = customerRepository.findById(customerId)
	                .orElseThrow(() -> new EntityNotFoundException("Customer not found with ID: " + customerId));

	        Booking booking = new Booking();

	        booking.setCustomerId(customerId);

	        booking.setBookingNumber(generateUniqueBookingNumber());

	        booking.setPickupAddress(mapLocationToAddress(bookingRequest.getPickupLocation()));
	        booking.setDeliveryAddress(mapLocationToAddress(bookingRequest.getDropoffLocation()));

	        booking.setPickupContact(mapContactPerson(
	                bookingRequest.getContactName(),
	                bookingRequest.getContactPhone(),
	                bookingRequest.getContactEmail()
	        ));
	        booking.setDeliveryContact(mapContactPerson(
	                bookingRequest.getContactName(),
	                bookingRequest.getContactPhone(),
	                bookingRequest.getContactEmail()
	        ));

	        booking.setScheduledPickupDate(mapToLocalDateTime(
	                bookingRequest.getPickupDate(),
	                bookingRequest.getPickupTime()
	        ));
	        booking.setScheduledPickupTime(bookingRequest.getPickupTime().toString());

	        booking.setCargo(mapGoodsDetailsToCargo(bookingRequest));

	        booking.setVehicleType(bookingRequest.getSelectedVehicleType());
	        booking.setVehicleName(bookingRequest.getVehicleName());
	        booking.setVehicleCapacity(bookingRequest.getVehicleCapacity());
	        booking.setVehicleId(bookingRequest.getVehicleId());

	        booking.setPricing(mapPricingDetails(bookingRequest));

	        booking.setPayment(mapPaymentDetails(bookingRequest));

	        if (bookingRequest.getEstimatedDistance() != null) {
	            booking.setTotalDistance(bookingRequest.getEstimatedDistance());
	        }
	        if (bookingRequest.getEstimatedDuration() != null) {
	            booking.setEstimatedDuration(bookingRequest.getEstimatedDuration());
	        }

	        booking.setBookingNotes(bookingRequest.getAdditionalNotes());

	        booking.setStatus(Booking.BookingStatus.PENDING);

	        booking.setCustomerTermsAccepted(true);
	        booking.setCustomerTermsAcceptedAt(LocalDateTime.now());

	        Booking savedBooking = bookingrepo.save(booking);

	        customer.getBookingHistory().add(savedBooking.getId());
	        customerRepository.save(customer);

	        return savedBooking;
	    }
	    private Booking.BookingAddress mapLocationToAddress(String location) {
	        Booking.BookingAddress address = new Booking.BookingAddress();
	        
	        if (location != null && !location.trim().isEmpty()) {
	            String[] parts = location.split(",");
	            
	            if (parts.length >= 1) {
	                address.setStreet(parts[0].trim());
	            }
	            if (parts.length >= 2) {
	                address.setCity(parts[1].trim());
	            }
	            if (parts.length >= 3) {
	                String lastPart = parts[2].trim();
	                String[] stateAndPin = lastPart.split("\\s+");
	                
	                if (stateAndPin.length >= 1) {
	                    address.setState(stateAndPin[0].trim());
	                }
	                if (stateAndPin.length >= 2) {
	                    address.setPincode(stateAndPin[stateAndPin.length - 1].trim());
	                }
	            }
	            
	            // Set default country
	            address.setCountry("India");
	        }
	        
	        return address;
	    }

	    private Booking.ContactPerson mapContactPerson(String name, String phone, String email) {
	        Booking.ContactPerson contact = new Booking.ContactPerson();
	        contact.setName(name);
	        contact.setPhoneNumber(phone);
	        contact.setEmail(email);
	        return contact;
	    }

	 
	    private LocalDateTime mapToLocalDateTime(LocalDate date, LocalTime time) {
	        if (date != null && time != null) {
	            return LocalDateTime.of(date, time);
	        }
	        return null;
	    }

	    /**
	     * Maps goods details from BookingRequest to CargoDetails entity
	     */
	    private Booking.CargoDetails mapGoodsDetailsToCargo(BookingRequest bookingRequest) {
	        Booking.CargoDetails cargo = new Booking.CargoDetails();
	        
	        // Map basic cargo information
	        cargo.setDescription(bookingRequest.getGoodsDescription());
	        cargo.setGoodsCategory(bookingRequest.getGoodsType());
	        cargo.setWeight(bookingRequest.getGoodsWeight());
	        
	        // Map special handling flags
	        cargo.setFragile(bookingRequest.getFragile() != null ? bookingRequest.getFragile() : false);
	        cargo.setPerishable(bookingRequest.getPerishable() != null ? bookingRequest.getPerishable() : false);
	        cargo.setRequiresSpecialHandling(bookingRequest.getSpecialHandling() != null ? 
	                bookingRequest.getSpecialHandling() : false);
	        
	        // Determine cargo type based on flags
	        if (cargo.getFragile()) {
	            cargo.setType(Booking.CargoType.FRAGILE);
	        } else if (cargo.getPerishable()) {
	            cargo.setType(Booking.CargoType.PERISHABLE);
	        } else {
	            cargo.setType(Booking.CargoType.GENERAL);
	        }
	        
	        // Build special instructions
	        StringBuilder instructions = new StringBuilder();
	        if (cargo.getFragile()) {
	            instructions.append("Handle with care - Fragile items. ");
	        }
	        if (cargo.getPerishable()) {
	            instructions.append("Perishable goods - Temperature controlled transport required. ");
	        }
	        if (cargo.getRequiresSpecialHandling()) {
	            instructions.append("Special handling required. ");
	        }
	        
	        cargo.setSpecialInstructions(instructions.toString().trim());
	        
	        return cargo;
	    }

	    /**
	     * Maps pricing information from BookingRequest to PricingDetails entity
	     */
	    private Booking.PricingDetails mapPricingDetails(BookingRequest bookingRequest) {
	        Booking.PricingDetails pricing = new Booking.PricingDetails();
	        
	        pricing.setBaseFare(bookingRequest.getBasePrice());
	        pricing.setInsuranceCharge(bookingRequest.getInsuranceCost());
	        pricing.setGstAmount(bookingRequest.getTaxAmount());
	        pricing.setTotalAmount(bookingRequest.getTotalAmount());
	        pricing.setFinalAmount(bookingRequest.getTotalAmount());
	        
	        // Set default values for other pricing fields
	        pricing.setServiceCharge(BigDecimal.ZERO);
	        pricing.setTollCharges(BigDecimal.ZERO);
	        pricing.setDiscountAmount(BigDecimal.ZERO);
	        
	        // Calculate per km rate if distance is available
	        if (bookingRequest.getEstimatedDistance() != null && 
	            bookingRequest.getEstimatedDistance().compareTo(BigDecimal.ZERO) > 0) {
	            BigDecimal perKmRate = bookingRequest.getBasePrice()
	                    .divide(bookingRequest.getEstimatedDistance(), 2, BigDecimal.ROUND_HALF_UP);
	            pricing.setPerKmRate(perKmRate);
	        }
	        
	        return pricing;
	    }

	    /**
	     * Maps payment information from BookingRequest to PaymentDetails entity
	     */
	    private Booking.PaymentDetails mapPaymentDetails(BookingRequest bookingRequest) {
	        Booking.PaymentDetails payment = new Booking.PaymentDetails();
	        
	        // Map payment method
	        payment.setMethod(mapPaymentMethod(bookingRequest.getPaymentMethod()));
	        
	        // Map payment status
	        payment.setStatus(mapPaymentStatus(bookingRequest.getPaymentStatus()));
	        
	        // Map Razorpay details if available
	        if (bookingRequest.getRazorpayOrderId() != null) {
	            payment.setTransactionId(bookingRequest.getRazorpayOrderId());
	        }
	        
	        // If payment is already completed, set paid amount
	        if ("PAID".equals(bookingRequest.getPaymentStatus())) {
	            payment.setPaidAmount(bookingRequest.getTotalAmount());
	            payment.setPaymentDate(LocalDateTime.now());
	        }
	        
	        return payment;
	    }

	    /**
	     * Maps payment method string to enum
	     */
	    private Booking.PaymentMethod mapPaymentMethod(String method) {
	        if (method == null) {
	            return Booking.PaymentMethod.CASH;
	        }
	        
	        switch (method.toUpperCase()) {
	            case "CREDIT_CARD":
	            case "DEBIT_CARD":
	                return Booking.PaymentMethod.CARD;
	            case "UPI":
	                return Booking.PaymentMethod.UPI;
	            case "NET_BANKING":
	                return Booking.PaymentMethod.NET_BANKING;
	            case "WALLET":
	                return Booking.PaymentMethod.WALLET;
	            case "CASH":
	            default:
	                return Booking.PaymentMethod.CASH;
	        }
	    }

	    /**
	     * Maps payment status string to enum
	     */
	    private Booking.PaymentStatus mapPaymentStatus(String status) {
	        if (status == null) {
	            return Booking.PaymentStatus.PENDING;
	        }
	        
	        switch (status.toUpperCase()) {
	            case "PAID":
	            case "COMPLETED":
	                return Booking.PaymentStatus.PAID;
	            case "FAILED":
	                return Booking.PaymentStatus.FAILED;
	            case "REFUNDED":
	                return Booking.PaymentStatus.REFUNDED;
	            case "PARTIALLY_REFUNDED":
	                return Booking.PaymentStatus.PARTIALLY_REFUNDED;
	            case "PENDING":
	            default:
	                return Booking.PaymentStatus.PENDING;
	        }
	    }
	    private String generateUniqueBookingNumber() {
	        String bookingNumber;
	        do {
	            bookingNumber = "BKN-" + UUID.randomUUID().toString().toUpperCase().substring(0, 8);
	        } while (bookingrepo.existsByBookingNumber(bookingNumber));
	        
	        return bookingNumber;
	        }
}
