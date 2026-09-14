# Made By Her — Spring Boot Backend

Backend for the Made By Her marketplace shown in the supplied UI.

## Stack

- Java 17
- Spring Boot 3.5.5
- Spring Web
- Spring Data JPA / Hibernate
- PostgreSQL
- Spring Security
- JWT
- Lombok
- Maven

## 1. Create PostgreSQL database

```sql
CREATE DATABASE made_by_her;
```

Then change credentials in:

`src/main/resources/application.properties`

## 2. Open in IntelliJ IDEA

Open the folder containing `pom.xml`.

Use:
- JDK 17+
- Maven
- Enable annotation processing for Lombok if IntelliJ asks.

Run:

```bash
mvn spring-boot:run
```

or run `MadeByHerApplication`.

Backend:
`http://localhost:8080`

## 3. Seeded admin

Email:
`admin@madebyher.in`

Password:
`Admin@123`

CHANGE THIS PASSWORD / REMOVE THE SEEDER BEFORE PRODUCTION.

## Authentication

Register/login:

`POST /api/auth/register`
`POST /api/auth/login`

For protected APIs send:

`Authorization: Bearer <JWT>`

## Main API groups

### Public/customer
- GET /api/products
- GET /api/products/{id}
- GET /api/products/featured
- GET /api/categories
- GET /api/products/{productId}/reviews

### Customer account
- GET/POST/PUT/DELETE /api/addresses
- GET /api/cart
- POST /api/cart/items
- PUT /api/cart/items/{itemId}
- DELETE /api/cart/items/{itemId}
- GET /api/wishlist
- POST /api/wishlist/toggle/{productId}
- POST /api/orders
- GET /api/orders/my
- GET /api/orders/my/{id}
- POST /api/products/{productId}/reviews

### Village Partner
- GET /api/partner/me
- GET /api/partner/women
- POST /api/partner/women
- PUT /api/partner/women/{id}
- DELETE /api/partner/women/{id}
- PUT /api/partner/profile
- POST /api/products
- PUT /api/products/{id}
- DELETE /api/products/{id}
- PATCH /api/orders/{id}/status

Current rule:
1 partner -> maximum 5 active women.
Change `app.partner.max-women` later if needed.

### Admin
- GET /api/admin/dashboard
- GET /api/admin/partners/pending
- PATCH /api/admin/partners/{id}/approve?approve=true
- PATCH /api/admin/products/{id}/approve?approve=true
- POST/PUT/DELETE /api/admin/categories
- GET /api/orders/admin
- PATCH /api/orders/{id}/payment

## Important next production integrations

This starter deliberately leaves external services behind clean APIs. Add:

1. Razorpay/Stripe payment gateway
2. Cloudinary/S3 image storage
3. Shiprocket/Delhivery/other logistics API
4. OTP login/phone verification
5. Email/SMS/WhatsApp notifications
6. Refund/payment webhook verification
7. GST/invoice generation
8. Payouts to women and Village Partners
9. Audit logs
10. Rate limiting
11. Redis caching
12. Search engine (optional, later)
13. Product moderation workflow
14. Real analytics / earnings dashboard

## Business-rule note

The sample order split is:
- 70% woman
- 10% Village Partner
- 20% platform

This is ONLY a placeholder example. Move these percentages into configuration/database before launch and finalize them with your actual business model.

## Suggested frontend mapping

Homepage:
- `/api/products/featured`
- `/api/categories`
- `/api/products`

Shop:
- `/api/products?search=&categoryId=&page=&size=`

Product page:
- `/api/products/{id}`
- `/api/products/{id}/reviews`

Partner dashboard:
- `/api/partner/me`
- `/api/partner/women`
- `/api/products`
- `/api/orders/{id}/status`

Admin:
- `/api/admin/dashboard`
- `/api/admin/partners/pending`
- `/api/admin/products/{id}/approve`

## Security notes

Do not store Aadhaar numbers. The example stores only `aadhaarLast4` and even that should be removed unless there is a real compliance need.

Do not trust price/commission values from the frontend. The backend must calculate the final order amount.

For production, use environment variables for database credentials and JWT secret.
