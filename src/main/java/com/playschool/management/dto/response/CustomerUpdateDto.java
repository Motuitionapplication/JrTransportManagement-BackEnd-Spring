package com.playschool.management.dto.response;

import com.playschool.management.entity.Customer;

public class CustomerUpdateDto {
    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private Customer.AccountStatus accountStatus;
    // Add more fields as needed for your API response

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public Customer.AccountStatus getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(Customer.AccountStatus accountStatus) {
        this.accountStatus = accountStatus;
    }
}