# Backend Implementation - Detailed Technical Explanation

## Project: JR Transport Management System

**Stack:** Spring Boot 3.5.3 | Java 17 | PostgreSQL | JWT Authentication

---

## 📋 Table of Contents

1. [Architecture Overview](#architecture-overview)
2. [Authentication & Security](#authentication--security)
3. [Database Design](#database-design)
4. [API Controllers - Detailed](#api-controllers---detailed)
5. [Service Layer](#service-layer)
6. [Key Business Flows](#key-business-flows)
7. [Configuration & Infrastructure](#configuration--infrastructure)

---

## 1. Architecture Overview

### 🏗️ **Layered Architecture Pattern**

```
┌─────────────────────────────────────────────────────┐
│             REST API Controllers                     │ ← Exposed Endpoints
│  (AuthController, VehicleController, etc.)          │
├─────────────────────────────────────────────────────┤
│             Service Layer                           │ ← Business Logic
│  (VehicleService, CustomerService, etc.)           │
├─────────────────────────────────────────────────────┤
│             Repository Layer (DAO)                  │ ← Data Access
│  (UserRepository, VehicleRepository, etc.)         │
├─────────────────────────────────────────────────────┤
│             Entity Models                           │ ← Database Entities
│  (User, Vehicle, Booking, etc.)                    │
├─────────────────────────────────────────────────────┤
│             PostgreSQL Database                     │ ← Persistent Storage
└─────────────────────────────────────────────────────┘
```

### **Key Design Patterns Used:**

- **Dependency Injection:** Spring's `@Autowired` and constructor injection
- **Repository Pattern:** Spring Data JPA repositories for data access
- **DTO Pattern:** Data Transfer Objects for API requests/responses
- **Service Layer:** Business logic separated from controllers
- **JWT Token-based Authentication:** Stateless security model

---

## 2. Authentication & Security

### 🔐 **JWT Authentication Flow**

#### **Step 1: User Registration (Signup)**

**Endpoint:** `POST /api/auth/signup`

**Request Body:**

```json
{
  "username": "owner123",
  "email": "owner@example.com",
  "password": "SecurePassword123",
  "firstName": "John",
  "lastName": "Doe",
  "role": ["ROLE_OWNER"]
}
```

**What Happens:**

1. User data is validated (email uniqueness, password strength)
2. Password is **hashed using BCrypt** before storage
3. User entity is created with assigned roles
4. User is saved to `users` table with role mappings in `user_roles` junction table

**Key Code:**

```java
@PostMapping("/signup")
public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
    // Validate username uniqueness
    if (userRepository.existsByUsername(signUpRequest.getUsername())) {
        return ResponseEntity.badRequest().body(new MessageResponse("Username is already taken!"));
    }
    // Validate email uniqueness
    if (userRepository.existsByEmail(signUpRequest.getEmail())) {
        return ResponseEntity.badRequest().body(new MessageResponse("Email is already in use!"));
    }

    // Create new user with encrypted password
    User user = new User(signUpRequest.getUsername(),
                        signUpRequest.getEmail(),
                        encoder.encode(signUpRequest.getPassword()));

    Set<String> strRoles = signUpRequest.getRole();
    Set<Role> roles = new HashSet<>();

    // Map string roles to Role entities
    strRoles.forEach(role -> {
        switch(role) {
            case "admin":
                Role adminRole = roleRepository.findByName(RoleName.ROLE_ADMIN)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                roles.add(adminRole);
                break;
            // ... other role mappings
        }
    });

    user.setRoles(roles);
    userRepository.save(user);
    return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
}
```

#### **Step 2: User Login (Signin)**

**Endpoint:** `POST /api/auth/signin`

**Request Body:**

```json
{
  "username": "owner123",
  "password": "SecurePassword123"
}
```

**Authentication Flow:**

1. **Authentication Manager** receives login request
2. **DaoAuthenticationProvider** (configured in `WebSecurityConfig`) authenticates:
   - Loads user from database via `UserDetailsServiceImpl`
   - Compares provided password with stored bcrypt hash
   - Validates user status and permissions

3. **JWT Token Generation** (if authentication succeeds):
   - `JwtUtils.generateJwtToken()` creates JWT with:
     - **Header:** Algorithm (HS512)
     - **Payload:** User ID, Username, Email, Roles, Issue time, Expiration
     - **Signature:** Signed with secret key

4. **Token Format:**

```
eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJvd25lcjEyMyIsImlhdCI6MTcwNTI0MDAwMCwiZXhwIjoxNzA1MzI2NDAwfQ.signature
```

**Response:**

```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "type": "Bearer",
  "id": 1,
  "username": "owner123",
  "email": "owner@example.com",
  "firstName": "John",
  "roles": ["ROLE_OWNER"]
}
```

### 🛡️ **Request Authentication (JWT Filter)**

**Class:** `AuthTokenFilter`

**Sequence for Protected Endpoints:**

```
Client Request (with Authorization: Bearer <token>)
        ↓
AuthTokenFilter.doFilterInternal()
        ↓
Extract token from "Authorization: Bearer" header
        ↓
JwtUtils.validateJwtToken()
        ├─ Check signature validity
        ├─ Check token expiration
        └─ Check token format
        ↓
Load user from database using sub (subject/username) claim
        ↓
Create Authentication object with user details & authorities
        ↓
Set SecurityContext with authentication
        ↓
Request continues to controller
        ↓
@PreAuthorize annotations enforce role-based access
```

### **Security Configuration (`WebSecurityConfig`)**

```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig {

    // Password Encoding
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // 10 rounds of hashing
    }

    // CORS Configuration
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(
            "http://localhost:4200",
            "https://jr-transport.netlify.app"
        ));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    // Security Filter Chain
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable()) // Disabled because we use JWT (stateless)
            .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth ->
                auth.requestMatchers("/api/auth/**").permitAll()  // Public endpoints
                    .requestMatchers("/ws/**").permitAll()        // WebSocket
                    .anyRequest().authenticated()                 // Everything else requires JWT
            )
            .addFilterBefore(authenticationJwtTokenFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
```

### **Role-Based Access Control**

**Example from `VehicleOwnerController`:**

```java
@PreAuthorize("hasRole('OWNER')")
@GetMapping("/{ownerId}/dashboard-stats")
public ResponseEntity<?> getDashboardStats(@PathVariable String ownerId) {
    // Only users with ROLE_OWNER can access
    // ...
}

@PreAuthorize("hasRole('ADMIN') or hasRole('SUPER_ADMIN')")
@DeleteMapping("/{ownerId}")
public ResponseEntity<?> deleteOwner(@PathVariable String ownerId) {
    // Only admins can delete owners
    // ...
}
```

---

## 3. Database Design

### 📊 **Entity Relationship Diagram (Conceptual)**

```
┌──────────────┐         ┌─────────────┐
│    User      │◄────────┤  Role       │
│──────────────│ M:N     │─────────────│
│ id (PK)      │         │ id (PK)     │
│ username     │         │ name        │
│ email        │         │ (ADMIN,     │
│ password     │         │  DRIVER,    │
│ firstName    │         │  CUSTOMER)  │
│ lastName     │         └─────────────┘
│ phoneNumber  │
│ isActive     │
└──────────────┘
       │
       │1
       │
       ├──────────────────┬─────────────────┬──────────────┐
       │                  │                 │              │
       │1                 │1                │1             │1
       ▼                  ▼                 ▼              ▼
┌──────────────┐  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐
│   Admin      │  │   Driver     │  │  Customer    │  │VehicleOwner  │
│──────────────│  │──────────────│  │──────────────│  │──────────────│
│ adminId (PK) │  │ driverId(PK) │  │ custId(PK)  │  │ ownerId(PK) │
│ userId (FK)  │  │ userId (FK)  │  │ userId(FK) │  │ userId(FK)   │
│ dept         │  │ licenseNo    │  │ address     │  │ businessName │
│              │  │ rating       │  │ ratings     │  │ walletBal    │
└──────────────┘  │ review       │  │             │  │ verified     │
                  └──────────────┘  └──────────────┘  └──────────────┘
                       │1                  │1                │1
                       │                   │                 │
                       │ M                 │ M               │ M
                       ▼                   ▼                 ▼
                  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐
                  │ Vehicle      │  │ Booking      │  │ Payment      │
                  │──────────────│  │──────────────│  │──────────────│
                  │ vehicleId    │  │ bookingId    │  │ paymentId    │
                  │ ownerId(FK)  │  │ customerId   │  │ custId(FK)   │
                  │ regNumber    │  │ driverId     │  │ amount       │
                  │ capacity     │  │ status       │  │ status       │
                  │ location     │  │ startTime    │  │ timestamp    │
                  │ status       │  │ endTime      │  └──────────────┘
                  └──────────────┘  │ fare         │
                                    └──────────────┘
```

### **Core Tables Structure**

#### **users Table**

```sql
CREATE TABLE users (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(50) UNIQUE NOT NULL,
  email VARCHAR(100) UNIQUE NOT NULL,
  password VARCHAR(120) NOT NULL,      -- BCrypt hash
  first_name VARCHAR(50) NOT NULL,
  last_name VARCHAR(50) NOT NULL,
  phone_number VARCHAR(15),
  is_active BOOLEAN DEFAULT true,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE user_roles (
  user_id BIGINT NOT NULL,
  role_id BIGINT NOT NULL,
  PRIMARY KEY (user_id, role_id),
  FOREIGN KEY (user_id) REFERENCES users(id),
  FOREIGN KEY (role_id) REFERENCES roles(id)
);
```

#### **vehicle_owners Table**

```sql
CREATE TABLE vehicle_owners (
  owner_id VARCHAR(36) PRIMARY KEY,
  user_id BIGINT NOT NULL UNIQUE,
  business_name VARCHAR(255),
  business_category VARCHAR(100),
  wallet_balance DECIMAL(10,2) DEFAULT 0.00,
  verification_status ENUM('PENDING', 'VERIFIED', 'REJECTED'),
  account_status ENUM('ACTIVE', 'SUSPENDED', 'DEACTIVATED'),
  city VARCHAR(100),
  created_at TIMESTAMP,
  updated_at TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE wallet_transactions (
  transaction_id VARCHAR(36) PRIMARY KEY,
  owner_id VARCHAR(36) NOT NULL,
  amount DECIMAL(10,2),
  transaction_type ENUM('ADD', 'DEDUCT'),
  reason VARCHAR(255),
  timestamp TIMESTAMP,
  FOREIGN KEY (owner_id) REFERENCES vehicle_owners(owner_id)
);
```

#### **vehicles Table**

```sql
CREATE TABLE vehicles (
  vehicle_id VARCHAR(36) PRIMARY KEY,
  owner_id VARCHAR(36) NOT NULL,
  registration_number VARCHAR(20) UNIQUE,
  vehicle_type VARCHAR(50),          -- CAR, VAN, BUS
  capacity INT,
  status VARCHAR(50),                -- ACTIVE, INACTIVE, MAINTENANCE
  current_location VARCHAR(255),
  last_service_date DATE,
  insurance_expiry DATE,
  pollution_certificate_expiry DATE,
  created_at TIMESTAMP,
  FOREIGN KEY (owner_id) REFERENCES vehicle_owners(owner_id)
);
```

#### **bookings Table**

```sql
CREATE TABLE bookings (
  booking_id VARCHAR(36) PRIMARY KEY,
  customer_id VARCHAR(36) NOT NULL,
  vehicle_id VARCHAR(36),
  driver_id VARCHAR(36),
  pickup_location VARCHAR(255),
  dropoff_location VARCHAR(255),
  booking_status ENUM('PENDING', 'CONFIRMED', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED'),
  start_time TIMESTAMP,
  end_time TIMESTAMP,
  estimated_fare DECIMAL(10,2),
  actual_fare DECIMAL(10,2),
  created_at TIMESTAMP,
  FOREIGN KEY (customer_id) REFERENCES customers(cust_id),
  FOREIGN KEY (vehicle_id) REFERENCES vehicles(vehicle_id),
  FOREIGN KEY (driver_id) REFERENCES drivers(driver_id)
);

CREATE TABLE booking_messages (
  message_id VARCHAR(36) PRIMARY KEY,
  booking_id VARCHAR(36) NOT NULL,
  message_type VARCHAR(50),          -- STATUS_UPDATE, DRIVER_LOCATION, etc.
  message_content TEXT,
  timestamp TIMESTAMP,
  FOREIGN KEY (booking_id) REFERENCES bookings(booking_id)
);
```

#### **payments Table**

```sql
CREATE TABLE payments (
  payment_id VARCHAR(36) PRIMARY KEY,
  booking_id VARCHAR(36),
  customer_id VARCHAR(36) NOT NULL,
  driver_id VARCHAR(36),
  amount DECIMAL(10,2),
  payment_method VARCHAR(50),        -- WALLET, CARD, CASH
  payment_status ENUM('PENDING', 'COMPLETED', 'FAILED', 'REFUNDED'),
  payment_timestamp TIMESTAMP,
  FOREIGN KEY (customer_id) REFERENCES customers(cust_id),
  FOREIGN KEY (driver_id) REFERENCES drivers(driver_id)
);
```

---

## 4. API Controllers - Detailed

### 🚗 **VehicleOwnerController** - Main Business Owner Management

**Base Path:** `/api/vehicle-owners`

#### **Key Endpoints:**

##### **1. Create/Update Vehicle Owner**

```
POST /api/vehicle-owners
Content-Type: application/json

Request:
{
  "userId": "user123",
  "businessName": "ABC Transport",
  "businessCategory": "PASSENGER_TRANSPORT",
  "city": "Mumbai",
  "panNumber": "XXXXX1234A",
  "gstNumber": "18AABCT1234B1Z5"
}

Response: 200 OK
{
  "ownerId": "owner-uuid",
  "userId": "user123",
  "businessName": "ABC Transport",
  "walletBalance": 0.00,
  "verificationStatus": "PENDING",
  "accountStatus": "ACTIVE",
  "createdAt": "2026-01-28T10:00:00"
}
```

##### **2. Get Owner Dashboard Statistics**

```
GET /api/vehicle-owners/{ownerId}/dashboard-stats
Authorization: Bearer <JWT_TOKEN>

Response: 200 OK
{
  "totalVehicles": 5,
  "activeVehicles": 4,
  "totalDrivers": 8,
  "activeDrivers": 7,
  "totalBookings": 142,
  "completedBookings": 140,
  "pendingPayments": 2,
  "walletBalance": 15000.00,
  "totalEarnings": 125000.00,
  "monthlyRevenue": 18500.00
}
```

##### **3. Wallet Operations**

**Add Funds:**

```
POST /api/vehicle-owners/{ownerId}/wallet/add
Content-Type: application/json

Request:
{
  "amount": 5000.00,
  "paymentMethod": "CREDIT_CARD",
  "transactionReference": "TXN123456"
}

Response: 200 OK
{
  "transactionId": "txn-uuid",
  "ownerId": "owner123",
  "previousBalance": 10000.00,
  "amount": 5000.00,
  "newBalance": 15000.00,
  "transactionType": "ADD",
  "timestamp": "2026-01-28T10:30:00"
}
```

**Deduct Funds:**

```
POST /api/vehicle-owners/{ownerId}/wallet/deduct
Content-Type: application/json

Request:
{
  "amount": 1000.00,
  "reason": "Payment released to driver",
  "driverId": "driver123"
}

Response: 200 OK
{
  "transactionId": "txn-uuid",
  "previousBalance": 15000.00,
  "amount": 1000.00,
  "newBalance": 14000.00,
  "transactionType": "DEDUCT",
  "reason": "Payment released to driver",
  "timestamp": "2026-01-28T10:35:00"
}
```

**Get Wallet Transaction History:**

```
GET /api/vehicle-owners/{ownerId}/wallet/transactions
Authorization: Bearer <JWT_TOKEN>

Response: 200 OK
[
  {
    "transactionId": "txn-001",
    "ownerId": "owner123",
    "amount": 5000.00,
    "transactionType": "ADD",
    "reason": "Manual recharge",
    "timestamp": "2026-01-28T10:00:00"
  },
  {
    "transactionId": "txn-002",
    "ownerId": "owner123",
    "amount": 1000.00,
    "transactionType": "DEDUCT",
    "reason": "Driver payment",
    "timestamp": "2026-01-28T10:30:00"
  }
]
```

##### **4. Driver Assignment**

**Assign Driver to Owner:**

```
POST /api/vehicle-owners/{ownerId}/drivers
Content-Type: application/json

Request:
{
  "driverId": "driver123",
  "vehicleId": "vehicle123",
  "assignmentDate": "2026-01-28",
  "status": "ACTIVE"
}

Response: 201 Created
{
  "assignmentId": "assign-uuid",
  "ownerId": "owner123",
  "driverId": "driver123",
  "vehicleId": "vehicle123",
  "assignmentDate": "2026-01-28",
  "status": "ACTIVE",
  "createdAt": "2026-01-28T10:00:00"
}
```

**Get Owner's Drivers:**

```
GET /api/vehicle-owners/{ownerId}/drivers
Authorization: Bearer <JWT_TOKEN>

Response: 200 OK
[
  {
    "driverId": "driver-001",
    "firstName": "Raj",
    "lastName": "Kumar",
    "licenseNumber": "DL123456",
    "phoneNumber": "+91-9876543210",
    "rating": 4.5,
    "totalRides": 156,
    "status": "ACTIVE",
    "assignedVehicle": "vehicle-001"
  }
]
```

##### **5. Verification Operations**

**Identity Verification:**

```
PUT /api/vehicle-owners/{ownerId}/identity-verification
Content-Type: application/json

Request:
{
  "panNumber": "XXXXX1234A",
  "panImageUrl": "https://bucket/pan.jpg",
  "aadharNumber": "XXXX XXXX 1234",
  "aadharImageUrl": "https://bucket/aadhar.jpg"
}

Response: 200 OK
{
  "ownerId": "owner123",
  "identityVerificationStatus": "PENDING",
  "identityVerifiedAt": null,
  "message": "Documents submitted for verification"
}
```

**Business Verification:**

```
PUT /api/vehicle-owners/{ownerId}/business-verification
Content-Type: application/json

Request:
{
  "gstNumber": "18AABCT1234B1Z5",
  "gstImageUrl": "https://bucket/gst.jpg",
  "businessLicenseUrl": "https://bucket/license.jpg",
  "businessProofUrl": "https://bucket/proof.jpg"
}

Response: 200 OK
{
  "ownerId": "owner123",
  "businessVerificationStatus": "UNDER_REVIEW",
  "message": "Business documents submitted for verification"
}
```

---

### 🚙 **VehicleController** - Vehicle Fleet Management

**Base Path:** `/api/vehicles`

#### **Key Endpoints:**

##### **1. Register Vehicle**

```
POST /api/vehicles
Content-Type: application/json
Authorization: Bearer <JWT_TOKEN>

Request:
{
  "ownerId": "owner123",
  "registrationNumber": "MH-02-AB-1234",
  "vehicleType": "CAR",
  "make": "Toyota",
  "model": "Fortuner",
  "capacity": 7,
  "color": "White",
  "engineNumber": "ENGINE123456",
  "chassisNumber": "CHASSIS123456",
  "manufacturingYear": 2023,
  "insuranceExpiry": "2027-01-28",
  "pollutionCertificateExpiry": "2028-01-28"
}

Response: 201 Created
{
  "vehicleId": "vehicle-uuid",
  "ownerId": "owner123",
  "registrationNumber": "MH-02-AB-1234",
  "vehicleType": "CAR",
  "capacity": 7,
  "status": "ACTIVE",
  "currentLocation": null,
  "createdAt": "2026-01-28T10:00:00"
}
```

##### **2. Get Available Vehicles**

```
GET /api/vehicles/available
Authorization: Bearer <JWT_TOKEN>

Response: 200 OK
[
  {
    "vehicleId": "vehicle-001",
    "ownerId": "owner123",
    "registrationNumber": "MH-02-AB-1234",
    "vehicleType": "CAR",
    "capacity": 7,
    "status": "AVAILABLE",
    "currentLocation": "Mumbai Central",
    "lastKnownLocation": "42.3601, -71.0589"
  },
  {
    "vehicleId": "vehicle-002",
    "ownerId": "owner123",
    "registrationNumber": "MH-02-AB-5678",
    "vehicleType": "VAN",
    "capacity": 12,
    "status": "AVAILABLE",
    "currentLocation": "Andheri Station",
    "lastKnownLocation": "42.3696, -71.0349"
  }
]
```

##### **3. Vehicle Status Updates**

**Update Vehicle Status:**

```
PUT /api/vehicles/{vehicleId}/status
Content-Type: application/json
Authorization: Bearer <JWT_TOKEN>

Request:
{
  "status": "MAINTENANCE",
  "reason": "Regular service scheduled",
  "estimatedReturnDate": "2026-02-05"
}

Response: 200 OK
{
  "vehicleId": "vehicle-001",
  "status": "MAINTENANCE",
  "statusChangedAt": "2026-01-28T10:00:00",
  "reason": "Regular service scheduled"
}
```

**Update Vehicle Location:**

```
PUT /api/vehicles/{vehicleId}/location
Content-Type: application/json
Authorization: Bearer <JWT_TOKEN>

Request:
{
  "latitude": 19.0760,
  "longitude": 72.8777,
  "address": "Bandra, Mumbai",
  "timestamp": "2026-01-28T10:30:00"
}

Response: 200 OK
{
  "vehicleId": "vehicle-001",
  "currentLocation": "Bandra, Mumbai",
  "coordinates": {
    "latitude": 19.0760,
    "longitude": 72.8777
  },
  "lastUpdated": "2026-01-28T10:30:00"
}
```

##### **4. Document Expiry Tracking**

**Get Vehicles with Expiring Documents:**

```
GET /api/vehicles/expiring-documents
Authorization: Bearer <JWT_TOKEN>

Response: 200 OK
[
  {
    "vehicleId": "vehicle-001",
    "registrationNumber": "MH-02-AB-1234",
    "insuranceExpiry": "2026-02-15",
    "daysUntilExpiry": 18,
    "pollutionCertificateExpiry": "2026-03-10",
    "status": "WARNING"
  },
  {
    "vehicleId": "vehicle-003",
    "registrationNumber": "MH-02-AB-9999",
    "insuranceExpiry": "2026-01-30",
    "daysUntilExpiry": 2,
    "pollutionCertificateExpiry": "2026-01-29",
    "status": "CRITICAL"
  }
]
```

##### **5. Vehicle Statistics**

**Get Owner's Vehicle Statistics:**

```
GET /api/vehicles/statistics/{ownerId}
Authorization: Bearer <JWT_TOKEN>

Response: 200 OK
{
  "ownerId": "owner123",
  "totalVehicles": 5,
  "vehiclesByType": {
    "CAR": 2,
    "VAN": 2,
    "BUS": 1
  },
  "vehiclesByStatus": {
    "ACTIVE": 4,
    "MAINTENANCE": 1,
    "INACTIVE": 0
  },
  "totalCapacity": 38,
  "averageCapacity": 7.6,
  "vehicleAgeDistribution": {
    "0-2_years": 3,
    "2-5_years": 2
  }
}
```

---

### 💳 **PaymentController** - Payment & Transaction Management

**Base Path:** `/api/payments`

#### **Key Endpoints:**

##### **1. Create Payment**

```
POST /api/payments
Content-Type: application/json
Authorization: Bearer <JWT_TOKEN>

Request:
{
  "bookingId": "booking123",
  "customerId": "customer123",
  "driverId": "driver123",
  "amount": 450.50,
  "paymentMethod": "WALLET",
  "paymentReference": "PAY123456"
}

Response: 201 Created
{
  "paymentId": "payment-uuid",
  "bookingId": "booking123",
  "customerId": "customer123",
  "driverId": "driver123",
  "amount": 450.50,
  "paymentMethod": "WALLET",
  "paymentStatus": "PENDING",
  "createdAt": "2026-01-28T10:00:00",
  "updatedAt": "2026-01-28T10:00:00"
}
```

##### **2. Get Customer Payments**

```
GET /api/payments/customer/{customerId}
Authorization: Bearer <JWT_TOKEN>

Response: 200 OK
[
  {
    "paymentId": "payment-001",
    "bookingId": "booking123",
    "customerId": "customer123",
    "amount": 450.50,
    "paymentStatus": "COMPLETED",
    "paymentMethod": "WALLET",
    "createdAt": "2026-01-28T09:00:00"
  }
]
```

##### **3. Release Payment to Driver**

```
POST /api/payments/{paymentId}/release
Content-Type: application/json
Authorization: Bearer <JWT_TOKEN>

Request:
{
  "releaseAmount": 400.00,
  "commissionAmount": 50.50,
  "releaseReason": "Ride completed successfully"
}

Response: 200 OK
{
  "paymentId": "payment-001",
  "releaseStatus": "RELEASED",
  "amountReleasedToDriver": 400.00,
  "commissionDeducted": 50.50,
  "releasedAt": "2026-01-28T10:30:00"
}
```

---

### 👤 **CustomerController** - Customer Account Management

**Base Path:** `/api/customers`

#### **Key Endpoints:**

##### **1. Get All Customers**

```
GET /api/customers
Authorization: Bearer <JWT_TOKEN>

Response: 200 OK
[
  {
    "custId": "customer-001",
    "userId": "user123",
    "firstName": "Priya",
    "lastName": "Sharma",
    "email": "priya@example.com",
    "phoneNumber": "+91-9876543210",
    "address": "123 Main St, Mumbai",
    "walletBalance": 1500.00,
    "totalRides": 42,
    "averageRating": 4.7,
    "createdAt": "2025-06-15T10:00:00"
  }
]
```

##### **2. Create Booking**

```
POST /api/customers/{customerId}/booking
Content-Type: application/json
Authorization: Bearer <JWT_TOKEN>

Request:
{
  "pickupLocation": "Bandra Station, Mumbai",
  "pickupLatitude": 19.0596,
  "pickupLongitude": 72.8295,
  "dropoffLocation": "Dadar Station, Mumbai",
  "dropoffLatitude": 19.0176,
  "dropoffLongitude": 72.8479,
  "vehicleType": "CAR",
  "preferredTime": "2026-01-28T11:00:00",
  "numberOfPassengers": 2,
  "specialRequests": "Need child seat"
}

Response: 201 Created
{
  "bookingId": "booking-uuid",
  "customerId": "customer123",
  "pickupLocation": "Bandra Station, Mumbai",
  "dropoffLocation": "Dadar Station, Mumbai",
  "bookingStatus": "PENDING",
  "estimatedFare": 285.00,
  "estimatedDuration": "25 mins",
  "createdAt": "2026-01-28T10:00:00"
}
```

##### **3. Get Booking History**

```
GET /api/customers/{customerId}/booking-history
Authorization: Bearer <JWT_TOKEN>

Response: 200 OK
[
  {
    "bookingId": "booking-001",
    "vehicleDetails": {
      "registrationNumber": "MH-02-AB-1234",
      "vehicleType": "CAR",
      "driverName": "Raj Kumar"
    },
    "pickupLocation": "Bandra Station",
    "dropoffLocation": "Dadar Station",
    "bookingStatus": "COMPLETED",
    "startTime": "2026-01-27T11:00:00",
    "endTime": "2026-01-27T11:25:00",
    "fare": 285.00,
    "rating": 5,
    "review": "Great ride, professional driver"
  }
]
```

---

### 🚗 **DriverController** - Driver Management

**Base Path:** `/api/transport/drivers`

#### **Key Features:**

- Driver registration and verification
- License validation
- Driver ratings and reviews
- Availability status management
- Earnings tracking

---

## 5. Service Layer

### **Architecture & Responsibilities**

Each service encapsulates business logic and coordinates between controllers and repositories:

```
VehicleOwnerService
├── saveVehicleOwner()
├── findOwnerById()
├── authenticateOwner()
├── getOwnersByVerificationStatus()
├── getVerifiedOwners()
├── addWalletBalance()          ← Wallet operations
├── deductWalletBalance()
├── getWalletTransactions()
├── assignDriver()              ← Driver assignment
└── getAssignmentHistory()

VehicleService
├── createVehicle()
├── findVehicleById()
├── getAvailableVehicles()
├── updateVehicleStatus()       ← Status tracking
├── updateVehicleLocation()     ← Location tracking
├── getVehiclesWithExpiringDocs()
├── updateServiceDate()
└── getVehicleStatistics()

CustomerService
├── createCustomer()
├── findCustomerById()
├── updateCustomerProfile()
├── updatePassword()
├── getBookingHistory()
├── getRatingAndReviews()
└── getWalletBalance()

BookingService
├── createBooking()
├── updateBookingStatus()       ← Core booking logic
├── getBookingDetails()
├── assignDriverToBooking()
├── getBookingsForDriver()
├── getBookingsForCustomer()
└── cancelBooking()

PaymentService
├── processPayment()
├── releasePaymentToDriver()
├── getPaymentHistory()
├── refundPayment()
└── updatePaymentStatus()

HistoryService & BackupService
├── logAssignmentHistory()
├── logPaymentHistory()
├── createBackup()
└── restoreBackup()
```

### **Example Service Implementation: VehicleOwnerService**

```java
@Service
@Transactional
public class VehicleOwnerService {

    @Autowired
    private VehicleOwnerRepository vehicleOwnerRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WalletTransactionRepository walletTransactionRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Create or update owner
    public VehicleOwner saveVehicleOwner(VehicleOwner owner) {
        // Validate user exists
        User user = userRepository.findById(owner.getUserId())
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        owner.setUser(user);
        owner.setCreatedAt(LocalDateTime.now());

        return vehicleOwnerRepository.save(owner);
    }

    // Authenticate owner with credentials
    public Optional<VehicleOwner> authenticateOwner(String credentials, String password) {
        Optional<VehicleOwner> owner = vehicleOwnerRepository
            .findByUserUsernameOrUserEmailOrUserPhoneNumber(
                credentials, credentials, credentials);

        if (owner.isPresent()) {
            User user = owner.get().getUser();
            if (passwordEncoder.matches(password, user.getPassword())) {
                return owner;
            }
        }
        return Optional.empty();
    }

    // Wallet operations
    public void addWalletBalance(String ownerId, BigDecimal amount) {
        VehicleOwner owner = vehicleOwnerRepository.findById(ownerId)
            .orElseThrow(() -> new ResourceNotFoundException("Owner not found"));

        BigDecimal newBalance = owner.getWalletBalance().add(amount);
        owner.setWalletBalance(newBalance);
        vehicleOwnerRepository.save(owner);

        // Log transaction
        WalletTransaction transaction = new WalletTransaction();
        transaction.setTransactionId(UUID.randomUUID().toString());
        transaction.setOwnerId(ownerId);
        transaction.setAmount(amount);
        transaction.setTransactionType(WalletTransaction.Type.ADD);
        transaction.setReason("Manual recharge");
        transaction.setTimestamp(LocalDateTime.now());

        walletTransactionRepository.save(transaction);
    }

    public void deductWalletBalance(String ownerId, BigDecimal amount, String reason) {
        VehicleOwner owner = vehicleOwnerRepository.findById(ownerId)
            .orElseThrow(() -> new ResourceNotFoundException("Owner not found"));

        if (owner.getWalletBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException("Insufficient wallet balance");
        }

        BigDecimal newBalance = owner.getWalletBalance().subtract(amount);
        owner.setWalletBalance(newBalance);
        vehicleOwnerRepository.save(owner);

        // Log transaction
        WalletTransaction transaction = new WalletTransaction();
        transaction.setTransactionId(UUID.randomUUID().toString());
        transaction.setOwnerId(ownerId);
        transaction.setAmount(amount);
        transaction.setTransactionType(WalletTransaction.Type.DEDUCT);
        transaction.setReason(reason);
        transaction.setTimestamp(LocalDateTime.now());

        walletTransactionRepository.save(transaction);
    }

    // Assign driver to owner's vehicle
    public AssignmentHistory assignDriver(String ownerId, String driverId, String vehicleId) {
        VehicleOwner owner = vehicleOwnerRepository.findById(ownerId)
            .orElseThrow();
        Driver driver = driverRepository.findById(driverId)
            .orElseThrow();
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
            .orElseThrow();

        AssignmentHistory assignment = new AssignmentHistory();
        assignment.setAssignmentId(UUID.randomUUID().toString());
        assignment.setOwnerId(ownerId);
        assignment.setDriverId(driverId);
        assignment.setVehicleId(vehicleId);
        assignment.setAssignmentDate(LocalDate.now());
        assignment.setStatus(AssignmentHistory.Status.ACTIVE);
        assignment.setCreatedAt(LocalDateTime.now());

        return assignmentHistoryRepository.save(assignment);
    }
}
```

---

## 6. Key Business Flows

### 📱 **Complete Booking Flow**

```
Step 1: Customer Initiates Booking
├─ POST /api/customers/{customerId}/booking
├─ Request: Pickup/Dropoff location, vehicle type, preferences
└─ Response: Booking created with status = PENDING

Step 2: System Matches Vehicle & Driver
├─ BookingService finds available vehicles
├─ Ranks vehicles by proximity/rating
├─ Assigns best match
└─ Updates booking.vehicleId, booking.driverId

Step 3: Driver Notification
├─ NotificationService sends push notification to driver
├─ Driver receives: Customer name, location, destination
└─ Driver has 30 seconds to accept/reject

Step 4: Driver Accepts Booking
├─ DriverController: PUT /api/drivers/{driverId}/accept-booking
├─ Booking status updated to IN_PROGRESS
├─ Real-time location sharing starts via WebSocket
└─ Customer sees driver's current location

Step 5: Driver Picks Up Customer
├─ Driver updates: "Arrived at pickup location"
├─ BookingMessage created with type = DRIVER_ARRIVED
├─ Customer receives notification

Step 6: Ride in Progress
├─ Real-time location updates via location tracking
├─ BookingMessages for ETA updates
├─ WebSocket broadcasts coordinates to customer

Step 7: Ride Completion
├─ Driver marks booking as COMPLETED
├─ System calculates actual fare
├─ PaymentController: POST /api/payments
├─ Payment status = PENDING (awaiting customer payment)

Step 8: Payment Processing
├─ Customer pays via wallet/card
├─ Payment status updated to COMPLETED
├─ Owner wallet deducts amount
├─ Driver earns amount (after commission)

Step 9: Rating & Review
├─ Customer rates driver (1-5 stars)
├─ Customer provides review text
├─ DriverReview entity created
├─ Driver rating updated (average)

Final State:
├─ Booking status = COMPLETED
├─ Payment status = COMPLETED
├─ Both driver & customer can view history
└─ Analytics updated in dashboard
```

### 💰 **Wallet & Payment Flow**

```
Owner Wallet Management:
├─ Initial Balance: 0.00
├─ Owner adds funds (PAYMENT_ADD)
│  └─ WalletTransaction: +5000 (timestamp, reference)
├─ Customer books ride
├─ Payment collected from customer
├─ Commission deducted (say 10%)
├─ Driver payment released
│  └─ WalletTransaction: -400 (driver payment)
└─ Final Balance = Previous + Additions - Deductions

Transaction History Query:
├─ GET /api/vehicle-owners/{ownerId}/wallet/transactions
├─ Returns: List sorted by timestamp
├─ Includes: Amount, Type (ADD/DEDUCT), Reason, Date
└─ Used for: Reconciliation, Tax reports
```

### 🚗 **Vehicle Assignment Flow**

```
Owner Assigns Driver to Vehicle:
├─ POST /api/vehicle-owners/{ownerId}/drivers
├─ Request contains: driverId, vehicleId
└─ System validates:
   ├─ Owner exists & is verified
   ├─ Driver exists & is verified
   ├─ Vehicle belongs to owner
   ├─ Driver not already assigned elsewhere
   └─ Vehicle not already in use

Assignment Created:
├─ AssignmentHistory record created
├─ Status = ACTIVE
├─ Start date recorded
└─ Can be modified later (reassign, unassign)

Assignment History Query:
├─ GET /api/vehicle-owners/{ownerId}/assignment-history
├─ Returns: List of all assignments (active & inactive)
└─ Used for: Compliance, driver rotation audits
```

---

## 7. Configuration & Infrastructure

### **⚙️ Spring Boot Configuration**

#### **application.properties**

```properties
# Server Configuration
server.port=8080
server.servlet.context-path=/
spring.application.name=Jr-Transport-Management-Backend

# Database Configuration (PostgreSQL)
spring.datasource.url=jdbc:postgresql://postgres.render.internal:5432/jrtransport_db
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA/Hibernate Configuration
spring.jpa.database=POSTGRESQL
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
spring.jpa.properties.hibernate.format_sql=true

# JWT Configuration
jwt.secret=${JWT_SECRET:your-super-secret-key-change-in-production}
jwt.expiration=86400000  # 24 hours in milliseconds

# CORS Configuration
cors.allowed-origins=http://localhost:4200,https://jr-transport.netlify.app

# Logging
logging.level.root=INFO
logging.level.com.transport.management=DEBUG

# WebSocket Configuration
spring.websocket.enabled=true
spring.websocket.allowed-origins=*

# Actuator
management.endpoints.web.exposure.include=health,info,metrics
```

#### **WebSocketConfig.java**

```java
@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(locationWebSocketHandler(), "/ws/location")
            .setAllowedOrigins("*");
        registry.addHandler(bookingNotificationHandler(), "/ws/bookings")
            .setAllowedOrigins("*");
    }

    @Bean
    public LocationWebSocketHandler locationWebSocketHandler() {
        return new LocationWebSocketHandler(); // Real-time location updates
    }

    @Bean
    public BookingNotificationHandler bookingNotificationHandler() {
        return new BookingNotificationHandler(); // Booking status updates
    }
}
```

### **🔧 Profiles Configuration**

#### **application-postgresql.properties** (Production)

```properties
spring.datasource.url=jdbc:postgresql://[PROD_HOST]:5432/[PROD_DB]
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}
spring.jpa.hibernate.ddl-auto=validate
logging.level.root=WARN
```

#### **application-cloud.properties** (Cloud Deployment)

```properties
server.port=${PORT:8080}
spring.datasource.hikari.connection-timeout=30000
spring.datasource.hikari.maximum-pool-size=5
spring.jpa.properties.hibernate.jdbc.batch_size=20
```

### **📦 Dependencies (pom.xml)**

```xml
<!-- Spring Boot Starters -->
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-web</artifactId>
</dependency>

<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-security</artifactId>
</dependency>

<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>

<!-- JWT Support -->
<dependency>
  <groupId>io.jsonwebtoken</groupId>
  <artifactId>jjwt-api</artifactId>
  <version>0.11.5</version>
</dependency>

<!-- Database -->
<dependency>
  <groupId>org.postgresql</groupId>
  <artifactId>postgresql</artifactId>
  <version>42.6.0</version>
</dependency>

<!-- WebSocket -->
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-websocket</artifactId>
</dependency>

<!-- Swagger/OpenAPI -->
<dependency>
  <groupId>org.springdoc</groupId>
  <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
  <version>2.0.0</version>
</dependency>

<!-- Validation -->
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-validation</artifactId>
</dependency>

<!-- Lombok (optional, for reducing boilerplate) -->
<dependency>
  <groupId>org.projectlombok</groupId>
  <artifactId>lombok</artifactId>
  <optional>true</optional>
</dependency>
```

### **🚀 Deployment**

**Render.com Deployment:**

```yaml
# render.yaml
services:
  - type: web
    name: jr-transport-backend
    env: java
    buildCommand: mvn clean package -DskipTests
    startCommand: java -jar target/jr-transport-0.0.1-SNAPSHOT.jar
    envVars:
      - key: JAVA_VERSION
        value: 17
      - key: DATABASE_URL
        fromDatabase:
          name: jrtransport_db
          property: connectionString
      - key: JWT_SECRET
        sync: false
      - key: SPRING_PROFILES_ACTIVE
        value: cloud,postgresql

databases:
  - name: jrtransport_db
    engine: postgres
    version: 12
    plan: standard
    preDeployCommand: |
      PGPASSWORD=$PGPASSWORD psql -h $PGHOST -U $PGUSER -d $PGDATABASE < init.sql
```

---

## 📈 Summary of Key Achievements

✅ **Robust Authentication:** JWT-based stateless auth with BCrypt password hashing  
✅ **Multi-Role System:** 6+ roles (Admin, Driver, Owner, Customer, Super Admin, Staff)  
✅ **Real-Time Features:** WebSocket for location tracking & booking notifications  
✅ **Financial System:** Wallet management with transaction history  
✅ **Verification Workflows:** Document verification for owners & drivers  
✅ **Analytics:** Dashboard statistics for all roles  
✅ **Scalable Design:** RESTful APIs, microservices-ready architecture  
✅ **Cloud-Ready:** Deployed on Render with PostgreSQL

---

**For Interview Discussion:** Focus on authentication flow, business logic (booking/payment), and architectural decisions. Mention trade-offs like stateless JWT vs session management, and how you chose components based on requirements.
