# Jr Transport Management - Architecture Viewer
# Run this script to see the project architecture without requiring external packages

import os
import sys
from datetime import datetime

def print_banner(text, width=80):
    """Create a banner with text"""
    print("=" * width)
    print(f"{text:^{width}}")
    print("=" * width)

def print_section(title, content, width=80):
    """Print a formatted section"""
    print("\n" + "=" * width)
    print(f" {title} ".center(width, "="))
    print("=" * width)
    print(content)
    print()

def show_project_structure():
    structure = """
    ┌─────────────────────────────────────────────────────────────────────────┐
    │                    Jr Transport Management System                        │
    │                         (Spring Boot 3.5.3)                           │
    └─────────────────────────────────────────────────────────────────────────┘
                                        │
                    ┌───────────────────┼───────────────────┐
                    │                   │                   │
    ┌───────────────▼──────────┐ ┌──────▼──────┐ ┌─────────▼──────────┐
    │    Controller Layer      │ │ Service Layer│ │  Repository Layer  │
    │     (REST APIs)          │ │(Business Logic)│ │   (Data Access)    │
    │                          │ │              │ │                    │
    │ • AuthController         │ │ • DriverService     │ │ • DriverRepository    │
    │ • DriverController       │ │ • VehicleService    │ │ • VehicleRepository   │
    │ • VehicleController      │ │ • VehicleOwnerService│ │ • BookingRepository   │
    │ • VehicleOwnerController │ │ • UserRoleService   │ │ • CustomerRepository  │
    └──────────────────────────┘ └─────────────┘ └────────────────────┘
                    │                   │                   │
                    └───────────────────┼───────────────────┘
                                        │
    ┌───────────────────────────────────▼───────────────────────────────────┐
    │                         Entity Layer                                   │
    │                       (JPA Entities)                                   │
    │                                                                        │
    │  • Driver         • Vehicle        • VehicleOwner                     │
    │  • Booking        • Customer       • MaintenanceRecord                │
    │  • ServiceCenter  • User          • Role                              │
    └────────────────────────────────────────────────────────────────────────┘
                                        │
    ┌───────────────────────────────────▼───────────────────────────────────┐
    │                       H2 Database                                      │
    │                    (In-Memory Development)                             │
    │                                                                        │
    │  Tables: drivers, vehicles, vehicle_owners, bookings, customers        │
    │  Configuration: application-local.properties                           │
    └────────────────────────────────────────────────────────────────────────┘

    Security & Configuration Components:
    ┌──────────────────┐  ┌──────────────────┐  ┌──────────────────┐
    │  Security Config │  │   JWT Auth       │  │  CORS Policy     │
    │  • WebSecurity   │  │  • Token Gen     │  │  • Cross-Origin  │
    │  • Filter Chain  │  │  • Validation    │  │  • Local Dev     │
    └──────────────────┘  └──────────────────┘  └──────────────────┘
    """
    
    print_banner("PROJECT STRUCTURE OVERVIEW")
    print(structure)
    
    print_section("LAYER RESPONSIBILITIES", """
    Controller Layer:  Handle HTTP requests, route to services, return responses
    Service Layer:     Business logic, transaction management, data validation
    Repository Layer:  Data access, JPA operations, custom queries
    Entity Layer:      Database mapping, relationships, JPA annotations
    Security Layer:    Authentication, authorization, JWT processing
    """)

def show_controller_layer():
    controller_diagram = """
    ┌─────────────────────────────────────────────────────────────────────────────────────┐
    │                              CONTROLLER LAYER                                      │
    │                          REST API Endpoints & Mappings                             │
    └─────────────────────────────────────────────────────────────────────────────────────┘

    ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐
    │  AuthController │  │ DriverController│  │VehicleController│  │VehicleOwnerCtrl │
    │                 │  │                 │  │                 │  │                 │
    │ POST /auth/login│  │ GET /drivers    │  │ GET /vehicles   │  │ GET /owners     │
    │ POST /auth/reg  │  │ POST /drivers   │  │ POST /vehicles  │  │ POST /owners    │
    │ POST /auth/refresh│ │ PUT /drivers/{id}│ │ PUT /vehicles/{id}│ │ PUT /owners/{id}│
    │ POST /auth/logout│ │ DEL /drivers/{id}│ │ GET /vehicles/  │  │ GET /owners/    │
    │                 │  │ GET /drivers/   │  │     owner/{id}  │  │     verified    │
    │                 │  │     status/{st} │  │ GET /vehicles/  │  │ POST /owners/   │
    │                 │  │ GET /drivers/   │  │     type/{type} │  │     wallet/add  │
    │                 │  │     available   │  │                 │  │                 │
    └─────────────────┘  └─────────────────┘  └─────────────────┘  └─────────────────┘
            │                       │                       │                       │
            │                       │                       │                       │
            ▼                       ▼                       ▼                       ▼
    ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐
    │   AuthService   │  │  DriverService  │  │ VehicleService  │  │VehicleOwnerSvc  │
    │                 │  │                 │  │                 │  │                 │
    │ • authenticate  │  │ • createDriver  │  │ • saveVehicle   │  │ • saveOwner     │
    │ • generateJWT   │  │ • updateDriver  │  │ • updateStatus  │  │ • authenticate  │
    │ • validateToken │  │ • findByStatus  │  │ • findByOwner   │  │ • addToWallet   │
    │ • refreshToken  │  │ • findAvailable │  │ • findByType    │  │ • getVerified   │
    └─────────────────┘  └─────────────────┘  └─────────────────┘  └─────────────────┘
    """
    
    print_banner("CONTROLLER LAYER ARCHITECTURE")
    print(controller_diagram)

def show_service_layer():
    service_diagram = """
    ┌─────────────────────────────────────────────────────────────────────────────────────┐
    │                               SERVICE LAYER                                         │
    │                        Business Logic & Transaction Management                      │
    └─────────────────────────────────────────────────────────────────────────────────────┘

    ┌─────────────────────────┐  ┌─────────────────────────┐  ┌─────────────────────────┐
    │      DriverService      │  │     VehicleService      │  │   VehicleOwnerService   │
    │     @Service            │  │      @Service           │  │       @Service          │
    │     @Transactional      │  │   @Transactional        │  │    @Transactional       │
    │                         │  │                         │  │                         │
    │ • createDriver()        │  │ • saveVehicle()         │  │ • saveVehicleOwner()    │
    │ • updateDriver()        │  │ • updateVehicleStatus() │  │ • authenticateOwner()   │
    │ • findByStatus()        │  │ • updateLocation()      │  │ • getByVerification()   │
    │ • findAvailableDrivers()│  │ • getVehiclesByOwner()  │  │ • getByAccountStatus()  │
    │ • findByVerification()  │  │ • getAvailableVehicles()│  │ • addToWallet()         │
    │ • findByLicenseType()   │  │ • getWithCapacity()     │  │ • getOwnersByCity()     │
    │ • findByCity()          │  │ • updateServiceDate()   │  │ • getVerifiedOwners()   │
    │ • findByMinRating()     │  │ • isVehicleAvailable()  │  │ • getDashboardStats()   │
    │ • updateAccountStatus() │  │ • getVehicleStats()     │  │                         │
    └─────────────────────────┘  └─────────────────────────┘  └─────────────────────────┘
                │                              │                              │
                ▼                              ▼                              ▼
    ┌─────────────────────────┐  ┌─────────────────────────┐  ┌─────────────────────────┐
    │    DriverRepository     │  │   VehicleRepository     │  │ VehicleOwnerRepository  │
    │      @Repository        │  │     @Repository         │  │      @Repository        │
    │                         │  │                         │  │                         │
    │ extends JpaRepository   │  │ extends JpaRepository   │  │ extends JpaRepository   │
    │   <Driver, String>      │  │   <Vehicle, String>     │  │ <VehicleOwner, String>  │
    └─────────────────────────┘  └─────────────────────────┘  └─────────────────────────┘
    """
    
    print_banner("SERVICE LAYER ARCHITECTURE")
    print(service_diagram)
    
    print_section("TRANSACTION MANAGEMENT", """
    @Transactional Annotations:
    • ACID Properties: Atomicity, Consistency, Isolation, Durability
    • Automatic Rollback: On exceptions and errors
    • Read-Only Operations: For performance optimization
    • Propagation Control: REQUIRED, REQUIRES_NEW, SUPPORTS, etc.
    
    Dependency Injection:
    • @Autowired: Automatic dependency injection
    • Constructor Injection: Preferred method for required dependencies
    • Service-to-Service Communication: Cross-layer coordination
    """)

def show_repository_layer():
    repository_diagram = """
    ┌─────────────────────────────────────────────────────────────────────────────────────┐
    │                             REPOSITORY LAYER                                        │
    │                        Data Access & JPA Integration                                │
    └─────────────────────────────────────────────────────────────────────────────────────┘

    ┌─────────────────────────────────────────────────────────────────────────────────────┐
    │                           JpaRepository<T, ID>                                     │
    │                           (Spring Data JPA)                                        │
    │                                                                                     │
    │  Inherited Methods: findAll(), findById(), save(), delete(), count(), exists()     │
    └─────────────────────────────────────────────────────────────────────────────────────┘
                                          │
                    ┌─────────────────────┼─────────────────────┐
                    │                     │                     │
                    ▼                     ▼                     ▼
    ┌─────────────────────────┐  ┌─────────────────────────┐  ┌─────────────────────────┐
    │   DriverRepository      │  │  VehicleRepository      │  │  BookingRepository      │
    │     @Repository         │  │    @Repository          │  │    @Repository          │
    │                         │  │                         │  │                         │
    │ Custom Query Methods:   │  │ Custom Query Methods:   │  │ Custom Query Methods:   │
    │ • findByStatus()        │  │ • findByOwnerId()       │  │ • findByCustomerId()    │
    │ • findByVerification()  │  │ • findByDriverId()      │  │ • findByDriverId()      │
    │ • findByLicenseType()   │  │ • findByVehicleNumber() │  │ • findByVehicleId()     │
    │ • findByCity()          │  │ • findByStatus()        │  │ • findByStatus()        │
    │ • findByState()         │  │ • findByVehicleType()   │  │ • findActiveBookings()  │
    │ • findByMinRating()     │  │ • findByLocation()      │  │ • findByDateRange()     │
    │                         │  │ • findExpiringDocs()    │  │ • findOverdueBookings() │
    │ @Query Annotations:     │  │                         │  │                         │
    │ • findTopRatedDrivers() │  │ @Query Annotations:     │  │ @Query Annotations:     │
    │ • findWithExpiring()    │  │ • findByLocationCity()  │  │ • findByDepartureCity() │
    └─────────────────────────┘  └─────────────────────────┘  └─────────────────────────┘
                │                              │                              │
                ▼                              ▼                              ▼
    ┌─────────────────────────┐  ┌─────────────────────────┐  ┌─────────────────────────┐
    │      drivers table      │  │     vehicles table      │  │     bookings table      │
    │                         │  │                         │  │                         │
    │     H2 Database         │  │     H2 Database         │  │     H2 Database         │
    │    (In-Memory)          │  │    (In-Memory)          │  │    (In-Memory)          │
    └─────────────────────────┘  └─────────────────────────┘  └─────────────────────────┘
    """
    
    print_banner("REPOSITORY LAYER ARCHITECTURE")
    print(repository_diagram)

def show_entity_relationships():
    entity_diagram = """
    ┌─────────────────────────────────────────────────────────────────────────────────────┐
    │                            ENTITY RELATIONSHIPS                                     │
    │                         JPA Configuration & Schema                                  │
    └─────────────────────────────────────────────────────────────────────────────────────┘

    ┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
    │     Driver      │    │     Vehicle     │    │  VehicleOwner   │
    │                 │    │                 │    │                 │
    │ @Entity         │    │ @Entity         │    │ @Entity         │
    │ @Table(drivers) │    │ @Table(vehicles)│    │ @Table(owners)  │
    │                 │    │                 │    │                 │
    │ @Id String id   │    │ @Id String id   │    │ @Id String id   │
    │ String userId   │    │ String number   │    │ String firstName│
    │ String firstName│    │ String ownerId  │◄───┤ String lastName │
    │ String lastName │    │ String driverId │    │ String email    │
    │ String email    │    │ VehicleType type│    │ String phone    │
    │ String phone    │    │ VehicleStatus   │    │ BigDecimal      │
    │ DriverStatus    │    │ @Column("`year`")│    │   walletBalance │
    │ LicenseType     │    │ Integer year    │    │ @Embedded       │
    │ BigDecimal      │    │ String model    │    │   Address       │
    │   avgRating     │    │ String brand    │    │ @Embedded       │
    │ @Embedded       │    │ BigDecimal      │    │   IdentityProof │
    │   Address       │    │   capacity      │    │                 │
    │ @CreationTime   │    │ @Embedded       │    │ @CreationTime   │
    │   createdAt     │    │   DocumentInfo  │    │   createdAt     │
    └─────────────────┘    │ @Embedded       │    └─────────────────┘
            │              │   InsuranceInfo │               │
            │              │ Boolean isActive│               │
            │              │ Boolean verified│               │
            │              │ @CreationTime   │               │
            │              │   createdAt     │               │
            │              └─────────────────┘               │
            │                       │                        │
            │    ┌──────────────────┘                        │
            │    │                                           │
            └────▼─────────           ────────────────────────┘
    ┌─────────────────┐           ┌─────────────────┐
    │     Booking     │           │ MaintenanceRec  │
    │                 │           │                 │
    │ @Entity         │           │ @Entity         │
    │ @Table(bookings)│           │ @Table(maint)   │
    │                 │           │                 │
    │ @Id String id   │           │ @Id String id   │
    │ String customerId│          │ String vehicleId│
    │ String driverId │           │ String desc     │
    │ String vehicleId│           │ BigDecimal cost │
    │ String ownerId  │           │ LocalDate date  │
    │ BookingStatus   │           │ MaintenanceType │
    │ PaymentStatus   │           │ String center   │
    │ @Embedded       │           │ @ForeignKey     │
    │   pickupAddress │           │ (NO_CONSTRAINT) │
    │ @Embedded       │           │                 │
    │   deliveryAddr  │           │                 │
    │ @Embedded       │           │                 │
    │   PricingInfo   │           │                 │
    │ LocalDateTime   │           │                 │
    │   scheduledTime │           │                 │
    └─────────────────┘           └─────────────────┘
    """
    
    print_banner("ENTITY RELATIONSHIPS & JPA CONFIGURATION")
    print(entity_diagram)
    
    print_section("JPA ANNOTATIONS REFERENCE", """
    Core JPA Annotations:
    @Entity              - Marks class as JPA entity
    @Table(name="...")   - Maps to database table
    @Id                  - Primary key field
    @GeneratedValue      - Auto-generated values (UUID strategy)
    @Column              - Custom column mapping
    @Embedded            - Embedded objects (Address, DocumentInfo, etc.)
    @CreationTimestamp   - Automatic timestamp creation
    @UpdateTimestamp     - Automatic timestamp updates
    @ForeignKey          - Foreign key constraints
    
    Relationship Annotations:
    @OneToMany           - One-to-many relationships
    @ManyToOne           - Many-to-one relationships
    @OneToOne            - One-to-one relationships
    @ManyToMany          - Many-to-many relationships
    @JoinColumn          - Foreign key column specification
    """)

def show_security_architecture():
    security_diagram = """
    ┌─────────────────────────────────────────────────────────────────────────────────────┐
    │                            SECURITY ARCHITECTURE                                    │
    │                        JWT Authentication & Authorization                           │
    └─────────────────────────────────────────────────────────────────────────────────────┘

    Client Request Flow:
    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐
    │   Client    │───▶│ Security    │───▶│    JWT      │───▶│    Auth     │
    │   Request   │    │ Filter      │    │ Validation  │    │  Manager    │
    │             │    │ Chain       │    │             │    │             │
    └─────────────┘    └─────────────┘    └─────────────┘    └─────────────┘
            │                   │                   │                   │
            │                   ▼                   ▼                   ▼
            │          ┌─────────────┐    ┌─────────────┐    ┌─────────────┐
            │          │UserDetails  │    │ Controller  │    │  Business   │
            │          │ Service     │◄───│   Access    │◄───│   Logic     │
            │          │             │    │             │    │             │
            │          └─────────────┘    └─────────────┘    └─────────────┘
            │                   │
            └───────────────────┘

    Security Components:
    ┌─────────────────────────┐  ┌─────────────────────────┐  ┌─────────────────────────┐
    │   WebSecurityConfig     │  │ JwtAuthenticationFilter │  │       JwtUtil           │
    │                         │  │                         │  │                         │
    │ @Configuration          │  │ OncePerRequestFilter    │  │ Token Generation        │
    │ @EnableWebSecurity      │  │                         │  │ Token Validation        │
    │                         │  │ • Extract JWT header    │  │ Claims Extraction       │
    │ SecurityFilterChain     │  │ • Validate signature    │  │ Expiration Handling     │
    │ CORS Configuration      │  │ • Set SecurityContext   │  │ Secret Key Management   │
    │ JWT Filter Registration │  │ • Forward to next       │  │                         │
    │ Public Endpoints:       │  │                         │  │ jwt.secret=...          │
    │ • /api/auth/**          │  │                         │  │ jwt.expiration=86400000 │
    │ • /h2-console/**        │  │                         │  │ (24 hours)              │
    └─────────────────────────┘  └─────────────────────────┘  └─────────────────────────┘
    """
    
    print_banner("SECURITY ARCHITECTURE")
    print(security_diagram)
    
    print_section("JWT TOKEN STRUCTURE", """
    JWT Token Components (from application-local.properties):
    
    Header:    {"alg":"HS256","typ":"JWT"}
    Payload:   {"sub":"userId","iat":timestamp,"exp":timestamp,"roles":["USER"]}
    Signature: HMACSHA256(base64UrlEncode(header) + "." + base64UrlEncode(payload), secret)
    
    Configuration:
    • Secret Key: jwt.secret=myDefaultSecretKey... (Base64 encoded)
    • Expiration: jwt.expiration=86400000 (24 hours)
    • Algorithm: HS256 (HMAC SHA-256)
    
    Security Annotations:
    • @PreAuthorize("hasRole('ADMIN')")  - Method-level security
    • @Secured("ROLE_USER")              - Role-based access
    • @RolesAllowed({"ADMIN", "USER"})   - JSR-250 annotations
    • @CrossOrigin(origins="...")        - CORS configuration
    """)

def show_configuration_analysis():
    config_diagram = """
    ┌─────────────────────────────────────────────────────────────────────────────────────┐
    │                         APPLICATION CONFIGURATION                                   │
    │                        application-local.properties                                 │
    └─────────────────────────────────────────────────────────────────────────────────────┘

    Database Configuration:
    ┌─────────────────────────────────────────────────────────────────────────────────────┐
    │ spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1                         │
    │ spring.datasource.driverClassName=org.h2.Driver                                    │
    │ spring.datasource.username=sa                                                      │
    │ spring.datasource.password=                                                        │
    │ spring.h2.console.enabled=true                                                     │
    │ spring.jpa.database-platform=org.hibernate.dialect.H2Dialect                      │
    └─────────────────────────────────────────────────────────────────────────────────────┘

    JPA/Hibernate Configuration:
    ┌─────────────────────────────────────────────────────────────────────────────────────┐
    │ spring.jpa.hibernate.ddl-auto=create-drop                                          │
    │ spring.jpa.show-sql=true                                                           │
    │ spring.jpa.properties.hibernate.format_sql=true                                    │
    │ spring.jpa.defer-datasource-initialization=false                                   │
    │ spring.jpa.open-in-view=false                                                      │
    │ spring.jpa.properties.hibernate.order_updates=true                                 │
    │ spring.jpa.properties.hibernate.order_inserts=true                                 │
    │ spring.jpa.properties.hibernate.hbm2ddl.halt_on_error=false                        │
    └─────────────────────────────────────────────────────────────────────────────────────┘

    Security Configuration:
    ┌─────────────────────────────────────────────────────────────────────────────────────┐
    │ jwt.secret=myDefaultSecretKey123456789012345678901234567890123456789012345678901234 │
    │ jwt.expiration=86400000                                                             │
    │ cors.allowed-origins=http://localhost:3000,http://localhost:4200                   │
    └─────────────────────────────────────────────────────────────────────────────────────┘

    Server Configuration:
    ┌─────────────────────────────────────────────────────────────────────────────────────┐
    │ server.port=8080                                                                   │
    │ spring.application.name=playschool-management-backend                              │
    │ spring.servlet.multipart.max-file-size=10MB                                        │
    │ spring.servlet.multipart.max-request-size=10MB                                     │
    └─────────────────────────────────────────────────────────────────────────────────────┘
    """
    
    print_banner("CONFIGURATION ANALYSIS")
    print(config_diagram)
    
    print_section("CONFIGURATION IMPACT", """
    H2 Database Impact:
    • In-memory storage for fast development
    • Automatic schema creation/destruction
    • No persistence between application restarts
    • H2 console available at /h2-console
    
    Hibernate DDL Impact:
    • create-drop: Auto-create schema on startup, drop on shutdown
    • show-sql: Display SQL queries in logs
    • format_sql: Pretty-print SQL for debugging
    • order_updates/inserts: Optimize batch operations
    
    JWT Security Impact:
    • Stateless authentication (no server-side sessions)
    • Token-based access control
    • 24-hour token expiration
    • HMAC SHA-256 signature algorithm
    
    CORS Policy Impact:
    • Allows frontend applications on ports 3000 and 4200
    • Enables cross-origin requests for local development
    • Supports React, Angular, and other SPA frameworks
    """)

def show_complete_flow():
    flow_diagram = """
    ┌─────────────────────────────────────────────────────────────────────────────────────┐
    │                           COMPLETE ARCHITECTURE FLOW                               │
    │                      Request-Response Lifecycle Through All Layers                 │
    └─────────────────────────────────────────────────────────────────────────────────────┘

    Request Flow (Client → Database):
    ┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
    │   Client Layer  │───▶│  Load Balancer  │───▶│ Security Layer  │
    │                 │    │    /Gateway     │    │                 │
    │ • React/Angular │    │ • Spring Cloud  │    │ • JWT Filter    │
    │ • Mobile App    │    │ • Nginx         │    │ • CORS Filter   │
    │ • API Client    │    │ • API Gateway   │    │ • Auth Manager  │
    └─────────────────┘    └─────────────────┘    └─────────────────┘
            │                        │                        │
            │                        ▼                        ▼
            │              HTTP Request          Security Check
            │            (GET /api/vehicles)     (JWT Validation)
            │                        │                        │
            ▼                        ▼                        ▼
    ┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
    │ Controller Layer│◄───│  Service Layer  │◄───│Repository Layer │
    │                 │    │                 │    │                 │
    │ • AuthController│    │ • DriverService │    │ • DriverRepo    │
    │ • DriverCtrl    │    │ • VehicleService│    │ • VehicleRepo   │
    │ • VehicleCtrl   │    │ • OwnerService  │    │ • BookingRepo   │
    │ • OwnerCtrl     │    │ • UserService   │    │ • CustomerRepo  │
    └─────────────────┘    └─────────────────┘    └─────────────────┘
            │                        │                        │
            ▼                        ▼                        ▼
    Route to Controller      Business Logic          Data Access
      (@GetMapping)         (@Transactional)       (JPA Repository)
            │                        │                        │
            │                        │                        ▼
            │                        │              ┌─────────────────┐
            │                        │              │  Database Layer │
            │                        │              │                 │
            │                        │              │ • H2 In-Memory  │
            │                        │              │ • JPA/Hibernate │
            │                        │              │ • Connection    │
            │                        │              │   Pool          │
            │                        │              └─────────────────┘
            │                        │                        │
            │                        │                        ▼
            │                        │              Database Query
            │                        │                (SQL/JPQL)
            │                        │                        │
            │                        │              ┌─────────────────┐
            │                        │              │                 │
            │                        │              │ Response Flow:  │
            │                        │              │ • SQL Result    │
            │                        │              │ • Entity Objects│
            │                        │              │ • Business Objs │
            │                        │              │ • JSON Response │
            │                        │              │ • Security Hdrs │
            │                        │              │ • HTTP Response │
            │                        │              │ • UI Update     │
            │                        │              │                 │
            │                        │              └─────────────────┘
            │                        │
            └────────────────────────┘

    Cross-Cutting Concerns:
    ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐
    │ Exception       │  │ Validation      │  │ Logging         │  │ Caching         │
    │ Handling        │  │                 │  │                 │  │                 │
    │ @ControllerAdv  │  │ @Valid          │  │ @Slf4j          │  │ @Cacheable      │
    │ Global Error    │  │ @Validated      │  │ Logback         │  │ Performance     │
    │ Management      │  │ Input Validation│  │ Debug Traces    │  │ Optimization    │
    └─────────────────┘  └─────────────────┘  └─────────────────┘  └─────────────────┘
    """
    
    print_banner("COMPLETE ARCHITECTURE FLOW")
    print(flow_diagram)

def show_technology_stack():
    tech_stack = """
    ┌─────────────────────────────────────────────────────────────────────────────────────┐
    │                            TECHNOLOGY STACK                                         │
    └─────────────────────────────────────────────────────────────────────────────────────┘

    Core Framework:
    • Spring Boot 3.5.3        - Main application framework
    • Spring Data JPA          - Data access layer
    • Spring Security          - Authentication & authorization
    • Spring Web               - REST API development

    Database & Persistence:
    • H2 Database 2.3.232      - In-memory database for development
    • Hibernate 6.6.18         - ORM and entity management
    • JPA (Jakarta Persistence) - Persistence API standard

    Security & Authentication:
    • JWT (JSON Web Tokens)    - Stateless authentication
    • HMAC SHA-256             - Token signature algorithm
    • CORS                     - Cross-origin resource sharing

    Build & Dependency Management:
    • Maven                    - Build tool and dependency management
    • Java 17                  - Programming language version

    Development Tools:
    • Lombok                   - Code generation (getters/setters)
    • Swagger/OpenAPI         - API documentation
    • Validation API          - Input validation

    Configuration:
    • YAML/Properties files    - Application configuration
    • Profiles                 - Environment-specific settings
    • Auto-configuration       - Spring Boot conventions
    """
    
    print(tech_stack)

def main():
    """Main function to display all architecture diagrams"""
    print_banner("Jr Transport Management - Architecture Overview")
    print(f"Generated on: {datetime.now().strftime('%Y-%m-%d %H:%M:%S')}")
    print("\nThis script shows the complete architecture of your Spring Boot application")
    print("without requiring any external packages (matplotlib, etc.)")
    
    while True:
        print("\n" + "="*80)
        print("Choose a section to view:")
        print("1. Project Structure Overview")
        print("2. Controller Layer Architecture")
        print("3. Service Layer Design")
        print("4. Repository Layer & Database Mapping")
        print("5. Entity Relationships & JPA Configuration")
        print("6. Security Architecture")
        print("7. Configuration Analysis")
        print("8. Complete Architecture Flow")
        print("9. Technology Stack")
        print("0. Exit")
        print("="*80)
        
        try:
            choice = input("\nEnter your choice (0-9): ").strip()
            
            if choice == '0':
                print("\nThank you for viewing the Jr Transport Management Architecture!")
                break
            elif choice == '1':
                show_project_structure()
            elif choice == '2':
                show_controller_layer()
            elif choice == '3':
                show_service_layer()
            elif choice == '4':
                show_repository_layer()
            elif choice == '5':
                show_entity_relationships()
            elif choice == '6':
                show_security_architecture()
            elif choice == '7':
                show_configuration_analysis()
            elif choice == '8':
                show_complete_flow()
            elif choice == '9':
                show_technology_stack()
            else:
                print("Invalid choice. Please enter a number between 0-9.")
                
            input("\nPress Enter to continue...")
            
        except KeyboardInterrupt:
            print("\n\nExiting...")
            break
        except Exception as e:
            print(f"An error occurred: {e}")

if __name__ == "__main__":
    main()
