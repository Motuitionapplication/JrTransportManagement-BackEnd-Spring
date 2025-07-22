package com.playschool.management.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.playschool.management.dto.CustomerResponseDto;
import com.playschool.management.entity.Customer;
import com.playschool.management.service.CustomerService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/customers")
@Tag(name = "Customer Management", description = "APIs for managing customers in the transport system")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    // This method returns a list of all customers in the system.
    // It is useful for admin or support staff to view all registered customers.
    /**
     * Retrieves all customers in the system.
     * @return List of all customers
     */
    @Operation(summary = "Get all customers", description = "Retrieves all customers in the system")
    @GetMapping
    public ResponseEntity<List<Customer>> getAllCustomers() {
        // Calls the service layer to fetch all customers from the database
        return ResponseEntity.ok(customerService.getAllCustomers());
    }


    // This method fetches a single customer's profile using their unique customer ID.
    // Used when you need details of a specific customer.
    /**
     * Retrieves a customer profile by customer ID.
     * @param customerId The unique ID of the customer
     * @return Customer profile if found, otherwise 404
     */
    @Operation(summary = "Get customer profile by ID", description = "Retrieves a customer profile by customer ID")
    @GetMapping("/{customerId}")
    public ResponseEntity<Customer> getCustomerProfile(@PathVariable String customerId) {
        // Calls the service layer to fetch customer details by ID
        return ResponseEntity.of(customerService.getCustomerById(customerId));
    }

    // This method fetches a customer's profile using their user ID (foreign key).
    // This is typically used after login, as userId is available in the auth token.
    /**
     * Retrieves a customer profile by user ID (foreign key).
     * @param userId The user ID associated with the customer
     * @return Customer profile DTO if found, otherwise 404
     */
    @Operation(summary = "Get Customer by user ID", description = "Retrieves a customer profile by user ID (foreign key)")
    @GetMapping("/by-user/{userId}")
    public ResponseEntity<CustomerResponseDto> getCustomerByUserId(@PathVariable String userId) {
        // Calls the service layer to fetch customer by userId
        Optional<Customer> customerOpt = customerService.getCustomerByUserId(userId);
        // If customer is found, map entity to DTO and return
        if (customerOpt.isPresent()) {
            // Converts Customer entity to CustomerResponseDto for frontend compatibility
            CustomerResponseDto dto = customerService.mapToResponseDto(customerOpt.get());
            return ResponseEntity.ok(dto);
        } else {
            // If not found, return 404 Not Found
            return ResponseEntity.notFound().build();
        }
    }

}
