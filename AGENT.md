# AGENT.md — Utility Billing System (WASAC/REG)
# Java Spring Boot Backend

## Project Overview
A secure, automated Utility Billing System for a national water and electricity utility company (Rwanda). Manages customers, meters, readings, billing (postpaid), payments, tariffs, and notifications. No frontend required — tested via Postman/Swagger.

---

## Tech Stack
- **Language:** Java 17
- **Framework:** Spring Boot 3.x
- **Security:** Spring Security 6.x + JWT (jjwt 0.12.x)
- **ORM:** Spring Data JPA + Hibernate
- **Database:** PostgreSQL 15+
- **API Docs:** SpringDoc OpenAPI 3 (Swagger UI at /swagger-ui.html)
- **Build Tool:** Maven
- **Utilities:** Lombok

---

## Project Structure
```
src/main/java/com/wasac/billing/
├── config/           # SecurityConfig, JwtConfig, SwaggerConfig
├── controller/       # REST controllers (one per domain)
├── dto/              # Request + Response DTOs only (never expose entities)
├── entity/           # JPA entities
├── enums/            # Role, Status, MeterType, BillStatus, PaymentMethod
├── exception/        # GlobalExceptionHandler + custom exceptions
├── repository/       # JpaRepository interfaces
├── security/         # JwtUtil, JwtAuthFilter, UserDetailsServiceImpl
└── service/          # Interfaces + Impl classes

src/main/resources/
├── application.yml
└── db/               # SQL scripts: triggers, stored procedures
```

---

## Entities & Fields

### BaseEntity (MappedSuperclass — all entities extend this)
```
id (UUID, auto), createdAt (LocalDateTime), updatedAt (LocalDateTime)
```

### User
```
fullName, email (unique, username), phoneNumber, password (BCrypt), 
status (ACTIVE/INACTIVE), roles (ManyToMany → Role)
```

### Role
```
name: ROLE_ADMIN | ROLE_OPERATOR | ROLE_FINANCE | ROLE_CUSTOMER
```

### Customer
```
fullName, nationalId (unique), email, phoneNumber, address, status (ACTIVE/INACTIVE)
```

### Meter
```
meterNumber (unique), meterType (WATER | ELECTRICITY), installationDate (LocalDate),
status (ACTIVE/INACTIVE), customer (ManyToOne)
```

### MeterReading
```
meter (ManyToOne), previousReading (Double), currentReading (Double),
consumption (Double, computed), readingDate (LocalDate), month (int), year (int)
UNIQUE CONSTRAINT: (meter_id, month, year)
```

### Tariff
```
meterType, tariffType (FLAT | TIERED), unitPrice (BigDecimal), minUnits, maxUnits,
serviceCharge (BigDecimal), vatRate (Double %), penaltyRate (Double %),
effectiveDate (LocalDate), active (boolean)
```

### Bill
```
customer (ManyToOne), meter (ManyToOne), meterReading (OneToOne),
month (int), year (int), totalConsumption (Double),
unitCharge (BigDecimal), serviceCharge (BigDecimal), vatAmount (BigDecimal),
penaltyAmount (BigDecimal), totalAmount (BigDecimal),
amountPaid (BigDecimal), outstandingBalance (BigDecimal),
status (PENDING | APPROVED | PAID | OVERDUE),
generatedDate (LocalDate), dueDate (LocalDate), approvedBy (String)
```

### Payment
```
bill (ManyToOne), amountPaid (BigDecimal), paymentMethod (CASH | MOBILE_MONEY | BANK_TRANSFER),
paymentDate (LocalDate), reference (unique, auto-generated UUID string)
```

### Notification
```
customer (ManyToOne), message (TEXT), sentAt (LocalDateTime),
type (BILL_GENERATED | PAYMENT_CONFIRMED)
```

---

## Security Rules

### Public endpoints (permit all)
```
POST /api/auth/signup
POST /api/auth/login
GET  /v3/api-docs/**
GET  /swagger-ui/**
GET  /swagger-ui.html
```

### Role-to-endpoint mapping
| Pattern                  | Roles allowed                              |
|--------------------------|--------------------------------------------|
| /api/users/**            | ROLE_ADMIN                                 |
| /api/customers/**        | ROLE_ADMIN, ROLE_OPERATOR                  |
| /api/meters/**           | ROLE_ADMIN, ROLE_OPERATOR                  |
| /api/readings/**         | ROLE_OPERATOR                              |
| /api/tariffs/**          | ROLE_ADMIN                                 |
| /api/bills/**            | ROLE_ADMIN, ROLE_FINANCE, ROLE_CUSTOMER    |
| /api/payments/**         | ROLE_FINANCE, ROLE_CUSTOMER                |

### JWT
- Algorithm: HS256, minimum 256-bit secret
- Expiry: 24h (86400000 ms)
- Header: `Authorization: Bearer <token>`
- JwtAuthFilter extends OncePerRequestFilter

---

## Business Rules (enforce in service layer, not controller)

### Customer rules
- nationalId must be unique → throw DuplicateResourceException (409)
- Inactive customers cannot receive new bills → throw BusinessRuleException (422)

### Meter rules
- meterNumber must be unique → throw DuplicateResourceException (409)
- Only ACTIVE meters can have readings captured

### MeterReading rules
- currentReading MUST be > previousReading → throw InvalidReadingException (400)
- Only ONE reading per meter per (month, year) → throw DuplicateReadingException (409)
- consumption = currentReading - previousReading (computed before save)
- Meter must be ACTIVE

### Tariff rules
- Apply latest active tariff where effectiveDate <= bill generation date
- New tariffs do NOT retroactively affect existing bills

### Bill generation rules
- Customer must be ACTIVE
- Meter must be ACTIVE
- Apply tariff: unitCharge = consumption × unitPrice, then add serviceCharge + VAT
- outstandingBalance = totalAmount on creation
- Initial status: PENDING

### Payment rules
- Partial payments allowed
- outstandingBalance -= amountPaid after each payment
- If outstandingBalance <= 0: set bill.status = PAID
- Auto-generate unique payment reference (UUID)

### Bill approval
- Only ROLE_ADMIN or ROLE_FINANCE can change status PENDING → APPROVED

---

## Database Routines (PostgreSQL SQL scripts in db/ folder)

### Trigger: trg_after_bill_insert
- Fires: AFTER INSERT on bills
- Action: INSERT into notifications with message:
  `Dear <customer_name>, Your <month>/<year> utility bill of <total_amount> FRW has been successfully processed.`

### Trigger: trg_after_payment_insert
- Fires: AFTER INSERT on payments
- Action: Check if bill outstanding_balance = 0 → UPDATE bill status to PAID → INSERT notification:
  `Dear <customer_name>, Your bill payment has been received. Outstanding balance: 0 FRW.`

### Stored Procedure: generate_monthly_bills(p_month INT, p_year INT)
- Loops (CURSOR) over active meters with readings for that month/year
- Fetches applicable tariff
- Computes and inserts bill records

---

## API Response Format (always use this wrapper)
```json
{
  "success": true,
  "message": "Operation successful",
  "data": { }
}
```
Error response:
```json
{
  "success": false,
  "message": "Validation failed",
  "errors": ["field: message"]
}
```

---

## Exception Map
| Exception class              | HTTP Status |
|------------------------------|-------------|
| ResourceNotFoundException    | 404         |
| DuplicateResourceException   | 409         |
| InvalidReadingException      | 400         |
| BusinessRuleException        | 422         |
| AccessDeniedException        | 403         |
| AuthenticationException      | 401         |
| MethodArgumentNotValidException | 400      |

---

## Code Conventions
- Constructor injection via `@RequiredArgsConstructor` (Lombok) — never `@Autowired` on fields
- Services: interface + `ServiceImpl` pattern
- Enums stored as STRING: `@Enumerated(EnumType.STRING)`
- Dates: `LocalDate` / `LocalDateTime` only — never `java.util.Date`
- Use `@Valid` on every `@RequestBody`
- Pagination: `Pageable` on all list endpoints (`?page=0&size=10`)
- All IDs: UUID type

---

## application.yml
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/wasac_billing
    username: postgres
    password: password
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
  application:
    name: wasac-billing-system

app:
  jwt:
    secret: your-super-secret-256-bit-key-change-this-now
    expiration: 86400000

springdoc:
  swagger-ui:
    path: /swagger-ui.html
  api-docs:
    path: /v3/api-docs
```

---

## Maven Dependencies (pom.xml)
```xml
spring-boot-starter-web
spring-boot-starter-security
spring-boot-starter-data-jpa
spring-boot-starter-validation
postgresql (runtime scope)
io.jsonwebtoken:jjwt-api:0.12.3
io.jsonwebtoken:jjwt-impl:0.12.3 (runtime)
io.jsonwebtoken:jjwt-jackson:0.12.3 (runtime)
org.springdoc:springdoc-openapi-starter-webmvc-ui:2.3.0
org.projectlombok:lombok
```

---

## HARD RULES — DO NOT VIOLATE
- NEVER expose entity classes in API responses — DTOs only
- NEVER store plain-text passwords
- NEVER hardcode JWT secret in source files
- NEVER skip `@Valid` on request bodies
- NEVER allow readings on inactive meters
- NEVER generate bills for inactive customers
- NEVER implement a frontend
- NEVER use field-level `@Autowired`
