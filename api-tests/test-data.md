# WASAC API Test Data

Sample payloads and expected values for manual API testing.  
Base URL: `http://localhost:8080`  
Shared password: **`Password123!`**

---

## 1. Users (signup / login)

Public signup accepts only `ROLE_CUSTOMER`. Omit `role` or set it to `ROLE_CUSTOMER`.

### Admin user

```json
{
  "fullName": "WASAC Admin",
  "email": "admin@wasac.rw",
  "phoneNumber": "250788100001",
  "password": "Password123!",
  "role": "ROLE_CUSTOMER"
}
```

After signup, assign `ROLE_ADMIN` via SQL or admin API.

### Operator user

```json
{
  "fullName": "WASAC Operator",
  "email": "operator@wasac.rw",
  "phoneNumber": "250788100002",
  "password": "Password123!",
  "role": "ROLE_CUSTOMER"
}
```

Assign `ROLE_OPERATOR` after bootstrap.

### Finance user

```json
{
  "fullName": "WASAC Finance",
  "email": "finance@wasac.rw",
  "phoneNumber": "250788100003",
  "password": "Password123!",
  "role": "ROLE_CUSTOMER"
}
```

Assign `ROLE_FINANCE` after bootstrap.

### Customer user (portal account)

```json
{
  "fullName": "WASAC Customer",
  "email": "customer@wasac.rw",
  "phoneNumber": "250788100004",
  "password": "Password123!",
  "role": "ROLE_CUSTOMER"
}
```

### Login (all roles)

```json
{
  "email": "admin@wasac.rw",
  "password": "Password123!"
}
```

### Role assignment (admin only)

`PUT /api/users/{userId}/roles`

```json
{
  "roles": ["ROLE_OPERATOR"]
}
```

Valid roles: `ROLE_ADMIN`, `ROLE_OPERATOR`, `ROLE_FINANCE`, `ROLE_CUSTOMER`.

### User status update (admin only)

`PUT /api/users/{userId}/status`

```json
{
  "status": "ACTIVE"
}
```

Status values: `ACTIVE`, `INACTIVE`.

---

## 2. Customers

### Create customer

`POST /api/customers`

```json
{
  "fullName": "Jean Uwimana",
  "nationalId": "1199980012345678",
  "email": "jean.uwimana@example.com",
  "phoneNumber": "250788222333",
  "address": "Kigali, Gasabo"
}
```

Save `data.id` as **customerId**.

### Update customer

`PUT /api/customers/{customerId}` — all fields required:

```json
{
  "fullName": "Jean Claude Uwimana",
  "nationalId": "1199980012345678",
  "email": "jean.uwimana@example.com",
  "phoneNumber": "250788222334",
  "address": "Kigali, Kicukiro"
}
```

### Deactivate customer (for meter negative test)

`PUT /api/customers/{customerId}/status`

```json
{
  "status": "INACTIVE"
}
```

### Duplicate national ID (negative)

Repeat `POST /api/customers` with the same `nationalId` → `409 Conflict`.

---

## 3. Meters

### Create water meter

`POST /api/meters`

```json
{
  "meterNumber": "WTR-000001",
  "meterType": "WATER",
  "installationDate": "2026-01-15",
  "customerId": "<customerId>"
}
```

Save `data.id` as **meterId**.  
Meter types: `WATER`, `ELECTRICITY`.

### Create electricity meter (optional second meter)

```json
{
  "meterNumber": "ELC-000001",
  "meterType": "ELECTRICITY",
  "installationDate": "2026-02-01",
  "customerId": "<customerId>"
}
```

### Duplicate meter number (negative)

Repeat with `"meterNumber": "WTR-000001"` → `409 Conflict`.

### Inactive customer assignment (negative)

Set customer to `INACTIVE`, then `POST /api/meters` with that `customerId` → `422`.

---

## 4. Tariffs

Create before bill generation. Effective date must be on or before bill generation date.

### Water flat tariff

`POST /api/tariffs`

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

Save `data.id` as **tariffId**.  
Tariff types: `FLAT`, `TIERED`.

### Electricity tiered tariff (optional)

```json
{
  "meterType": "ELECTRICITY",
  "tariffType": "TIERED",
  "unitPrice": 120.00,
  "minUnits": 0,
  "maxUnits": 100,
  "serviceCharge": 500.00,
  "vatRate": 18,
  "penaltyRate": 3,
  "effectiveDate": "2026-01-01",
  "active": true
}
```

---

## 5. Meter readings

Use **meter UUID** (`meterId`), not the meter number string.

### Valid reading (June 2026)

`POST /api/readings` — operator token only

```json
{
  "meterId": "<meterId>",
  "previousReading": 100,
  "currentReading": 125.5,
  "readingDate": "2026-06-05",
  "month": 6,
  "year": 2026
}
```

Expected: `consumption = 25.5`. Save `data.id` as **readingId**.

### Second reading (for monthly batch test)

```json
{
  "meterId": "<meterId>",
  "previousReading": 125.5,
  "currentReading": 140,
  "readingDate": "2026-07-05",
  "month": 7,
  "year": 2026
}
```

### Invalid: current ≤ previous

```json
{
  "meterId": "<meterId>",
  "previousReading": 140,
  "currentReading": 140,
  "readingDate": "2026-08-05",
  "month": 8,
  "year": 2026
}
```

Expected: `400 Bad Request` — *Current reading must be greater than previous reading*.

### Duplicate month/year

Repeat the June 2026 reading → `409 Conflict`.

---

## 6. Bills

### Generate bill from reading

`POST /api/bills/generate/{readingId}`  
Roles: `ROLE_ADMIN`, `ROLE_FINANCE`, or `ROLE_CUSTOMER`.

Expected for consumption `25.5`, unit price `500`, service charge `1000`, VAT `18%`:

| Field | Calculation | Value |
|---|---|---|
| unitCharge | 25.5 × 500 | 12750.00 |
| serviceCharge | fixed | 1000.00 |
| vatAmount | (12750 + 1000) × 18 / 100 | 2475.00 |
| totalAmount | sum | 16225.00 |
| status | initial | `PENDING` |
| outstandingBalance | equals total | 16225.00 |

Save `data.id` as **billId**.

### Approve bill

`PUT /api/bills/{billId}/approve` — admin or finance token

```json
{
  "status": "APPROVED"
}
```

Only `APPROVED` is accepted; bill must be `PENDING`.

### Monthly batch generation (admin only)

`POST /api/bills/generate`

```json
{
  "month": 7,
  "year": 2026
}
```

Calls PostgreSQL procedure `generate_monthly_bills`.

Bill statuses: `PENDING`, `APPROVED`, `PAID`, `OVERDUE`.

---

## 7. Payments

Payment methods: `CASH`, `MOBILE_MONEY`, `BANK_TRANSFER`.

### Partial payment

`POST /api/payments` — finance or customer token

```json
{
  "billId": "<billId>",
  "amountPaid": 5000.00,
  "paymentMethod": "MOBILE_MONEY",
  "paymentDate": "2026-06-05"
}
```

Save `data.id` as **paymentId**.

### Final payment (settle balance)

Assuming outstanding balance after partial payment is `11225.00`:

```json
{
  "billId": "<billId>",
  "amountPaid": 11225.00,
  "paymentMethod": "BANK_TRANSFER",
  "paymentDate": "2026-06-06"
}
```

Expected: bill `status` → `PAID`, `outstandingBalance` → `0.00`.

### Pay already-paid bill (negative)

Repeat payment on paid bill → `422 Unprocessable Entity`.

---

## 8. Quick reference — IDs and tokens

After running `wasac-billing.http` or the Postman collection, you should have:

```text
adminToken       = JWT from admin@wasac.rw login
operatorToken    = JWT from operator@wasac.rw login
financeToken     = JWT from finance@wasac.rw login
customerToken    = JWT from customer@wasac.rw login

adminUserId      = UUID from signup/login response
operatorUserId   = UUID from signup response
financeUserId    = UUID from signup response
customerUserId   = UUID from signup response

customerId       = Jean Uwimana customer record
meterId          = WTR-000001 meter record
readingId        = June 2026 reading
tariffId         = WATER FLAT tariff
billId           = bill generated from readingId
paymentId        = first partial payment
```

---

## 9. Authorization negative matrix

| Request | Token | Expected |
|---|---|---|
| `GET /api/users` | none | 401 |
| `GET /api/users` | customerToken | 403 |
| `GET /api/tariffs` | operatorToken | 403 |
| `POST /api/readings` | adminToken | 403 |
| `PUT /api/bills/{id}/approve` | customerToken | 403 |
| `POST /api/payments` | adminToken | 403 |
| `POST /api/bills/generate` | financeToken | 403 |
