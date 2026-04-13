package com.transport.management.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.transport.management.dto.DriverLocation;
import com.transport.management.service.NotificationService;

@RestController
@RequestMapping("/api/location")
@CrossOrigin(origins = {"http://127.0.0.1:5500", "http://localhost:5500"})
public class LocationController {

	@Autowired
    private NotificationService notificationService;

    @PostMapping("/update")
    public String updateLocation(@RequestBody DriverLocation location) {
        // When a location is received, pass it to the service to be broadcasted
        notificationService.broadcastLocation(location);
        return "Location update received for driver " + location.getDriverId();
    }
	
	
}

