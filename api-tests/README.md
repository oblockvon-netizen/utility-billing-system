# WASAC Utility Billing System — API Tests

Manual API test suite for the Spring Boot utility billing application.

| Item | Value |
|---|---|
| Base URL | `http://localhost:8080` |
| Swagger UI | `http://localhost:8080/swagger-ui.html` |
| OpenAPI JSON | `http://localhost:8080/v3/api-docs` |
| Content-Type | `application/json` |
| Auth header | `Authorization: Bearer <JWT>` |

## Files in this folder

| File | Purpose |
|---|---|
| `wasac-billing.http` | Step-by-step requests for IntelliJ HTTP Client or VS Code REST Client |
| `postman-collection.json` | Import into Postman; collection variables hold tokens and IDs |
| `test-data.md` | Sample payloads for users, customers, meters, readings, tariffs, bills, and payments |

## Prerequisites

1. PostgreSQL running with database `wasac_billing` (see `application.properties`).
2. Spring Boot app started: `./mvnw spring-boot:run`
3. On first startup, Hibernate creates tables and `db/routines.sql` installs `generate_monthly_bills`.
4. REST Client extension (VS Code) or built-in HTTP Client (IntelliJ).

## Test accounts

All accounts use password **`Password123!`**.

| Role | Email |
|---|---|
| Admin | `admin@wasac.rw` |
| Operator | `operator@wasac.rw` |
| Finance | `finance@wasac.rw` |
| Customer | `customer@wasac.rw` |

Public signup only creates `ROLE_CUSTOMER`. After signing up all four users, assign elevated roles before testing protected admin/operator/finance endpoints.

### Option A — SQL role bootstrap (first run)

```sql
\c wasac_billing

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u JOIN roles r ON r.name = 'ROLE_ADMIN'
WHERE u.email = 'admin@wasac.rw' ON CONFLICT DO NOTHING;

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u JOIN roles r ON r.name = 'ROLE_OPERATOR'
WHERE u.email = 'operator@wasac.rw' ON CONFLICT DO NOTHING;

INSERT INTO user_roles (user_id, role_id)
SELECT u.id, r.id FROM users u JOIN roles r ON r.name = 'ROLE_FINANCE'
WHERE u.email = 'finance@wasac.rw' ON CONFLICT DO NOTHING;
```

Log in again after role changes so JWTs include the new authorities.

### Option B — API role assignment (after Option A grants admin)

Use `PUT /api/users/{userId}/roles` with admin token:

```json
{ "roles": ["ROLE_OPERATOR"] }
```

## Recommended test order

### Phase 1 — Authentication

1. Sign up admin, operator, finance, and customer (`POST /api/auth/signup`).
2. Bootstrap roles (SQL above, or admin API after SQL grants admin).
3. Log in each user (`POST /api/auth/login`) and save tokens.
4. Negative: duplicate email signup → `409`; invalid role on signup → `422`; wrong password → `401`.

### Phase 2 — Role-based access

| Endpoint group | Path prefix | Allowed roles |
|---|---|---|
| Users | `/api/users` | `ROLE_ADMIN` |
| Customers | `/api/customers` | `ROLE_ADMIN`, `ROLE_OPERATOR` |
| Meters | `/api/meters` | `ROLE_ADMIN`, `ROLE_OPERATOR` |
| Readings | `/api/readings` | `ROLE_OPERATOR` |
| Tariffs | `/api/tariffs` | `ROLE_ADMIN` |
| Bills | `/api/bills` | `ROLE_ADMIN`, `ROLE_FINANCE`, `ROLE_CUSTOMER` |
| Bill approval | `PUT /api/bills/{id}/approve` | `ROLE_ADMIN`, `ROLE_FINANCE` |
| Monthly generation | `POST /api/bills/generate` | `ROLE_ADMIN` |
| Payments | `/api/payments` | `ROLE_FINANCE`, `ROLE_CUSTOMER` |
| Notifications | `/api/notifications` | `ROLE_ADMIN`, `ROLE_CUSTOMER` |

Run forbidden tests: customer token on `/api/tariffs` → `403`; no token → `401`.

### Phase 3 — Happy-path business flow

1. **Admin** — list users; create water tariff.
2. **Operator** — create customer; create meter; capture meter reading.
3. **Admin/Finance/Customer** — generate bill from reading (`POST /api/bills/generate/{readingId}`).
4. **Finance** — approve bill (`PUT /api/bills/{billId}/approve` with `"status": "APPROVED"`).
5. **Finance** — record partial then final payment; verify bill status becomes `PAID`.
6. **Customer** — list bills and payments.

### Phase 4 — Invalid / business-rule cases

| Scenario | Endpoint | Expected |
|---|---|---|
| Duplicate customer national ID | `POST /api/customers` | `409 Conflict` |
| Duplicate meter number | `POST /api/meters` | `409 Conflict` |
| Meter on inactive customer | `POST /api/meters` | `422 Unprocessable Entity` |
| Current reading ≤ previous | `POST /api/readings` | `400 Bad Request` |
| Duplicate reading (same meter/month/year) | `POST /api/readings` | `409 Conflict` |
| Reading on inactive meter | `POST /api/readings` | `422 Unprocessable Entity` |
| No Authorization header | any protected route | `401 Unauthorized` |
| Wrong role token | role-restricted route | `403 Forbidden` |

## Response format

Success:

```json
{
  "success": true,
  "message": "Operation successful",
  "data": { }
}
```

Error:

```json
{
  "success": false,
  "message": "Error summary",
  "errors": ["Detail message"]
}
```

## How to run

### VS Code REST Client

1. Install **REST Client** extension.
2. Open `wasac-billing.http`.
3. Click **Send Request** above each `###` block in order.
4. Variables (`@adminToken`, `@customerId`, etc.) populate automatically from named responses.

### IntelliJ HTTP Client

1. Open `wasac-billing.http`.
2. Click the green run icon beside each request.
3. View captured variables in the **Run** tool window.

### Postman

1. **Import** → `postman-collection.json`.
2. Run the **Setup** folder first, then **Happy Path**, then **Negative Tests**.
3. Collection variables update via test scripts after login and create calls.

## Variables to track

```text
adminToken, operatorToken, financeToken, customerToken
adminUserId, operatorUserId, financeUserId, customerUserId
customerId, meterId, readingId, tariffId, billId, paymentId
```

See `test-data.md` for sample JSON bodies and expected calculations.
