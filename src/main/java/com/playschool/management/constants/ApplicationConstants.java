package com.playschool.management.constants;

/**
 * Application-wide constants to avoid magic numbers and strings
 */
public final class ApplicationConstants {
    
    // Private constructor to prevent instantiation
    private ApplicationConstants() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }
    
    // HTTP Status Codes
    public static final class HttpStatus {
        public static final String OK = "200";
        public static final String BAD_REQUEST = "400";
        public static final String NOT_FOUND = "404";
        public static final String INTERNAL_SERVER_ERROR = "500";
        
        private HttpStatus() {
            throw new UnsupportedOperationException("Utility class cannot be instantiated");
        }
    }
    
    // Database Column Specifications
    public static final class ColumnSpecs {
        public static final int DECIMAL_PRECISION = 10;
        public static final int DECIMAL_SCALE = 2;
        public static final int HIGH_PRECISION_DECIMAL = 12;
        public static final int SHORT_TEXT_LENGTH = 1000;
        public static final int LONG_TEXT_LENGTH = 2000;
        public static final int USERNAME_MAX_LENGTH = 50;
        public static final int EMAIL_MAX_LENGTH = 100;
        public static final int PASSWORD_MAX_LENGTH = 120;
        public static final int PHONE_MAX_LENGTH = 15;
        public static final int ADDRESS_MAX_LENGTH = 200;
        public static final int DESCRIPTION_MAX_LENGTH = 500;
        
        private ColumnSpecs() {
            throw new UnsupportedOperationException("Utility class cannot be instantiated");
        }
    }
    
    // JWT and Security Constants
    public static final class Security {
        public static final int JWT_EXPIRATION_MS = 86400000; // 24 hours
        public static final String JWT_HEADER = "Authorization";
        public static final String JWT_PREFIX = "Bearer ";
        public static final long CORS_MAX_AGE = 3600L; // 1 hour
        
        private Security() {
            throw new UnsupportedOperationException("Utility class cannot be instantiated");
        }
    }
    
    // Common Messages
    public static final class Messages {
        public static final String VEHICLE_SAVED_SUCCESSFULLY = "Vehicle saved successfully";
        public static final String VEHICLE_NOT_FOUND = "Vehicle not found";
        public static final String INVALID_VEHICLE_DATA = "Invalid vehicle data";
        public static final String INTERNAL_SERVER_ERROR = "Internal server error";
        public static final String VEHICLE_STATUS_UPDATED = "Vehicle status updated successfully";
        public static final String INVALID_STATUS = "Invalid status";
        public static final String VEHICLE_LOCATION_UPDATED = "Vehicle location updated successfully";
        public static final String SERVICE_DATE_UPDATED = "Next service date updated successfully";
        public static final String INVALID_DATE_FORMAT = "Invalid date format";
        public static final String VEHICLE_ACTIVE_STATUS_UPDATED = "Vehicle active status updated successfully";
        public static final String LIST_RETRIEVED_SUCCESSFULLY = "List of vehicles retrieved successfully";
        public static final String AVAILABLE_VEHICLES_RETRIEVED = "List of available vehicles retrieved successfully";
        
        private Messages() {
            throw new UnsupportedOperationException("Utility class cannot be instantiated");
        }
    }
}
