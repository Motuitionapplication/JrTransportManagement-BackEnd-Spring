package com.transport.management.service;

import java.util.List;
import java.util.Optional;

import com.transport.management.dto.CustomerCreateDto;
import com.transport.management.dto.CustomerResponseDto;
import com.transport.management.dto.request.PasswordUpdateDto;
import com.transport.management.dto.response.CustomerUpdateDto;
import com.transport.management.entity.Customer;

public interface CustomerService {
    List<Customer> getAllCustomers();
    Optional<Customer> getCustomerById(String id);
    Optional<Customer> getCustomerByUserId(String userId);
    CustomerResponseDto mapToResponseDto(Customer customer);
    Customer updateCustomer(String customerId, CustomerUpdateDto dto);
    boolean deleteCustomerById(String customerId);

    // 🔄 Add this to support saveCustomer endpoint
    CustomerResponseDto addCustomer(CustomerCreateDto dto);
    public String getid(String userid);
    void updateCustomerPassword(String customerId, PasswordUpdateDto dto);
    }
