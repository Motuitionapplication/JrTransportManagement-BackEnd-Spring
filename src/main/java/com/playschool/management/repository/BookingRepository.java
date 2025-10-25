package com.playschool.management.repository;

import com.playschool.management.entity.Booking;
import com.playschool.management.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, String> {
    
    // Find bookings by customer ID
    List<Booking> findByCustomerId(String customerId);
    
    // Find bookings by driver ID
    List<Booking> findByDriverId(String driverId);
    
    // Find bookings by owner ID
    List<Booking> findByOwnerId(String ownerId);
    
    // Find bookings by vehicle ID
    List<Booking> findByVehicleId(String vehicleId);
    
    // Find bookings by status
    List<Booking> findByStatus(Booking.BookingStatus status);
    
    // Find bookings by payment status
    List<Booking> findByPaymentStatus(Booking.PaymentStatus paymentStatus);
    
    // Find active bookings (in progress)
    @Query("SELECT b FROM Booking b WHERE b.status IN ('CONFIRMED', 'IN_TRANSIT', 'PICKED_UP')")
    List<Booking> findActiveBookings();
    
    // Find bookings within date range
    @Query("SELECT b FROM Booking b WHERE b.scheduledPickupDate BETWEEN :startDate AND :endDate")
    List<Booking> findBookingsByDateRange(@Param("startDate") LocalDateTime startDate, 
                                         @Param("endDate") LocalDateTime endDate);
    
    // Find bookings by departure city
    @Query("SELECT b FROM Booking b WHERE b.pickupAddress.city = :city")
    List<Booking> findByDepartureCity(@Param("city") String departureCity);
    
    // Find bookings by destination city
    @Query("SELECT b FROM Booking b WHERE b.deliveryAddress.city = :city")
    List<Booking> findByDestinationCity(@Param("city") String destinationCity);
    
    // Find bookings by route
    @Query("SELECT b FROM Booking b WHERE b.pickupAddress.city = :departureCity AND b.deliveryAddress.city = :destinationCity")
    List<Booking> findByRoute(@Param("departureCity") String departureCity, 
                             @Param("destinationCity") String destinationCity);
    
    // Find bookings by vehicle type
    @Query("SELECT b FROM Booking b JOIN Vehicle v ON b.vehicleId = v.id WHERE v.vehicleType = :vehicleType")
    List<Booking> findByVehicleType(@Param("vehicleType") Vehicle.VehicleType vehicleType);
    
    // Find bookings with fare above amount
    @Query("SELECT b FROM Booking b WHERE b.pricing.totalAmount > :amount")
    List<Booking> findByTotalAmountGreaterThan(@Param("amount") BigDecimal amount);
    
    // Find bookings by distance range
    @Query("SELECT b FROM Booking b WHERE b.totalDistance BETWEEN :minDistance AND :maxDistance")
    List<Booking> findByTotalDistanceBetween(@Param("minDistance") BigDecimal minDistance, @Param("maxDistance") BigDecimal maxDistance);
    
    // Find bookings requiring special care (based on cargo type)
    @Query("SELECT b FROM Booking b WHERE b.cargo.type IN ('FRAGILE', 'HAZARDOUS', 'VALUABLE')")
    List<Booking> findBySpecialCareRequiredTrue();
    
    // Find recent bookings for customer
    @Query("SELECT b FROM Booking b WHERE b.customerId = :customerId ORDER BY b.createdAt DESC")
    List<Booking> findRecentBookingsByCustomer(@Param("customerId") String customerId);
    
    // Find pending pickup bookings
    @Query("SELECT b FROM Booking b WHERE b.status = 'CONFIRMED' AND b.scheduledPickupDate <= :currentTime")
    List<Booking> findPendingPickupBookings(@Param("currentTime") LocalDateTime currentTime);
    
    // Find overdue bookings
    @Query("SELECT b FROM Booking b WHERE b.status IN ('CONFIRMED', 'IN_TRANSIT') AND b.estimatedArrival < :currentTime")
    List<Booking> findOverdueBookings(@Param("currentTime") LocalDateTime currentTime);
    
    // Find bookings with specific cargo type
    @Query("SELECT b FROM Booking b WHERE b.cargo.type = :cargoType")
    List<Booking> findByCargoType(@Param("cargoType") Booking.CargoType cargoType);
    
    // Find bookings with hazardous cargo
    @Query("SELECT b FROM Booking b WHERE b.cargo.type = 'HAZARDOUS'")
    List<Booking> findBookingsWithHazardousCargo();
    
    // Find bookings with insurance (based on valuable cargo)
    @Query("SELECT b FROM Booking b WHERE b.cargo.type = 'VALUABLE'")
    List<Booking> findBookingsWithInsurance();
    
    // Find bookings by pickup location
    @Query("SELECT b FROM Booking b WHERE b.pickupAddress.city = :city")
    List<Booking> findByPickupAddress(@Param("city") String city);
    
    // Find bookings by delivery location
    @Query("SELECT b FROM Booking b WHERE b.deliveryAddress.city = :city")
    List<Booking> findByDropAddress(@Param("city") String city);
    
    // Find bookings with discount applied
    @Query("SELECT b FROM Booking b WHERE b.pricing.discountAmount > 0")
    List<Booking> findBookingsWithDiscount();
    
    // Find bookings by payment method
    @Query("SELECT b FROM Booking b WHERE b.payment.method = :paymentMethod")
    List<Booking> findByPaymentMethod(@Param("paymentMethod") Booking.PaymentMethod paymentMethod);
    
    // Find completed bookings for driver
    @Query("SELECT b FROM Booking b WHERE b.driverId = :driverId AND b.status = 'DELIVERED'")
    List<Booking> findCompletedBookingsByDriver(@Param("driverId") String driverId);
    
    // Count bookings by status for dashboard
    @Query("SELECT b.status, COUNT(b) FROM Booking b GROUP BY b.status")
    List<Object[]> countBookingsByStatus();
    
    // Find earnings by owner
    @Query("SELECT SUM(b.pricing.totalAmount) FROM Booking b WHERE b.ownerId = :ownerId AND b.status = 'DELIVERED'")
    BigDecimal findTotalEarningsByOwner(@Param("ownerId") String ownerId);
    
    // Find earnings by driver
    @Query("SELECT SUM(b.pricing.totalAmount) FROM Booking b WHERE b.driverId = :driverId AND b.status = 'DELIVERED'")
    BigDecimal findTotalEarningsByDriver(@Param("driverId") String driverId);
    
    // Find bookings with feedback pending
    @Query("SELECT b FROM Booking b WHERE b.status = 'DELIVERED' AND b.customerComment IS NULL")
    List<Booking> findBookingsWithPendingFeedback();
    
    // Find repeat customers
    @Query("SELECT b.customerId FROM Booking b GROUP BY b.customerId HAVING COUNT(b) > 1")
    List<String> findRepeatCustomers();
    
    // Find bookings by special instructions
    @Query("SELECT b FROM Booking b WHERE b.cargo.specialInstructions IS NOT NULL AND b.cargo.specialInstructions != ''")
    List<Booking> findBookingsWithSpecialInstructions();
    
    // Count total bookings for a time period
    @Query("SELECT COUNT(b) FROM Booking b WHERE b.createdAt BETWEEN :startDate AND :endDate")
    Long countBookingsInPeriod(@Param("startDate") LocalDateTime startDate, 
                              @Param("endDate") LocalDateTime endDate);
    
    // Find average booking amount
    @Query("SELECT AVG(b.pricing.totalAmount) FROM Booking b WHERE b.status = 'DELIVERED'")
    BigDecimal findAverageBookingAmount();
        
    boolean existsByBookingNumber(String bookingNumber);

}
