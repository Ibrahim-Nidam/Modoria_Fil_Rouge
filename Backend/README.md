
# Modoria Backend — Spring Boot 3 & Java 21

Modoria's backend powers a premium, seasonal e-commerce platform with robust authentication, order management, and real-time support chat.

## Features

- **Authentication & RBAC**: Secure JWT-based login, registration, and role-based access (ADMIN, CLIENT, AGENT).
- **Product Catalog**: Category, product, and image management with advanced filtering.
- **Order Management**: Place, view, and update orders; admin can manage all orders and update statuses.
- **Support Chat**: REST and WebSocket endpoints for customer support and agent workflows.
- **Email Notifications**: Branded HTML emails for order confirmation and password reset.
- **Database**: PostgreSQL with Liquibase migrations.

## Technology Stack

- Java 21, Spring Boot 3.5
- Spring Security, Spring Data JPA
- RESTful APIs for all core features
- Lombok, MapStruct, JUnit 5, Mockito

## Key Packages

- `com.modoria.identity` — User, role, and authentication logic
- `com.modoria.catalog` — Product and category management
- `com.modoria.order` — Order domain, admin order management
- `com.modoria.chat` — Support sessions, chat messages, WebSocket endpoints
- `com.modoria.shared.email` — Email service and templates

## Getting Started

### Prerequisites
- JDK 21
- Maven 3.9+
- PostgreSQL

### Local Development
1. Configure your PostgreSQL database and update `src/main/resources/application-dev.yml` as needed.
2. Run database migrations:
   ```bash
   mvnd liquibase:update
   ```
3. Build and start the backend:
   ```bash
   mvnd clean spring-boot:run
   ```

## Testing

Run all tests:
```bash
mvnd test
```

---
© 2026 Modoria Backend Team