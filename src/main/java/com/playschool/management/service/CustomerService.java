package com.playschool.management.service;

import java.util.List;
import java.util.Optional;

import com.playschool.management.dto.CustomerCreateDto;
import com.playschool.management.dto.CustomerResponseDto;
import com.playschool.management.dto.response.CustomerUpdateDto;
import com.playschool.management.entity.Customer;

public interface CustomerService {
    List<Customer> getAllCustomers();
    Optional<Customer> getCustomerById(String id);
    Optional<Customer> getCustomerByUserId(String userId);
    CustomerResponseDto mapToResponseDto(Customer customer);
    Customer updateCustomer(String customerId, CustomerUpdateDto dto);
    boolean deleteCustomerById(String customerId);

    // 🔄 Add this to support saveCustomer endpoint
    CustomerResponseDto addCustomer(CustomerCreateDto dto);
}