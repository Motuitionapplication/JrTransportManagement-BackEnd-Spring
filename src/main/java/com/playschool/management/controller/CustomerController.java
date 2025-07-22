package com.playschool.management.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.playschool.management.entity.Customer;
import com.playschool.management.service.CustomerService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/customers")
@Tag(name = "Customer Management", description = "APIs for managing customers in the transport system")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @Operation(summary = "Get all customers", description = "Retrieves all customers in the system")
    @GetMapping
    public ResponseEntity<List<Customer>> getAllCustomers() {
        return ResponseEntity.ok(customerService.getAllCustomers());
    }


    // Get customer profile by ID
    @GetMapping("/{customerId}")
    public ResponseEntity<Customer> getCustomerProfile(@PathVariable String customerId) {
        return ResponseEntity.of(customerService.getCustomerById(customerId));
    }

    @GetMapping("/by-user/{userId}")
    @Operation(summary = "Get Customer by user ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Customer found",
            content = @Content(schema = @Schema(implementation = Customer.class))),
        @ApiResponse(responseCode = "404", description = "Customer not found")
    })
    public ResponseEntity<Customer> getCustomerByUserId(@PathVariable String userId) {
        Optional<Customer> customerOpt = customerService.getCustomerByUserId(userId);
        if (customerOpt.isPresent()) {
            System.out.println("[DEBUG] Found customer for userId=" + userId + ": " + customerOpt.get());
            return ResponseEntity.ok(customerOpt.get());
        } else {
            System.out.println("[DEBUG] No customer found for userId=" + userId);
            return ResponseEntity.notFound().build();
        }
    }

}
