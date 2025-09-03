package com.playschool.management.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.playschool.management.entity.AssignmentHistory;
import com.playschool.management.entity.Driver;
import com.playschool.management.entity.Vehicle;

public interface AssignmentHistoryRepository extends JpaRepository<AssignmentHistory, String> {
    
    // Custom query to find the current active assignment for a vehicle
    Optional<AssignmentHistory> findByVehicleAndIsActiveTrue(Vehicle vehicle);

    // Custom query to find the current active assignment for a driver
    Optional<AssignmentHistory> findByDriverAndIsActiveTrue(Driver driver);
}
