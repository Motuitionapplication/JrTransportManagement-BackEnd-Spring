package com.playschool.management.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data 
@Table(name = "assignment_history")
public class AssignmentHistory {

	 @Id
	    @GeneratedValue(strategy = GenerationType.UUID) 
	    @Column(name = "id", updatable = false, nullable = false)
	    private String id;


    @ManyToOne
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @ManyToOne
    @JoinColumn(name = "driver_id", nullable = false)
    private Driver driver;

    @Column(name = "assignment_start_date", nullable = false)
    private LocalDate assignmentStartDate;

    @Column(name = "assignment_end_date")
    private LocalDate assignmentEndDate; // This will be null for active assignments

    @Column(name = "is_active", nullable = false)
    private boolean isActive;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Vehicle getVehicle() {
		return vehicle;
	}

	public void setVehicle(Vehicle vehicle) {
		this.vehicle = vehicle;
	}

	public Driver getDriver() {
		return driver;
	}

	public void setDriver(Driver driver) {
		this.driver = driver;
	}

	public LocalDate getAssignmentStartDate() {
		return assignmentStartDate;
	}

	public void setAssignmentStartDate(LocalDate assignmentStartDate) {
		this.assignmentStartDate = assignmentStartDate;
	}

	public LocalDate getAssignmentEndDate() {
		return assignmentEndDate;
	}

	public void setAssignmentEndDate(LocalDate assignmentEndDate) {
		this.assignmentEndDate = assignmentEndDate;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}
    
}
