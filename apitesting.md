# WASAC Utility Billing System API Testing Guide

## Overview

This guide tests every currently implemented API in dependency order using
Swagger UI or Postman.

- Base URL: `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- Authentication header: `Authorization: Bearer <JWT_TOKEN>`
- Request content type: `application/json`
- Dates use ISO format: `YYYY-MM-DD`
- Paginated endpoints support `?page=0&size=10`

Successful responses use:

```json
{
  "success": true,
  "message": "Operation successful",
  "data": {}
}
```

## Before Testing

1. Start PostgreSQL and create the configured `wasac_billing` database.
2. Set a JWT secret containing at least 32 bytes.
3. Start the Spring Boot application.
4. On startup the app runs `src/main/resources/db/routines.sql` automatically.
   If monthly bill generation fails, manually execute that script against
   `wasac_billing` and verify with `\df generate_monthly_bills`.
5. Open Swagger UI and use **Authorize** to enter JWT tokens.

### Required Test Roles

Signup creates users with `ROLE_CUSTOMER` only. Create four users through
signup, then assign admin, operator, and finance roles directly in PostgreSQL.

Use these test accounts:

| Account | Email | Intended role |
|---|---|---|
| Admin | `admin@wasac.rw` | `ROLE_ADMIN` |
| Operator | `operator@wasac.rw` | `ROLE_OPERATOR` |
| Finance | `finance@wasac.rw` | `ROLE_FINANCE` |
| Customer | `customer@wasac.rw` | `ROLE_CUSTOMER` |

After signup, assign roles in PostgreSQL. Connect to the **application database**
first — the `postgres=#` prompt means you are on the default `postgres`
database, where these tables do not exist.

```sql
\c wasac_billing
\dt
```

You should see `users`, `roles`, and `user_roles`. If those tables are missing,
start the Spring Boot app once so Hibernate creates the schema, then run the
role inserts below.

```sql
INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u
JOIN roles r ON r.name = 'ROLE_ADMIN'
WHERE u.email = 'admin@wasac.rw'
ON CONFLICT DO NOTHING;

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u
JOIN roles r ON r.name = 'ROLE_OPERATOR'
WHERE u.email = 'operator@wasac.rw'
ON CONFLICT DO NOTHING;

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id
FROM users u
JOIN roles r ON r.name = 'ROLE_FINANCE'
WHERE u.email = 'finance@wasac.rw'
ON CONFLICT DO NOTHING;
```

Log in again after changing roles so the account can be tested with the
correct authorities.

**Or assign roles through the API** (admin only) after signup:

### PUT `/api/users/{userId}/roles`

Replace `{userId}` with the finance user's `id` from `GET /api/users`.

```json
{
  "role": "ROLE_FINANCE"
}
```

Expected: `200 OK` and `data.roles` includes `ROLE_FINANCE`. Repeat for
`ROLE_ADMIN` and `ROLE_OPERATOR` on the other test accounts.

**Role assignment troubleshooting:**

- Use the exact signup email `finance@wasac.rw` (not `finance@wassac.rw`).
- Connect to `wasac_billing`, not the default `postgres` database.
- After SQL or API role changes, **log in again** to get a new JWT.
- Verify with:

```sql
SELECT u.email, r.name
FROM users u
JOIN user_roles ur ON ur.user_id = u.id
JOIN roles r ON r.id = ur.role_id
WHERE u.email = 'finance@wasac.rw';
```

### Save IDs and Tokens

Keep these values from responses:

```text
ADMIN_TOKEN=
OPERATOR_TOKEN=
FINANCE_TOKEN=
CUSTOMER_TOKEN=

USER_ID=
CUSTOMER_ID=
METER_ID=
READING_ID=
TARIFF_ID=
BILL_ID=
PAYMENT_ID=
```

## Role Access Summary

| API group | Allowed roles |
|---|---|
| Authentication | Public |
| Users | Admin |
| Customers | Admin, Operator |
| Meters | Admin, Operator |
| Meter Readings | Operator |
| Tariffs | Admin |
| Bills | Admin, Finance, Customer |
| Monthly bill generation | Admin |
| Bill approval | Admin, Finance |
| Payments | Finance, Customer |

## 1. Authentication

### POST `/api/auth/signup`

Create each test account by changing the name, email, and phone number.

```json
{
  "fullName": "WASAC Admin",
  "email": "admin@wasac.rw",
  "phoneNumber": "250788100001",
  "password": "Password123!",
  "role": "ROLE_CUSTOMER"
}
```

Expected: `201 Created`, JWT token returned, user status `ACTIVE`, and initial
role `ROLE_CUSTOMER`.

Public signup only accepts `ROLE_CUSTOMER`. Submitting `ROLE_ADMIN`,
`ROLE_OPERATOR`, or `ROLE_FINANCE` returns `422 Unprocessable Entity`.

Repeat for:

```text
operator@wasac.rw
finance@wasac.rw
customer@wasac.rw
```

Negative tests:

- Submit the same email twice: expect `409 Conflict`.
- Submit an invalid email or password shorter than 8 characters: expect
  `400 Bad Request`.

### POST `/api/auth/login`

```json
{
  "email": "admin@wasac.rw",
  "password": "Password123!"
}
```

Expected: `200 OK` with `data.token`. Repeat for every role and save each
token.

Negative tests:

- Wrong password: expect `401 Unauthorized`.
- Inactivate the user, then log in: expect `401 Unauthorized`.

## 2. Users

Use `ADMIN_TOKEN`.

### GET `/api/users?page=0&size=10`

Expected: `200 OK` with a paginated list. Save a user `id` as `USER_ID`.

Authorization test: use `CUSTOMER_TOKEN`; expect `403 Forbidden`.

### PUT `/api/users/{userId}/status`

Replace `{userId}` with `USER_ID`.

```json
{
  "status": "INACTIVE"
}
```

Expected: `200 OK` and status `INACTIVE`.

Restore the user:

```json
{
  "status": "ACTIVE"
}
```

### PUT `/api/users/{userId}/roles`

Assign `ROLE_FINANCE` to the finance test account (see also **Required Test
Roles** above):

```json
{
  "role": "ROLE_FINANCE"
}
```

Expected: `200 OK` with the updated role list.

## 3. Customers

Use `ADMIN_TOKEN` or `OPERATOR_TOKEN`.

### POST `/api/customers`

```json
{
  "fullName": "Jean Uwimana",
  "nationalId": "1199980012345678",
  "email": "jean.uwimana@example.com",
  "phoneNumber": "250788222333",
  "address": "Kigali, Gasabo"
}
```

Expected: `201 Created`, status `ACTIVE`. Save `data.id` as `CUSTOMER_ID`.

Negative test: submit the same `nationalId` again; expect `409 Conflict`.

### GET `/api/customers/{customerId}`

Expected: `200 OK` with the created customer.

### GET `/api/customers?page=0&size=10`

Expected: `200 OK` with paginated customers.

### PUT `/api/customers/{customerId}`

The request must include all customer fields.

```json
{
  "fullName": "Jean Claude Uwimana",
  "nationalId": "1199980012345678",
  "email": "jean.uwimana@example.com",
  "phoneNumber": "250788222334",
  "address": "Kigali, Kicukiro"
}
```

Expected: `200 OK` with updated fields.

### PUT `/api/customers/{customerId}/status`

```json
{
  "status": "INACTIVE"
}
```

Expected: `200 OK`.

Keep the customer inactive temporarily to test the meter business rule, then
restore it:

```json
{
  "status": "ACTIVE"
}
```

## 4. Meters

Use `ADMIN_TOKEN` or `OPERATOR_TOKEN`.

### POST `/api/meters`

```json
{
  "meterNumber": "WTR-000001",
  "meterType": "WATER",
  "installationDate": "2026-01-15",
  "customerId": "<CUSTOMER_ID>"
}
```

Expected: `201 Created`, status `ACTIVE`. Save `data.id` as `METER_ID`.

Negative tests:

- Submit the same `meterNumber`: expect `409 Conflict`.
- Inactivate the customer and try assigning a new meter: expect
  `422 Unprocessable Entity`.
- Use a future installation date: expect `400 Bad Request`.

### GET `/api/meters/{meterId}`

Expected: `200 OK`.

### GET `/api/meters/customer/{customerId}?page=0&size=10`

Expected: `200 OK` with the customer's meters.

### PUT `/api/meters/{meterId}`

The request must include all meter fields.

```json
{
  "meterNumber": "WTR-000001",
  "meterType": "WATER",
  "installationDate": "2026-01-15",
  "customerId": "<CUSTOMER_ID>"
}
```

Expected: `200 OK`.

### PUT `/api/meters/{meterId}/status`

```json
{
  "status": "INACTIVE"
}
```

Expected: `200 OK`.

Keep it inactive briefly for the reading failure test, then restore it:

```json
{
  "status": "ACTIVE"
}
```

## 5. Tariffs

Use `ADMIN_TOKEN`.

### POST `/api/tariffs`

Create an applicable water tariff before generating a bill.

```json
{
  "meterType": "WATER",
  "tariffType": "FLAT",
  "unitPrice": 500.00,
  "minUnits": 0,
  "maxUnits": null,
  "serviceCharge": 1000.00,
  "vatRate": 18,
  "penaltyRate": 5,
  "effectiveDate": "2026-01-01",
  "active": true
}
```

Expected: `201 Created`. Save `data.id` as `TARIFF_ID`.

### GET `/api/tariffs/{tariffId}`

Expected: `200 OK`.

### GET `/api/tariffs?page=0&size=10`

Expected: `200 OK` with all tariffs.

### GET `/api/tariffs?meterType=WATER&page=0&size=10`

Expected: `200 OK` with water tariffs only.

### PUT `/api/tariffs/{tariffId}`

The request must include all tariff fields.

```json
{
  "meterType": "WATER",
  "tariffType": "FLAT",
  "unitPrice": 525.00,
  "minUnits": 0,
  "maxUnits": null,
  "serviceCharge": 1000.00,
  "vatRate": 18,
  "penaltyRate": 5,
  "effectiveDate": "2026-01-01",
  "active": true
}
```

Expected: `200 OK`.

Negative test: set `maxUnits` lower than `minUnits`; expect
`422 Unprocessable Entity`.

## 6. Meter Readings

Use `OPERATOR_TOKEN`.

### POST `/api/readings`

Use the meter **UUID** from `POST /api/meters` (`data.id`), not the meter
number (`meterNumber` such as `WTR-000001` or `MI001`).

```json
{
  "meterId": "<METER_ID>",
  "previousReading": 100,
  "currentReading": 125.5,
  "readingDate": "2026-06-05",
  "month": 6,
  "year": 2026
}
```

Expected: `201 Created`, with `consumption` equal to `25.5`. Save `data.id`
as `READING_ID`.

Negative tests:

- Submit the same meter/month/year again: expect `409 Conflict`.
- Set `currentReading` equal to or below `previousReading`: expect
  `400 Bad Request`.
- Inactivate the meter and submit a reading: expect
  `422 Unprocessable Entity`.
- Use `ADMIN_TOKEN`: expect `403 Forbidden` because readings are operator-only.

### GET `/api/readings/{readingId}`

Expected: `200 OK`.

### GET `/api/readings/meter/{meterId}?page=0&size=10`

Expected: `200 OK` with paginated readings.

## 7. Bills

Use `ADMIN_TOKEN`, `FINANCE_TOKEN`, or `CUSTOMER_TOKEN` unless a stricter role
is stated.

### POST `/api/bills/generate/{readingId}`

Replace `{readingId}` with `READING_ID`.

Expected: `201 Created`. Verify:

- `status` is `PENDING`
- `totalConsumption` is `25.5`
- `unitCharge = consumption × unitPrice`
- `vatAmount = (unitCharge + serviceCharge) × vatRate / 100`
- `outstandingBalance` equals `totalAmount`

Save `data.id` as `BILL_ID`.

Negative tests:

- Generate another bill from the same reading: expect `409 Conflict`.
- Inactivate the customer before generation: expect
  `422 Unprocessable Entity`.
- Use a meter type without an applicable active tariff: expect
  `422 Unprocessable Entity`.

### GET `/api/bills/{billId}`

Expected: `200 OK`.

### GET `/api/bills?page=0&size=10`

Expected: `200 OK` with paginated bills.

### PUT `/api/bills/{billId}/approve`

Use `ADMIN_TOKEN` or `FINANCE_TOKEN`.

```json
{
  "status": "APPROVED"
}
```

Expected: `200 OK`, status `APPROVED`, and `approvedBy` equals the logged-in
admin or finance email.

Negative tests:

- Use `CUSTOMER_TOKEN`: expect `403 Forbidden`.
- Approve an already approved bill: expect `422 Unprocessable Entity`.
- Send any status other than `APPROVED`: expect `422 Unprocessable Entity`.

### POST `/api/bills/generate`

Admin only. This calls the PostgreSQL `generate_monthly_bills` procedure.

Create another active meter reading for a different month or meter before this
test, then send:

```json
{
  "month": 6,
  "year": 2026
}
```

Expected: `200 OK`. The procedure generates bills for eligible readings and
skips/logs rows that already have bills or cannot be billed.

Negative test: use `FINANCE_TOKEN`; expect `403 Forbidden`.

### Verify Bill Notification

After generating a bill, query PostgreSQL:

```sql
SELECT *
FROM notifications
WHERE type = 'BILL_GENERATED'
ORDER BY sent_at DESC;
```

Expected: a message containing the customer name, bill month/year, and total.

## 8. Payments

Use `FINANCE_TOKEN` or `CUSTOMER_TOKEN`.

### POST `/api/payments`

Record a partial payment:

```json
{
  "billId": "<BILL_ID>",
  "amountPaid": 5000.00,
  "paymentMethod": "MOBILE_MONEY",
  "paymentDate": "2026-06-05"
}
```

Expected: `201 Created`, a UUID reference, increased bill `amountPaid`, and
decreased `billOutstandingBalance`. Save `data.id` as `PAYMENT_ID`.

Record another payment large enough to settle the remaining balance:

```json
{
  "billId": "<BILL_ID>",
  "amountPaid": 50000.00,
  "paymentMethod": "BANK_TRANSFER",
  "paymentDate": "2026-06-05"
}
```

Expected: `201 Created`, outstanding balance `0.00`, and bill status `PAID`.

Negative tests:

- Record another payment on the paid bill: expect `422 Unprocessable Entity`.
- Submit amount `0` or a negative amount: expect `400 Bad Request`.
- Use `ADMIN_TOKEN`: expect `403 Forbidden`.

### GET `/api/payments/{paymentId}`

Expected: `200 OK`.

### GET `/api/payments?page=0&size=10`

Expected: `200 OK` with all paginated payments.

### GET `/api/payments?billId={billId}&page=0&size=10`

Expected: `200 OK` with payments for the specified bill.

### Verify Payment Notification

After fully settling a bill, query PostgreSQL:

```sql
SELECT *
FROM notifications
WHERE type = 'PAYMENT_CONFIRMED'
ORDER BY sent_at DESC;
```

Expected: a fully-paid payment confirmation notification.

## 9. Common Error Tests

Run these checks across protected APIs:

| Test | Expected result |
|---|---|
| No Authorization header | `401 Unauthorized` |
| Invalid or expired JWT | `401 Unauthorized` |
| Valid JWT with wrong role | `403 Forbidden` |
| Unknown UUID | `404 Not Found` |
| Missing required request field | `400 Bad Request` |
| Invalid enum value | `400 Bad Request` |
| Duplicate unique value | `409 Conflict` |
| Business-rule violation | `422 Unprocessable Entity` |

## 10. Recommended Full Test Order

1. Signup four users.
2. Assign admin, operator, and finance roles in PostgreSQL.
3. Log in all four users and save tokens.
4. Test admin user management.
5. Create and update a customer.
6. Create and update a meter.
7. Create an applicable tariff.
8. Capture a meter reading as operator.
9. Generate a bill.
10. Verify the bill-generated notification.
11. Approve the bill as admin or finance.
12. Record partial and final payments.
13. Verify bill status is `PAID`.
14. Verify the payment-confirmed notification.
15. Create another reading and test monthly stored-procedure generation.
16. Run authorization and business-rule negative tests.
