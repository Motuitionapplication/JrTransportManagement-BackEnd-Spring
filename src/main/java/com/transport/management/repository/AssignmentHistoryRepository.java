package com.transport.management.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.transport.management.dto.response.AssignmentHistoryDto;
import com.transport.management.entity.AssignmentHistory;
import com.transport.management.entity.Driver;
import com.transport.management.entity.Vehicle;

public interface AssignmentHistoryRepository extends JpaRepository<AssignmentHistory, String> {
    
    // Custom query to find the current active assignment for a vehicle
    Optional<AssignmentHistory> findByVehicleAndIsActiveTrue(Vehicle vehicle);

    // Custom query to find the current active assignment for a driver
    Optional<AssignmentHistory> findByDriverAndIsActiveTrue(Driver driver);
    
    @Query("SELECT new com.transport.management.dto.response.AssignmentHistoryDto(" + 
    	       "v.vehicleNumber, " +
    	       "v.model, " +
    	       "d.firstName, " +
    	       "d.lastName, " +
    	       "d.phoneNumber, " +
    	       "ah.assignmentStartDate, " +
    	       "ah.assignmentEndDate) " +
    	       "FROM AssignmentHistory ah " +
    	       "JOIN ah.vehicle v " +
    	       "JOIN ah.driver d " +
    	       "WHERE v.owner.id = :ownerId " +
    	       "ORDER BY ah.assignmentStartDate DESC")
    	List<AssignmentHistoryDto> findHistoryByOwnerId(@Param("ownerId") String ownerId);
}

