# Master Prompt — WASAC Utility Billing System
# Paste this prompt (with AGENT.md attached as context) at the start of each coding session

---

## CONTEXT PROMPT (paste into Cursor / Codex at session start)

You are a senior Java backend developer building the WASAC Utility Billing System.

Read the AGENT.md file in this project carefully. It is the single source of truth for:
- Tech stack and versions
- Entity design and relationships
- Security rules and role mappings
- Business rules (enforce in service layer only)
- Code conventions

Always follow the AGENT.md. If a rule in AGENT.md conflicts with a suggestion, AGENT.md wins.

---

## TASK-SPECIFIC PROMPTS

Copy-paste the relevant prompt below when working on each task.

---

### PROMPT T1 — User Management + JWT Security

```
Using the AGENT.md as context, implement Task 1: User Management and JWT Security.

Generate in this order:
1. Enums: Role (ROLE_ADMIN, ROLE_OPERATOR, ROLE_FINANCE, ROLE_CUSTOMER), Status (ACTIVE, INACTIVE)
2. BaseEntity (MappedSuperclass with UUID id, createdAt, updatedAt)
3. Entities: User, Role (with ManyToMany relationship)
4. DTOs: SignupRequest, LoginRequest, AuthResponse (contains JWT token + user info)
5. UserDetailsServiceImpl implementing Spring Security UserDetailsService
6. JwtUtil: generateToken(UserDetails), validateToken(token, UserDetails), extractUsername(token)
7. JwtAuthFilter (OncePerRequestFilter): extract Bearer token, validate, set SecurityContext
8. SecurityConfig (@Configuration, @EnableWebSecurity):
   - Stateless session (SessionCreationPolicy.STATELESS)
   - BCryptPasswordEncoder bean
   - Public routes: /api/auth/**, /swagger-ui/**, /v3/api-docs/**
   - All other routes require authentication
   - Role-based rules as per AGENT.md security table
9. AuthController: POST /api/auth/signup, POST /api/auth/login
10. UserController (ROLE_ADMIN only): GET all users, PUT update status

Rules:
- Password must be BCrypt encoded before saving
- Login returns JWT token in AuthResponse
- Wrap all responses in ApiResponse<T> wrapper class
- Add @SecurityScheme and @OpenAPIDefinition for Swagger JWT support
```

---

### PROMPT T2 — Customer and Meter Management

```
Using the AGENT.md as context, implement Task 2: Customer and Meter Management.

Generate in this order:
1. Enums: MeterType (WATER, ELECTRICITY) — if not already created
2. Entities: Customer, Meter (with ManyToOne to Customer)
3. DTOs:
   - CustomerRequest (fullName, nationalId, email, phoneNumber, address)
   - CustomerResponse
   - MeterRequest (meterNumber, meterType, installationDate, customerId)
   - MeterResponse
4. Repositories: CustomerRepository, MeterRepository
   - CustomerRepository: boolean existsByNationalId(String nationalId)
   - MeterRepository: boolean existsByMeterNumber(String meterNumber)
5. Services + Impls:
   - CustomerService: create, update, findById, findAll (paginated), updateStatus
   - MeterService: create, update, findById, findByCustomer, updateStatus
6. Controllers:
   - CustomerController (/api/customers) — ROLE_ADMIN, ROLE_OPERATOR
   - MeterController (/api/meters) — ROLE_ADMIN, ROLE_OPERATOR

Business rules to enforce in service layer:
- Throw DuplicateResourceException if nationalId already exists
- Throw DuplicateResourceException if meterNumber already exists
- Inactive customers cannot be assigned new meters
```

---

### PROMPT T3 — Meter Reading Management

```
Using the AGENT.md as context, implement Task 3: Meter Reading Management.

Generate in this order:
1. Entity: MeterReading with @Table(uniqueConstraints = @UniqueConstraint(columnNames = {"meter_id", "month", "year"}))
2. DTOs: MeterReadingRequest (meterId, previousReading, currentReading, readingDate, month, year), MeterReadingResponse
3. Repository: MeterReadingRepository
   - boolean existsByMeterIdAndMonthAndYear(UUID meterId, int month, int year)
4. Service + Impl: MeterReadingService
5. Controller: ReadingController (/api/readings) — ROLE_OPERATOR only

Business rules to enforce:
- Fetch meter by meterId — throw ResourceNotFoundException if not found
- Throw BusinessRuleException if meter status is INACTIVE
- Throw InvalidReadingException if currentReading <= previousReading
- Throw DuplicateReadingException if reading for (meterId, month, year) already exists
- Compute: consumption = currentReading - previousReading before saving
```

---

### PROMPT T4 — Tariff, Tax, and Penalty Configuration

```
Using the AGENT.md as context, implement Task 4: Tariff Configuration.

Generate in this order:
1. Enum: TariffType (FLAT, TIERED)
2. Entity: Tariff (all fields from AGENT.md)
3. DTOs: TariffRequest, TariffResponse
4. Repository: TariffRepository
   - Optional<Tariff> findTopByMeterTypeAndActiveTrueAndEffectiveDateLessThanEqualOrderByEffectiveDateDesc(MeterType type, LocalDate date)
5. Service + Impl: TariffService
6. Controller: TariffController (/api/tariffs) — ROLE_ADMIN only

Rules:
- When a new tariff is saved, it does NOT modify existing bills
- Only one active tariff per MeterType should apply at billing time (latest effectiveDate <= today)
- Return paginated list of all tariffs with filter by meterType
```

---

### PROMPT T5 — Bill Generation and Payment Processing

```
Using the AGENT.md as context, implement Task 5: Bill Generation and Payment Processing.

Generate in this order:
1. Enums: BillStatus (PENDING, APPROVED, PAID, OVERDUE), PaymentMethod (CASH, MOBILE_MONEY, BANK_TRANSFER)
2. Entities: Bill, Payment (all fields from AGENT.md)
3. DTOs:
   - BillResponse, BillApprovalRequest
   - PaymentRequest (billId, amountPaid, paymentMethod, paymentDate)
   - PaymentResponse
4. Repositories: BillRepository, PaymentRepository
5. Services + Impls: BillService, PaymentService
6. Controllers:
   - BillController (/api/bills) — ROLE_ADMIN, ROLE_FINANCE, ROLE_CUSTOMER
   - PaymentController (/api/payments) — ROLE_FINANCE, ROLE_CUSTOMER

Bill Generation logic (BillService.generateBill(UUID readingId)):
- Fetch MeterReading → get Meter → get Customer
- Throw BusinessRuleException if customer is INACTIVE
- Fetch applicable tariff using TariffRepository method
- Compute: unitCharge = consumption × tariff.unitPrice
- Compute: vatAmount = (unitCharge + serviceCharge) × vatRate / 100
- Compute: totalAmount = unitCharge + serviceCharge + vatAmount
- Set outstandingBalance = totalAmount, status = PENDING
- Save bill (DB trigger will auto-insert notification)

Payment logic (PaymentService.recordPayment(PaymentRequest)):
- Fetch bill → validate it exists and is not already PAID
- Generate unique reference = UUID.randomUUID().toString()
- Save payment
- Update: bill.amountPaid += payment.amountPaid
- Update: bill.outstandingBalance -= payment.amountPaid
- If outstandingBalance <= 0: set bill.status = PAID
- Save bill (DB trigger handles notification)

Bill approval (ROLE_ADMIN or ROLE_FINANCE only):
- Change status PENDING → APPROVED
- Set approvedBy = current user email from SecurityContext
```

---

### PROMPT T6 — Database Routines (PostgreSQL)

```
Using the AGENT.md as context, implement Task 6: Database Routines.

Write raw PostgreSQL SQL for:

1. TRIGGER: trg_after_bill_insert
   - Fires: AFTER INSERT on bills table
   - Joins to customers table to get customer name
   - Inserts into notifications:
     message = 'Dear ' || customer_name || ', Your ' || month || '/' || year || ' utility bill of ' || total_amount || ' FRW has been successfully processed.'
     type = 'BILL_GENERATED'
     sent_at = NOW()

2. TRIGGER: trg_after_payment_insert
   - Fires: AFTER INSERT on payments table
   - Fetches bill and customer
   - If bill.outstanding_balance <= 0:
     * UPDATE bills SET status = 'PAID' WHERE id = bill_id
     * INSERT into notifications:
       message = 'Dear ' || customer_name || ', Your payment has been received. Your bill is now fully paid.'
       type = 'PAYMENT_CONFIRMED'

3. STORED PROCEDURE: generate_monthly_bills(p_month INT, p_year INT)
   - DECLARE a CURSOR over all meter readings WHERE month = p_month AND year = p_year
   - For each reading: fetch active tariff, compute bill amounts, INSERT into bills
   - Use EXCEPTION block to skip and log errors per row

Place all SQL in src/main/resources/db/routines.sql
Add a comment header on each routine explaining what it does.
Call the stored procedure from a @Scheduled method or expose via POST /api/bills/generate endpoint (ROLE_ADMIN only).
```

---

### PROMPT — GlobalExceptionHandler

```
Create a @RestControllerAdvice GlobalExceptionHandler for the WASAC billing system.

Handle these exceptions with correct HTTP status codes (see AGENT.md exception map):
- ResourceNotFoundException → 404
- DuplicateResourceException → 409
- InvalidReadingException → 400
- BusinessRuleException → 422
- AccessDeniedException → 403
- AuthenticationException → 401
- MethodArgumentNotValidException → 400 (extract field errors into list)
- Generic Exception → 500 with message "An unexpected error occurred"

All responses use the ApiResponse<Void> wrapper:
{ "success": false, "message": "...", "errors": ["field: error"] }
```

---

### PROMPT — Swagger / OpenAPI Configuration

```
Configure SpringDoc OpenAPI 3 for the WASAC billing system.

1. Create SwaggerConfig.java with @OpenAPIDefinition:
   - Title: "WASAC Utility Billing System API"
   - Version: "1.0.0"
   - Description: "Backend API for water and electricity billing management"

2. Add @SecurityScheme for JWT Bearer:
   - name: "bearerAuth"
   - type: HTTP
   - scheme: bearer
   - bearerFormat: JWT

3. Annotate each controller with @Tag(name = "...", description = "...")
4. On protected endpoints add: @SecurityRequirement(name = "bearerAuth")
5. Swagger UI accessible at: http://localhost:8080/swagger-ui.html
```

---

### PROMPT — Debugging Helper

```
I have a bug in the WASAC Utility Billing System (Spring Boot 3, PostgreSQL, JWT).
Tech stack is in AGENT.md.

Here is the error:
[PASTE FULL STACK TRACE HERE]

Here is the relevant code:
[PASTE CLASS OR METHOD HERE]

Please:
1. Identify the root cause
2. Explain why it happens
3. Provide the minimum fix
4. State if any AGENT.md business rule is being violated
```

