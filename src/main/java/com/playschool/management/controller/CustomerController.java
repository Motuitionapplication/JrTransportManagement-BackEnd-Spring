package com.playschool.management.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.playschool.management.dto.CustomerCreateDto;
import com.playschool.management.dto.CustomerResponseDto;
import com.playschool.management.dto.response.CustomerUpdateDto;
import com.playschool.management.entity.Customer;
import com.playschool.management.service.CustomerService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/customers")
@Tag(name = "Customer Management", description = "APIs for managing customers in the transport system")
@Slf4j  // ✅ Added for logging support
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @Operation(summary = "Get all customers", description = "Retrieves all customers in the system")
    @GetMapping
    public ResponseEntity<List<Customer>> getAllCustomers() {
        return ResponseEntity.ok(customerService.getAllCustomers());
    }

    @Operation(summary = "Get customer profile by ID", description = "Retrieves a customer profile by customer ID")
    @GetMapping("/{customerId}")
    public ResponseEntity<Customer> getCustomerProfile(@PathVariable String customerId) {
        return ResponseEntity.of(customerService.getCustomerById(customerId));
    }

    @Operation(summary = "Get Customer by user ID", description = "Retrieves a customer profile by user ID (foreign key)")
    @GetMapping("/by-user/{userId}")
    public ResponseEntity<CustomerResponseDto> getCustomerByUserId(@PathVariable String userId) {
        Optional<Customer> customerOpt = customerService.getCustomerByUserId(userId);
        return customerOpt
                .map(customer -> ResponseEntity.ok(customerService.mapToResponseDto(customer)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Create or update a customer", description = "Creates a new customer or updates an existing one based on the customer ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Customer saved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid customer data"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/{customerId}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Customer> updateCustomer(
            @PathVariable String customerId,
            @Valid
            @RequestBody CustomerUpdateDto dto
    ) {
        Customer updated = customerService.updateCustomer(customerId, dto);
        return ResponseEntity.ok(updated);
    }


    @Operation(summary = "Delete customer", description = "Delete a customer by ID")
    @DeleteMapping("/{customerId}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable String customerId) {
        boolean deleted = customerService.deleteCustomerById(customerId);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @Operation(summary = "Register a new customer", description = "Creates a new customer in the system")
    @PutMapping
    public ResponseEntity<CustomerResponseDto> addCustomer(@Valid @RequestBody CustomerCreateDto dto) {
        return ResponseEntity.ok(customerService.addCustomer(dto));
    }
}