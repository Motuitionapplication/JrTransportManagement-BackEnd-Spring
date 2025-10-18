# Repo Audit Report

- Generated: 2025-10-15T11:32:18.080Z
- CWD: D:\GKCT\JrTransportManagement-BackEnd-Spring
- Options: frontend=JrTransportManagement-Frontend-Angular, backend=JrTransportManagement-BackEnd-Spring

## Frontend
- Directory: D:\GKCT\JrTransportManagement-BackEnd-Spring\JrTransportManagement-Frontend-Angular
- Exists: false
- Total files: 0
- @Component files: 0
- @Injectable files: 0

- Expected components/services presence:
  - Found: None
  - Missing: DriverLoginComponent, DriverDashboardComponent, DriverProfileComponent, VehicleComponent, ConsignmentComponent, HistoryComponent, DocumentUploadComponent, ReviewComponent

- JWT patterns:
  - localStorage.setItem('token'): false
  - sessionStorage.setItem('token'): false
  - Authorization header present: false

- Mock/hard-coded indicators:
  - Contains word "mock": false
  - Contains const array literal: false

- Sample component files:
  - None

## Backend
- Directory: D:\GKCT\JrTransportManagement-BackEnd-Spring\JrTransportManagement-BackEnd-Spring
- Exists: false
- Total files: 0
- Controller files detected: 0
- Mapping annotations counts: GET=0, POST=0, PUT=0, DELETE=0, REQUEST=0

- Expected classes presence:
  - Found: None
  - Missing: DriverAuthController, DriverController, DriverAuthService, DriverService, JWTFilter, SecurityConfig

- Config files:
  - application.properties present: false
  - application.yml present: false
  - Flags (values redacted):
    - jwt.secret: false
    - spring.datasource.username: false
    - spring.datasource.password: false
    - jdbc:postgresql: false

- Real-time / DB migrations markers:
  - WebSocket: false
  - STOMP: false
  - FCM: false
  - Flyway: false
  - Liquibase: false

- Sample controller files:
  - None

## Cross-check: Frontend API paths vs Backend
- No API strings detected in frontend.

- No missing API paths detected (string-level match).

## Recommendations / Next Steps
- Review missing expected components/services and classes.
- Verify JWT handling patterns are consistent with your auth strategy.
- Investigate any "mock" or hard-coded arrays left in code.
- For missing API paths, confirm backend mappings or adjust frontend endpoints.
- Consider adding tests or CI checks to validate key artifacts.