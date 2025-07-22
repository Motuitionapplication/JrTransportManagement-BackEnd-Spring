package com.playschool.management.service;

import com.playschool.management.entity.Customer;
import java.util.List;

import java.util.Optional;
import org.springframework.http.ResponseEntity;

public interface CustomerService {
    List<Customer> getAllCustomers();
    Optional<Customer> getCustomerById(String id);
    Optional<Customer> getCustomerByUserId(String userId);
}
