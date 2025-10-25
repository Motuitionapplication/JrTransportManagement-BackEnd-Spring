package com.playschool.management.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.playschool.management.dto.DriverLocation;

@Service
public class NotificationService {

	@Autowired
    private SimpMessagingTemplate messagingTemplate;

    public void broadcastLocation(DriverLocation location) {
        // The destination is dynamic, based on the driver's ID.
        // Clients subscribed to "/topic/location/123" will receive this message.
        String destination = "/topic/location/" + location.getDriverId();
        messagingTemplate.convertAndSend(destination, location);
    }
	
	
}
