package com.playschool.management.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.playschool.management.dto.CustomerResponseDto;
import com.playschool.management.entity.Customer;
import com.playschool.management.repository.CustomerRepository;
import com.playschool.management.service.CustomerService;

@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Override
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }
    @Override
    public Optional<Customer> getCustomerById(String id) {
        return customerRepository.findById(id);
    }

    @Override
    public Optional<Customer> getCustomerByUserId(String userId) {
        return customerRepository.findByUserId(userId);
    }

    @Override
    public CustomerResponseDto mapToResponseDto(Customer customer) {
        CustomerResponseDto dto = new CustomerResponseDto();
        dto.setId(customer.getId());
        dto.setUserId(customer.getUserId());
        dto.setPassword(customer.getPassword());
        CustomerResponseDto.Profile profile = new CustomerResponseDto.Profile();
        profile.setFirstName(customer.getFirstName());
        profile.setLastName(customer.getLastName());
        profile.setFatherName(customer.getFatherName());
        profile.setEmail(customer.getEmail());
        profile.setPhoneNumber(customer.getPhoneNumber());
        profile.setAlternatePhone(customer.getAlternatePhone());
        if (customer.getAddress() != null) {
            com.playschool.management.entity.VehicleOwner.Address addr = customer.getAddress();
            CustomerResponseDto.Address address = new CustomerResponseDto.Address();
            address.setStreet(addr.getStreet());
            address.setCity(addr.getCity());
            address.setState(addr.getState());
            address.setPincode(addr.getPincode());
            address.setCountry(addr.getCountry());
            profile.setAddress(address);
        }
        profile.setProfilePhoto(customer.getProfilePhoto());
        profile.setDateOfBirth(customer.getDateOfBirth());
        dto.setProfile(profile);
        // Map other fields as needed
        return dto;
    }
}
