# WASAC Utility Billing System

## Overview

The WASAC Utility Billing System is a Spring Boot backend application designed to automate the management of water and electricity billing services.

The system supports customer management, utility meters, meter readings, tariff configuration, bill generation, payment processing, notifications, and role-based security using JWT authentication.

This project was developed using Spring Boot, Spring Security, Spring Data JPA, PostgreSQL, and Swagger OpenAPI.

---

## Features

### Authentication & Authorization

* JWT-based authentication
* Secure login and signup
* BCrypt password encryption
* Role-based access control

### User Roles

| Role          | Responsibilities                                       |
| ------------- | ------------------------------------------------------ |
| ROLE_ADMIN    | Manage users, tariffs, bills, and system configuration |
| ROLE_OPERATOR | Capture meter readings and manage customers/meters     |
| ROLE_FINANCE  | Approve bills and process payments                     |
| ROLE_CUSTOMER | View bills and payment history                         |

---

## Functional Modules

### User Management

* User registration
* User login
* User status management
* Role assignment

### Customer Management

* Register customers
* Update customer information
* Activate/Deactivate customers
* Prevent duplicate National IDs

### Meter Management

* Register utility meters
* Water and Electricity meter support
* Meter status management
* Customer-to-meter assignment

### Meter Reading Management

* Monthly meter readings
* Consumption calculation
* Duplicate monthly reading prevention
* Active meter validation

### Tariff Management

* Flat tariffs
* Tier-based tariffs
* VAT configuration
* Service charge configuration
* Tariff versioning

### Billing

* Automatic bill generation
* Outstanding balance tracking
* Bill approval workflow
* Penalty support

### Payments

* Partial payments
* Full payments
* Automatic balance updates
* Automatic bill closure when fully paid

### Notifications

* Bill generated notifications
* Payment confirmation notifications
* Trigger-based notification creation

---

## Technology Stack

### Backend

* Java 21
* Spring Boot 3.5.x
* Spring Security
* Spring Data JPA
* Hibernate

### Database

* PostgreSQL 17

### Documentation

* Swagger OpenAPI 3

### Authentication

* JSON Web Tokens (JWT)

### Build Tool

* Maven

---

## Project Structure

```text
src/main/java
├── config
├── controller
├── dto
├── entity
├── enums
├── exception
├── repository
├── security
├── service
│   ├── impl
├── util
└── UtilityBillingSystemApplication

src/main/resources
├── application.properties
└── db
    └── routines.sql
```

---

## Database Entities

### Core Entities

* User
* Role
* Customer
* Meter
* MeterReading
* Tariff
* Bill
* Payment
* Notification

### Relationship Summary

* User ↔ Role (Many-to-Many)
* Customer → Meter (One-to-Many)
* Meter → MeterReading (One-to-Many)
* Customer → Bill (One-to-Many)
* Bill → Payment (One-to-Many)
* Customer → Notification (One-to-Many)

Refer to `erd.md` for the complete Entity Relationship Diagram.

---

## Security Configuration

### Public Endpoints

```text
/api/auth/**
/swagger-ui/**
/v3/api-docs/**
```

### Protected Endpoints

All remaining endpoints require authentication.

JWT must be supplied as:

```http
Authorization: Bearer <token>
```

---

## API Documentation

Swagger UI:

```text
http://localhost:8080/swagger-ui.html
```

OpenAPI JSON:

```text
http://localhost:8080/v3/api-docs
```

---

## Database Configuration

Create PostgreSQL database:

```sql
CREATE DATABASE wasac_billing;
```

Update:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/wasac_billing
spring.datasource.username=postgres
spring.datasource.password=123

app.jwt.secret=wasac-secret-key-2026-super-secure-key-for-jwt
app.jwt.expiration=86400000
```

---

## Running the Application

### Clone

```bash
git clone <repository-url>
cd utility-billing-system
```

### Build

```bash
mvn clean install
```

### Run

```bash
mvn spring-boot:run
```

Application URL:

```text
http://localhost:8080
```

---

## Business Rules

### Customer Rules

* National ID must be unique.
* Inactive customers cannot receive new meters.
* Inactive customers cannot receive bills.

### Meter Reading Rules

* Meter must be active.
* One reading per meter per month/year.
* Current reading must be greater than previous reading.

### Billing Rules

* Bills are generated from meter readings.
* Tariffs are versioned.
* New tariffs only affect future billing cycles.

### Payment Rules

* Partial payments allowed.
* Outstanding balance updated automatically.
* Bill status becomes PAID when balance reaches zero.

---

## Database Routines

### Trigger: Bill Generated

Automatically inserts a notification after a bill is generated.

### Trigger: Payment Confirmation

Automatically updates bill status and inserts notification after full payment.

### Stored Procedure

`generate_monthly_bills(month, year)`

Generates bills for all eligible meter readings using the active tariff.

---

## Authors

WASAC Utility Billing System

Developed using:

* Spring Boot
* PostgreSQL
* JWT Authentication
* Swagger OpenAPI
