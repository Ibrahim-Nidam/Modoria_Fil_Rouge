# 🛍️ Modoria — Intelligent & Immersive Seasonal E-commerce

Modoria is a premium, masculine-editorial e-commerce platform built with a modern tech stack. This project is structured as a monorepo containing both the Spring Boot backend and the Angular standalone frontend.

## 📁 Project Structure

- **[Backend/](./Backend)**: Spring Boot 3 + Java 21. High-performance REST API with JWT, Redis, and WebSockets.
- **[Frontend/](./Frontend)**: Angular 21 Standalone. Immersive seasonal UI themes and state-of-the-art design.

## 🚀 Quick Start (Monorepo)

### Prerequisites
- **Java 21+**
- **Node.js 22+**
- **Maven**
- **Docker & Docker Compose**

### Running the System
1. **Infrastructure**: Start the database and Redis using Docker.
   ```bash
   cd Backend
   docker-compose up -d
   ```
2. **Backend**: Start the Spring Boot application.
   ```bash
   cd Backend
   .\mvnw.cmd spring-boot:run
   ```
3. **Frontend**: (Work in Progress)
   ```bash
   cd Frontend
   npm install
   npm start
   ```

---
© 2026 Modoria Team. All rights reserved.
