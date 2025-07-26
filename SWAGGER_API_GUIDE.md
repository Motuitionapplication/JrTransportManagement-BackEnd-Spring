# Jr Transport Management Backend API

## Overview
This is the Spring Boot backend for the Jr Transport Management System, providing REST APIs for managing vehicles, owners, drivers, customers, and bookings.

## 🚀 Getting Started

### Prerequisites
- Java 17 or higher
- Maven 3.6 or higher
- PostgreSQL database (or H2 for testing)

### Running the Application

1. **Clone and Navigate to Backend Directory**
   ```bash
   cd D:\GKCT_WorkSpace\GKCT_Deploy_branch_final\backend\JrTransportManagement-BackEnd-Spring
   ```

2. **Build the Application**
   ```bash
   mvn clean compile
   ```

3. **Run the Application**
   ```bash
   mvn spring-boot:run
   ```

   Or use the VS Code task: `Ctrl+Shift+P` → "Tasks: Run Task" → "Start Spring Boot Application"

4. **Application URLs**
   - **Base API URL**: `http://localhost:8080/api/transport`
   - **Swagger UI**: `http://localhost:8080/api/transport/swagger-ui.html`
   - **API Documentation**: `http://localhost:8080/api/transport/api-docs`

## 📚 API Documentation with Swagger

### Accessing Swagger UI
1. Start the application
2. Open your browser and navigate to: `http://localhost:8080/api/transport/swagger-ui.html`
3. You'll see the interactive API documentation with all available endpoints

### Available API Endpoints

#### 🚗 Vehicle Management (`/api/vehicles`)
- **POST** `/api/vehicles` - Create or update a vehicle
- **GET** `/api/vehicles` - Get all vehicles
- **GET** `/api/vehicles/{vehicleId}` - Get vehicle by ID
- **GET** `/api/vehicles/owner/{ownerId}` - Get vehicles by owner
- **GET** `/api/vehicles/type/{vehicleType}` - Get vehicles by type
- **GET** `/api/vehicles/available` - Get available vehicles
- **GET** `/api/vehicles/location` - Get vehicles by location
- **GET** `/api/vehicles/capacity` - Get vehicles with minimum capacity
- **GET** `/api/vehicles/expiring-documents` - Get vehicles with expiring documents
- **PUT** `/api/vehicles/{vehicleId}/status` - Update vehicle status
- **PUT** `/api/vehicles/{vehicleId}/location` - Update vehicle location
- **PUT** `/api/vehicles/{vehicleId}/service-date` - Update next service date
- **PUT** `/api/vehicles/{vehicleId}/active-status` - Update active status
- **GET** `/api/vehicles/{vehicleId}/availability` - Check vehicle availability
- **DELETE** `/api/vehicles/{vehicleId}` - Delete vehicle
- **GET** `/api/vehicles/statistics/{ownerId}` - Get vehicle statistics

#### 👤 Vehicle Owner Management (`/api/vehicle-owners`)
- **POST** `/api/vehicle-owners` - Create or update a vehicle owner
- **GET** `/api/vehicle-owners` - Get all vehicle owners
- **GET** `/api/vehicle-owners/{ownerId}` - Get owner by ID
- **POST** `/api/vehicle-owners/authenticate` - Authenticate owner
- **GET** `/api/vehicle-owners/verification-status/{status}` - Get owners by verification status
- **GET** `/api/vehicle-owners/account-status/{status}` - Get owners by account status
- **GET** `/api/vehicle-owners/verified` - Get verified owners
- **GET** `/api/vehicle-owners/city/{city}` - Get owners by city
- **GET** `/api/vehicle-owners/wallet-balance` - Get owners with wallet balance
- **PUT** `/api/vehicle-owners/{ownerId}/verification-status` - Update verification status
- **PUT** `/api/vehicle-owners/{ownerId}/account-status` - Update account status
- **POST** `/api/vehicle-owners/{ownerId}/wallet/add` - Add money to wallet
- **POST** `/api/vehicle-owners/{ownerId}/wallet/deduct` - Deduct money from wallet
- **PUT** `/api/vehicle-owners/{ownerId}/identity-verification` - Update identity verification
- **PUT** `/api/vehicle-owners/{ownerId}/business-verification` - Update business verification
- **PUT** `/api/vehicle-owners/{ownerId}/change-password` - Change password
- **GET** `/api/vehicle-owners/{ownerId}/wallet/transactions` - Get wallet transactions
- **GET** `/api/vehicle-owners/check-email` - Check if email exists
- **GET** `/api/vehicle-owners/check-phone` - Check if phone exists
- **GET** `/api/vehicle-owners/check-userid` - Check if user ID exists
- **DELETE** `/api/vehicle-owners/{ownerId}` - Delete owner
- **GET** `/api/vehicle-owners/{ownerId}/dashboard-stats` - Get dashboard statistics

## 🧪 Testing with Swagger

### 1. Create a Vehicle Owner
1. Navigate to **Vehicle Owner Management** section
2. Click on `POST /api/vehicle-owners`
3. Click "Try it out"
4. Use this sample JSON:
```json
{
  "ownerId": "owner001",
  "userId": "john_doe",
  "email": "john@example.com",
  "phoneNumber": "+1234567890",
  "password": "password123",
  "firstName": "John",
  "lastName": "Doe",
  "dateOfBirth": "1985-05-15",
  "gender": "MALE",
  "address": {
    "street": "123 Main St",
    "city": "New York",
    "state": "NY",
    "pincode": "10001",
    "country": "USA"
  },
  "walletBalance": 1000.00,
  "verificationStatus": "PENDING",
  "accountStatus": "ACTIVE"
}
```

### 2. Create a Vehicle
1. Navigate to **Vehicle Management** section
2. Click on `POST /api/vehicles`
3. Click "Try it out"
4. Use this sample JSON:
```json
{
  "id": "vehicle001",
  "vehicleNumber": "ABC123",
  "vehicleType": "TRUCK",
  "model": "Ford Transit",
  "manufacturer": "Ford",
  "year": 2022,
  "capacity": 5000.00,
  "ownerId": "owner001",
  "registration": {
    "number": "REG123456",
    "expiryDate": "2025-12-31",
    "documentUrl": "https://example.com/reg.pdf"
  },
  "insurance": {
    "number": "INS789012",
    "expiryDate": "2025-06-30",
    "documentUrl": "https://example.com/insurance.pdf",
    "provider": "ABC Insurance"
  },
  "currentLatitude": 40.7128,
  "currentLongitude": -74.0060,
  "currentAddress": "New York, NY",
  "status": "AVAILABLE",
  "fareDetails": {
    "perKmRate": 5.50,
    "wholeFare": 1000.00,
    "sharingFare": 500.00,
    "gstIncluded": true,
    "movingInsurance": 100.00
  },
  "isActive": true
}
```

### 3. Test API Endpoints
- **Get all vehicles**: `GET /api/vehicles`
- **Get vehicles by owner**: `GET /api/vehicles/owner/owner001`
- **Check vehicle availability**: `GET /api/vehicles/vehicle001/availability`
- **Update vehicle status**: `PUT /api/vehicles/vehicle001/status`

## 🔧 Configuration

### Database Configuration
The application is configured to use PostgreSQL. Update the database settings in `application.properties`:

```properties
# For PostgreSQL
spring.datasource.url=jdbc:postgresql://localhost:5432/jr_transport_db
spring.datasource.username=your_username
spring.datasource.password=your_password

# For H2 (testing)
# spring.datasource.url=jdbc:h2:mem:testdb
# spring.h2.console.enabled=true
```

### Swagger Configuration
Swagger is automatically configured and available at:
- UI: `http://localhost:8080/api/transport/swagger-ui.html`
- JSON: `http://localhost:8080/api/transport/api-docs`

## 📝 Features Implemented

✅ **Vehicle Management**
- CRUD operations for vehicles
- Document management (registration, insurance, permits)
- Location tracking
- Status management
- Capacity and availability checking

✅ **Vehicle Owner Management**
- Owner registration and authentication
- Wallet management (add/deduct funds)
- Identity and business verification
- Dashboard statistics

✅ **API Documentation**
- Complete Swagger/OpenAPI documentation
- Interactive testing interface
- Detailed parameter descriptions
- Response examples

✅ **Data Validation**
- Input validation using Bean Validation
- Custom error handling
- Comprehensive logging

## 🚀 Next Steps

To extend the API further, you can:

1. **Add Driver Management** - Create similar controllers for drivers
2. **Add Customer Management** - Implement customer-related endpoints
3. **Add Booking Management** - Create booking workflow APIs
4. **Add Authentication** - Implement JWT-based security
5. **Add File Upload** - Support document uploads
6. **Add Email/SMS** - Notification services

## 🔍 Troubleshooting

### Common Issues

1. **Port Already in Use**
   ```bash
   # Change port in application.properties
   server.port=8081
   ```

2. **Database Connection Issues**
   - Ensure PostgreSQL is running
   - Check database credentials
   - Verify database exists

3. **Swagger UI Not Loading**
   - Check if application started successfully
   - Verify URL: `http://localhost:8080/api/transport/swagger-ui.html`

### Logs
Check application logs for detailed error information:
```bash
mvn spring-boot:run
```

## 📞 Support

For issues or questions:
- Check the logs for detailed error messages
- Verify all dependencies are correctly installed
- Ensure the database is properly configured and running

Happy testing! 🎉
