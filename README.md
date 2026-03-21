
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
- PostgreSQL 17

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

## Docker (Frontend + Backend + Postgres + pgAdmin)

1. Create an env file from the template:
   ```bash
   cp .env.example .env
   ```
2. Start the full stack locally:
   ```bash
   docker compose up -d --build
   ```
3. Access services:
   - Frontend: `http://localhost:4200`
   - Backend API: `http://localhost:8081`
   - pgAdmin (database UI): `http://localhost:5050`

Notes:
- This Docker setup is development-only for now.
- Database image is `postgres:17-alpine`.
- Backend runs with `SPRING_PROFILES_ACTIVE=dev`.

### pgAdmin first connection
- Login with `PGADMIN_EMAIL` and `PGADMIN_PASSWORD` from `.env`.
- Add server with:
  - Host: `postgres`
  - Port: `5432`
  - Username: `postgres`
  - Password: value of `DB_PASSWORD`

## Documentation
- See `Backend/README.md` and `Frontend/README.md` for detailed module and setup info.

## GitHub Actions CI

Workflow file: `.github/workflows/ci.yml`

What it does:
1. Runs backend tests (`mvn test`) on PRs and pushes to `main`.
2. Runs frontend build (`npm run build`) on PRs and pushes to `main`.

No deployment step is included.

## Project Links

- Repository: [Modoria_Fil_Rouge](https://github.com/Ibrahim-Nidam/Modoria_Fil_Rouge.git)
- Specification: [Project Specification (Overleaf)](https://www.overleaf.com/read/brggjhctrgtn#add734)
- Jira Board: [MFR Project (Jira)](https://ibrahimnidam-22.atlassian.net/jira/software/projects/MFR/summary?atlOrigin=eyJpIjoiNTMyNjUyOTYwOGNhNDg1YTljNzdmMWNiNmFmN2UwYjMiLCJwIjoiaiJ9)
- Use Case Diagram: [Use Case UML (Lucidchart)](https://lucid.app/lucidchart/6ec880ac-6930-4595-8f2d-d454b9a0bc32/edit?view_items=VDxFFDiTvS14&page=0_0&invitationId=inv_792d4a2f-7c88-45d5-bbff-6adb6331291f)
- Class Diagram: [Class Diagram UML (Lucidchart)](https://lucid.app/lucidchart/bd1d5b85-4864-4102-92cc-056d1c8d9d66/edit?viewport_loc=-144%2C476%2C2011%2C1008%2Ckb8yzNXKq2YA2&invitationId=inv_ed4b618f-3ee5-47a7-b3d0-5b489e191d02)
- Slide Presentation: [Project Presentation (Canva)](https://www.canva.com/design/DAG3oRK0Olc/CH4ZSWbg_fIlHTYe5kn6QA/view?utm_content=DAG3oRK0Olc&utm_campaign=designshare&utm_medium=link2&utm_source=uniquelinks&utlId=hb8891cc6fd)

## App Screenshots

> Screenshots are stored in the `docs/` folder.

### Home Page
![Home Page](docs/Home%20Page.png)

### Catalog Page
![Catalog Page](docs/Catalog%20Page.png)

### Product Details Page
![Product Details Page](docs/Product%20Details%20Page.png)

### Cart / Checkout Flow
![Cart and Checkout Flow](docs/Cart%20%20Checkout%20Flow.png)

### Admin Dashboard
![Admin Dashboard](docs/Admin%20Dashboard.png)

---
© 2026 Modoria Project Team
