# ☕ Modoria Backend — Spring Boot 3 & Java 21

Modoria's backend is a high-performance, secure, and scalable REST API designed to power a seasonal luxury e-commerce experience.

## 🚀 Technical Highlights

- **Architecture**: Domain-Driven Design (DDD) inspired structure with clear separation of concerns (Application, Domain, Infrastructure).
- **Security**: Stateless JWT Authentication with role-based access control (RBAC).
- **Real-time**: WebSocket integration for live AI chatbot and human human-agent support.
- **Database**: PostgreSQL with Liquibase for reliable schema versioning and migrations.
- **Tools**: Lombok, MapStruct, JUnit 5, Mockito.

## 📁 Key Modules

- `com.modoria.identity`: Authentication, Role management, and Token Blacklisting.
- `com.modoria.catalog`: Product management with advanced search Specification API.
- `com.modoria.order`: Shopping cart, order processing, and dynamic PDF invoice generation.
- `com.modoria.chat`: WebSocket-based support session management with AI-to-Human handover logic.
- `com.modoria.ai`: Intelligent shopping assistant integration.

## 🛠️ Getting Started

### Prerequisites
- JDK 21
- Maven 3.9+
- PostgreSQL (Dockerized)

### Local Development
1. **Infrastructure**:
   ```bash
   docker-compose up -d
   ```
2. **Build & Run**:
   ```bash
   .\mvnw.cmd clean spring-boot:run
   ```
3. **API Documentation**:
   Access Swagger UI at `http://localhost:8080/swagger-ui/index.html`

## 🧪 Testing & Quality
Run the full test suite (Unit + Integration):
```bash
.\mvnw.cmd test
```

## 📜 Coding Guidelines (Backend)
- Use **Conventional Commits**.
- Strictly adhere to **DTO mapping** via MapStruct (avoid exposing Entities).
- Handle all edge cases via `GlobalExceptionHandler`.

---
© 2026 Modoria Backend Team.