package com.playschool.management.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/test")
public class TestController {
    @GetMapping("/all")
    public String allAccess() {
        return "Public Content.";
    }
    
    @GetMapping("/parent")
    @PreAuthorize("hasRole('PARENT') or hasRole('ADMIN')")
    public String parentAccess() {
        return "Parent Content.";
    }
    
    @GetMapping("/driver")
    @PreAuthorize("hasRole('DRIVER') or hasRole('ADMIN')")
    public String driverAccess() {
        return "Driver Board.";
    }
    
    @GetMapping("/owner")
    @PreAuthorize("hasRole('OWNER') or hasRole('ADMIN')")
    public String ownerAccess() {
        return "Vehicle Owner Board.";
    }
    
    @GetMapping("/customer")
    @PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
    public String customerAccess() {
        return "Customer Board.";
    }
    
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminAccess() {
        return "Admin Board.";
    }
    
    @GetMapping("/staff")
    @PreAuthorize("hasRole('STAFF') or hasRole('ADMIN')")
    public String staffAccess() {
        return "Staff Board.";
    }
}
