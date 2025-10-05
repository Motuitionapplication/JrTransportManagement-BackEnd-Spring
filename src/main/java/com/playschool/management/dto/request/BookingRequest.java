package com.playschool.management.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.Valid;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class BookingRequest {

	public enum VehicleType {
        TRUCK, VAN, TRAILER, CONTAINER, PICKUP
    }

    @NotBlank(message = "Customer ID cannot be blank")
    private String customerId;

    @NotNull(message = "Pickup location is required")
    @Valid // This annotation triggers validation on the nested AddressDTO object
    private AddressDTO pickupLocation;

    @NotNull(message = "Drop-off location is required")
    @Valid
    private AddressDTO dropOffLocation;

    @NotNull(message = "Pickup date is required")
    @FutureOrPresent(message = "Pickup date must be in the present or future")
    private LocalDate pickupDate;

    @NotBlank(message = "Pickup time preference is required")
    private String pickupTime;

    @NotNull(message = "Truck and cargo requirements are necessary")
    @Valid
    private TruckRequirementsDTO truckRequirements;

    /**
     * Nested DTO to represent a physical address.
     * Reused for both pickup and drop-off locations.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddressDTO {
        @NotBlank(message = "Street cannot be blank")
        private String street;

        @NotBlank(message = "City cannot be blank")
        private String city;

        @NotBlank(message = "State cannot be blank")
        private String state;

        @NotBlank(message = "Pincode cannot be blank")
        @Pattern(regexp = "^[1-9][0-9]{5}$", message = "Invalid pincode format")
        private String pincode;

        @NotBlank(message = "Country cannot be blank")
        private String country;

		public String getStreet() {
			return street;
		}

		public void setStreet(String street) {
			this.street = street;
		}

		public String getCity() {
			return city;
		}

		public void setCity(String city) {
			this.city = city;
		}

		public String getState() {
			return state;
		}

		public void setState(String state) {
			this.state = state;
		}

		public String getPincode() {
			return pincode;
		}

		public void setPincode(String pincode) {
			this.pincode = pincode;
		}

		public String getCountry() {
			return country;
		}

		public void setCountry(String country) {
			this.country = country;
		}
        
        
    }

    /**
     * Nested DTO to capture details about the cargo,
     * which implicitly defines the truck type and size needed.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TruckRequirementsDTO {
        @NotNull(message = "Vehicle type is required")
        private VehicleType vehicleType;

        @NotNull(message = "Cargo weight is required")
        @Positive(message = "Weight must be positive")
        private BigDecimal cargoWeightKg;

        // Optional dimensions, but good to have for size calculation
        @Positive(message = "Length must be positive")
        private BigDecimal cargoLengthMeters;

        @Positive(message = "Width must be positive")
        private BigDecimal cargoWidthMeters;

        @Positive(message = "Height must be positive")
        private BigDecimal cargoHeightMeters;

		public VehicleType getVehicleType() {
			return vehicleType;
		}

		public void setVehicleType(VehicleType vehicleType) {
			this.vehicleType = vehicleType;
		}

		public BigDecimal getCargoWeightKg() {
			return cargoWeightKg;
		}

		public void setCargoWeightKg(BigDecimal cargoWeightKg) {
			this.cargoWeightKg = cargoWeightKg;
		}

		public BigDecimal getCargoLengthMeters() {
			return cargoLengthMeters;
		}

		public void setCargoLengthMeters(BigDecimal cargoLengthMeters) {
			this.cargoLengthMeters = cargoLengthMeters;
		}

		public BigDecimal getCargoWidthMeters() {
			return cargoWidthMeters;
		}

		public void setCargoWidthMeters(BigDecimal cargoWidthMeters) {
			this.cargoWidthMeters = cargoWidthMeters;
		}

		public BigDecimal getCargoHeightMeters() {
			return cargoHeightMeters;
		}

		public void setCargoHeightMeters(BigDecimal cargoHeightMeters) {
			this.cargoHeightMeters = cargoHeightMeters;
		}
        
        
    }

	public BookingRequest(@NotBlank(message = "Customer ID cannot be blank") String customerId,
			@NotNull(message = "Pickup location is required") @Valid AddressDTO pickupLocation,
			@NotNull(message = "Drop-off location is required") @Valid AddressDTO dropOffLocation,
			@NotNull(message = "Pickup date is required") @FutureOrPresent(message = "Pickup date must be in the present or future") LocalDate pickupDate,
			@NotBlank(message = "Pickup time preference is required") String pickupTime,
			@NotNull(message = "Truck and cargo requirements are necessary") @Valid TruckRequirementsDTO truckRequirements) {
		super();
		this.customerId = customerId;
		this.pickupLocation = pickupLocation;
		this.dropOffLocation = dropOffLocation;
		this.pickupDate = pickupDate;
		this.pickupTime = pickupTime;
		this.truckRequirements = truckRequirements;
	}

	public String getCustomerId() {
		return customerId;
	}

	public void setCustomerId(String customerId) {
		this.customerId = customerId;
	}

	public AddressDTO getPickupLocation() {
		return pickupLocation;
	}

	public void setPickupLocation(AddressDTO pickupLocation) {
		this.pickupLocation = pickupLocation;
	}

	public AddressDTO getDropOffLocation() {
		return dropOffLocation;
	}

	public void setDropOffLocation(AddressDTO dropOffLocation) {
		this.dropOffLocation = dropOffLocation;
	}

	public LocalDate getPickupDate() {
		return pickupDate;
	}

	public void setPickupDate(LocalDate pickupDate) {
		this.pickupDate = pickupDate;
	}

	public String getPickupTime() {
		return pickupTime;
	}

	public void setPickupTime(String pickupTime) {
		this.pickupTime = pickupTime;
	}

	public TruckRequirementsDTO getTruckRequirements() {
		return truckRequirements;
	}

	public void setTruckRequirements(TruckRequirementsDTO truckRequirements) {
		this.truckRequirements = truckRequirements;
	}
    
}
