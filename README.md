# 🛍️ Modoria — Intelligent & Immersive Seasonal E-commerce Platform

## 📌 Project Description

**Modoria** is a **full-stack web application** built with **Spring Boot 3** and **Angular 18**, designed as a **seasonal e-commerce platform** with dynamic UI themes, real-time support, and AI-ready extensions.

It enables **secure shopping**, **order management**, **customer support**, and **seasonal immersive experiences**. The system leverages **JWT authentication**, **Redis caching**, **WebSocket chat**, and optional **GraphQL endpoints** for advanced querying.

---

## 🎯 Main Objectives

* Offer a **dynamic seasonal shopping experience** (winter, spring, summer, autumn)
* Manage **products, orders, carts, reviews, users**, and **real-time chat**
* Provide **secure authentication** with role-based access (Admin, Customer, Support)
* Support **high-performance queries** using **Redis caching**
* Enable **real-time chat** between customers and support
* Maintain **modular, testable, and scalable architecture**
* Facilitate **Liquibase-based DB versioning**, Docker deployment, and CI/CD

---

## 🔗 Useful Links

* 📂 **[GitHub Repository](https://github.com/Ibrahim-Nidam/Modoria_Fil_Rouge.git)** – Full source code for Modoria
* 📝 **[Jira Board](https://ibrahimnidam-22.atlassian.net/jira/software/projects/MFR/summary?atlOrigin=eyJpIjoiNTMyNjUyOTYwOGNhNDg1YTljNzdmMWNiNmFmN2UwYjMiLCJwIjoiaiJ9)** – Project backlog and sprint management
* 🗂️ **[Cahier de Charges](https://www.overleaf.com/read/brggjhctrgtn#add734)** – Detailed project requirements and specifications
* 📊 **[UML Use Case Diagram](https://lucid.app/lucidchart/845c7e1a-e9f4-4f45-a128-f287f0e1cef2/edit?viewport_loc=-11%2C-11%2C2217%2C1052%2C0_0&invitationId=inv_c87ba5fe-2fd8-422c-b0b9-448f1cc19287)** – Visual representation of system actors and interactions
* 🧩 **[UML Class Diagram](https://lucid.app/lucidchart/fe62cdba-65e5-4a14-80b7-e57cedb2b1d5/edit?viewport_loc=-11%2C-11%2C2217%2C1052%2C0_0&invitationId=inv_54dda570-4a93-4dd5-94b9-252b3b58d541)** – Full class relationships and entities
* 🎤 **[Presentation Slides](https://www.canva.com/design/DAG3oRK0Olc/CH4ZSWbg_fIlHTYe5kn6QA/view?utm_content=DAG3oRK0Olc&utm_campaign=designshare&utm_medium=link2&utm_source=uniquelinks&utlId=hb8891cc6fd)** – Project presentation for stakeholders

---

## 🛠️ Technologies Used

* **Java 21** & **Spring Boot 3**
* **Angular 18**
* **Spring Security** + **JWT Authentication**
* **Spring Data JPA** + **PostgreSQL (QA)** / **H2 (Dev)**
* **Redis** for caching
* **WebSocket** for real-time chat
* **Liquibase** for database migrations
* **GraphQL (optional)** for flexible queries
* **MapStruct** for DTO mapping
* **REST API** + **Swagger/OpenAPI**
* **JUnit 5 & Mockito** for testing
* **SonarLint** for static code analysis
* **Docker & Docker-Compose**
* **Seasonal dynamic UI themes**
* **Optional AI recommendation engine** (future-ready)

---

## 🧩 Key Features

✅ **CRUD management** for Products, Orders, Cart, Reviews, Users

✅ **Secure role-based authentication** (Admin, Customer, Support)

✅ **Real-time WebSocket chat** between Customer and Support

✅ **Seasonal UI themes** applied dynamically (Winter, Spring, Summer, Autumn)

✅ **Redis caching** for improved performance of frequent queries

✅ **Liquibase changelogs** for database evolution

✅ Optional **GraphQL API** for advanced querying

✅ REST API documented with **Swagger/OpenAPI**

✅ **Dockerized deployment** ready

✅ Comprehensive **unit and integration testing**

---

## 📂 Project Structure

```
modoria/
│
├── README.md
├── .gitignore
├── .gitattributes
├── docker-compose.yml
├── pom.xml
│
├── frontend/
│   ├── angular.json
│   ├── package.json
│   ├── tsconfig.json
│   ├── src/
│   │   ├── app/
│   │   │   ├── core/
│   │   │   │   ├── auth/
│   │   │   │   │   ├── auth.service.ts
│   │   │   │   │   ├── jwt.interceptor.ts
│   │   │   │   │   └── auth.guard.ts
│   │   │   │   ├── services/
│   │   │   │   │   ├── api.service.ts
│   │   │   │   │   ├── websocket.service.ts
│   │   │   │   │   └── cache.service.ts
│   │   │   │   └── models/
│   │   │   │       ├── product.model.ts
│   │   │   │       ├── order.model.ts
│   │   │   │       ├── user.model.ts
│   │   │   │       └── chat-message.model.ts
│   │   │   ├── features/
│   │   │   │   ├── home/
│   │   │   │   ├── products/
│   │   │   │   ├── cart/
│   │   │   │   ├── orders/
│   │   │   │   ├── reviews/
│   │   │   │   ├── admin/
│   │   │   │   └── support/
│   │   │   ├── shared/
│   │   │   │   ├── components/
│   │   │   │   ├── directives/
│   │   │   │   └── pipes/
│   │   │   └── app.component.ts
│   │   ├── assets/
│   │   │   ├── themes/
│   │   │   │   ├── winter.css
│   │   │   │   ├── summer.css
│   │   │   │   ├── autumn.css
│   │   │   │   └── spring.css
│   │   │   └── images/
│   │   └── environments/
│   │       ├── environment.ts
│   │       └── environment.prod.ts
│
├── backend/
│   ├── src/
│   │   ├── main/java/com/modoria/
│   │   │   ├── ModoriaApplication.java
│   │   │   ├── config/
│   │   │   │   ├── SecurityConfig.java
│   │   │   │   ├── WebSocketConfig.java
│   │   │   │   ├── RedisConfig.java
│   │   │   │   ├── AppConfig.java
│   │   │   │   └── OpenApiConfig.java
│   │   │   ├── controller/
│   │   │   │   ├── AuthController.java
│   │   │   │   ├── ProductController.java
│   │   │   │   ├── OrderController.java
│   │   │   │   ├── CartController.java
│   │   │   │   ├── ReviewController.java
│   │   │   │   ├── ChatController.java
│   │   │   │   └── UserController.java
│   │   │   ├── dto/
│   │   │   │   ├── ProductDTO.java
│   │   │   │   ├── OrderDTO.java
│   │   │   │   ├── ReviewDTO.java
│   │   │   │   ├── ChatMessageDTO.java
│   │   │   │   ├── CartDTO.java
│   │   │   │   └── UserDTO.java
│   │   │   ├── model/
│   │   │   │   ├── Product.java
│   │   │   │   ├── Order.java
│   │   │   │   ├── Cart.java
│   │   │   │   ├── Review.java
│   │   │   │   ├── ChatMessage.java
│   │   │   │   ├── User.java
│   │   │   │   └── enums/
│   │   │   │       ├── RoleType.java
│   │   │   │       ├── OrderStatus.java
│   │   │   │       └── SeasonTheme.java
│   │   │   ├── repository/
│   │   │   │   ├── ProductRepository.java
│   │   │   │   ├── OrderRepository.java
│   │   │   │   ├── ReviewRepository.java
│   │   │   │   ├── UserRepository.java
│   │   │   │   ├── CartRepository.java
│   │   │   │   └── ChatMessageRepository.java
│   │   │   ├── service/
│   │   │   │   ├── impl/
│   │   │   │   │   ├── ProductServiceImpl.java
│   │   │   │   │   ├── OrderServiceImpl.java
│   │   │   │   │   ├── ReviewServiceImpl.java
│   │   │   │   │   ├── ChatServiceImpl.java
│   │   │   │   │   └── UserServiceImpl.java
│   │   │   │   └── interfaces/
│   │   │   │       ├── ProductService.java
│   │   │   │       ├── OrderService.java
│   │   │   │       ├── ReviewService.java
│   │   │   │       ├── ChatService.java
│   │   │   │       └── UserService.java
│   │   │   ├── util/
│   │   │   │   ├── DateUtils.java
│   │   │   │   ├── JwtUtils.java
│   │   │   │   ├── ThemeUtils.java
│   │   │   │   └── CacheUtils.java
│   │   │   ├── exception/
│   │   │   │   ├── GlobalExceptionHandler.java
│   │   │   │   └── ResourceNotFoundException.java
│   │   │   └── security/
│   │   │       ├── JwtAuthenticationFilter.java
│   │   │       ├── JwtService.java
│   │   │       └── CustomUserDetailsService.java
│   │   ├── main/resources/
│   │   │   ├── application.yml
│   │   │   ├── application-qa.yml
│   │   │   ├── application-dev.yml
│   │   │   ├── application-prod.yml
│   │   │   ├── db/changelog/
│   │   │   │   ├── db.changelog-master.xml
│   │   │   │   ├── db.changelog-v1.0-initial.xml
│   │   │   │   ├── db.changelog-v2.0-orders-reviews.xml
│   │   │   │   └── db.changelog-v3.0-chat.xml
│   │   │   ├── static/
│   │   │   │   └── openapi.yaml
│   │   │   └── templates/
│   │   │       └── email/
│   │   │           ├── order-confirmation.html
│   │   │           └── password-reset.html
│   │   └── test/java/com/modoria/
│   │       ├── service/
│   │       │   ├── ProductServiceTest.java
│   │       │   ├── OrderServiceTest.java
│   │       │   └── ChatServiceTest.java
│   │       └── security/
│   │           └── JwtUtilsTest.java
│
├── docs/
│   ├── diagrams/
│   │   ├── class-diagram.png
│   │   ├── usecase-diagram.png
│   │   └── architecture.png
│   ├── api/
│   │   └── insomnia_collection.json
│   └── presentation/
│       ├── technical_presentation.pptx
│       └── client_presentation.pptx
│
└── logs/
    └── app.log

```

---

## ⚙️ Main Features by Role

| Role         | Functionalities                                                           |
| ------------ | ------------------------------------------------------------------------- |
| **Admin**    | Manage Products, Orders, Users, Reviews, Themes, Access GraphQL endpoints |
| **Customer** | Browse Products, Manage Cart & Orders, Leave Reviews, Chat with Support   |
| **Support**  | Respond to Customer Chat, View Orders & Issues, Moderate Reviews          |

---

## 🧪 Run & Test

```bash
# Backend
mvn spring-boot:run

# Frontend
cd frontend
npm install
ng serve
```

**Swagger UI:**
👉 [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

**Postman/Insomnia Collection:**
Import `/docs/api/insomnia_collection.json`

**Run Tests:**

```bash
  mvn test
```

---

## 📸 Application Screenshots

*(Insert screenshots of seasonal UI, dashboard, product catalog, and chat)*

---

## ⚡ Example API Endpoints

| Entity          | Base URL            | Methods                |
| --------------- | ------------------- | ---------------------- |
| `/api/products` | Manage products     | GET, POST, PUT, DELETE |
| `/api/orders`   | Manage orders       | GET, POST, PUT, DELETE |
| `/api/cart`     | Manage cart         | GET, POST, PUT, DELETE |
| `/api/reviews`  | Manage reviews      | GET, POST, PUT, DELETE |
| `/api/users`    | Manage users        | GET, POST, PUT, DELETE |
| `/api/chat`     | Real-time messaging | GET, POST (WebSocket)  |

---