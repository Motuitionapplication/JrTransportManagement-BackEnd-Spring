package com.playschool.management.service;

import java.time.LocalDateTime;
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
        // 1. Verify that the customer exists.
        customerRepository.findById(customerId)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found with ID: " + customerId));

        // 2. Create a new Booking entity instance.
        Booking booking = new Booking();

        // 3. Map data from the DTO to the Booking entity.
        booking.setCustomerId(customerId);

        // Map pickup and delivery addresses
        booking.setPickupAddress(mapAddressDtoToEntity(bookingRequest.getPickupLocation()));
        booking.setDeliveryAddress(mapAddressDtoToEntity(bookingRequest.getDropOffLocation()));

        // Map date and time preferences
        booking.setScheduledPickupDate(bookingRequest.getPickupDate().atStartOfDay()); // Converts LocalDate to LocalDateTime at midnight
        booking.setScheduledPickupTime(bookingRequest.getPickupTime());
        booking.setCargo(mapTruckRequirementsToCargo(bookingRequest.getTruckRequirements()));
        booking.setBookingNumber(generateUniqueBookingNumber());
        booking.setStatus(Booking.BookingStatus.PENDING); 
        booking.setCustomerTermsAccepted(true);
        booking.setCustomerTermsAcceptedAt(LocalDateTime.now());

        Booking savedBooking = bookingrepo.save(booking);
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Customer not found"));

        customer.getBookingHistory().add(booking.getId());
        customerRepository.save(customer);
        
        return savedBooking;
    }

    private Booking.BookingAddress mapAddressDtoToEntity(BookingRequest.AddressDTO addressDto) {
        Booking.BookingAddress address = new Booking.BookingAddress();
        address.setStreet(addressDto.getStreet());
        address.setCity(addressDto.getCity());
        address.setState(addressDto.getState());
        address.setPincode(addressDto.getPincode());
        address.setCountry(addressDto.getCountry());
        return address;
    }

    private Booking.CargoDetails mapTruckRequirementsToCargo(BookingRequest.TruckRequirementsDTO truckRequirements) {
        Booking.CargoDetails cargo = new Booking.CargoDetails();
        cargo.setWeight(truckRequirements.getCargoWeightKg());
        cargo.setLength(truckRequirements.getCargoLengthMeters());
        cargo.setWidth(truckRequirements.getCargoWidthMeters());
        cargo.setHeight(truckRequirements.getCargoHeightMeters());
                cargo.setDescription("Cargo for " + truckRequirements.getVehicleType()); 
        
        return cargo;
    }

    private String generateUniqueBookingNumber() {
        return "BKN-" + UUID.randomUUID().toString().toUpperCase().substring(0, 8);
    }
}
