
# Modoria — Fullstack E-Commerce Platform

Modoria is a premium, seasonal e-commerce platform featuring a modern Angular 21 frontend and a robust Spring Boot 3 backend. It supports secure authentication, order management, and real-time support chat.

## Project Structure

- **[Backend/](./Backend)** — Java 21, Spring Boot 3.5, PostgreSQL, WebSocket, REST APIs
- **[Frontend/](./Frontend)** — Angular 21 standalone, minimalist luxury UI

## Key Features

- User authentication and role-based access (ADMIN, CLIENT, AGENT)
- Product catalog and advanced filtering
- Order placement, history, and admin management
- Customer support chat (REST + WebSocket backend)
- Branded HTML email notifications

## Getting Started

### Prerequisites
- Java 21+
- Node.js 22+, npm 10+
- Maven 3.9+
- PostgreSQL

### Setup
1. **Clone the repository:**
   ```bash
   git clone https://github.com/Ibrahim-Nidam/Modoria_Fil_Rouge.git
   cd Modoria_Fil_Rouge
   ```
2. **Configure the database:**
   - Update `Backend/src/main/resources/application-dev.yml` with your PostgreSQL settings.
3. **Start the backend:**
   ```bash
   cd Backend
   mvnd clean spring-boot:run
   ```
4. **Start the frontend:**
   ```bash
   cd ../Frontend
   npm install
   npm start
   ```

## Documentation
- See `Backend/README.md` and `Frontend/README.md` for detailed module and setup info.

---
© 2026 Modoria Project Team
