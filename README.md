<p align="center">
  <img src="https://img.shields.io/badge/Spring%20Boot-4.0.2-6DB33F?style=for-the-badge&logo=springboot&logoColor=white" />
  <img src="https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" />
  <img src="https://img.shields.io/badge/JWT-Auth-000000?style=for-the-badge&logo=jsonwebtokens&logoColor=white" />
  <img src="https://img.shields.io/badge/PostgreSQL-4169E1?style=for-the-badge&logo=postgresql&logoColor=white" />
  <img src="https://img.shields.io/badge/MongoDB-47A248?style=for-the-badge&logo=mongodb&logoColor=white" />
</p>

<h1 align="center">🔐 Authentication & Authorization Service</h1>

<p align="center">
  <b>A production-ready REST API implementing JWT-based authentication and fine-grained Role-Based Access Control (RBAC)</b>
</p>

<p align="center">
  Built with <b>Spring Boot 4.0</b> · Secured with <b>Spring Security</b> · Powered by <b>PostgreSQL + MongoDB</b>
</p>

---

## ✨ Features

| Category | Details |
|----------|---------|
| 🔑 **Authentication** | Stateless JWT auth with access & refresh tokens |
| 🛡️ **Authorization** | Fine-grained RBAC with role → permission mapping |
| 📋 **Token Management** | Issue, validate, and revoke tokens per device |
| 👥 **User Management** | Full CRUD with enable/disable, role assignment |
| 🎭 **Role Management** | Dynamic roles with assignable permissions |
| 🗄️ **Polyglot Persistence** | PostgreSQL for entities, MongoDB for tokens & audit |
| ⚡ **Auto-Initialization** | Seeds default admin user, roles, and permissions on startup |
| 🧹 **Error Handling** | Global exception handler with structured JSON responses |

---

## 🏗️ Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                        Client Request                           │
│              (React · Mobile · Postman · cURL)                  │
└──────────────────────────┬──────────────────────────────────────┘
                           │ HTTP / JSON
                           ▼
┌─────────────────────────────────────────────────────────────────┐
│                   Spring Security Filter Chain                  │
│  ┌──────────┐   ┌────────────────────┐   ┌──────────────────┐  │
│  │   CORS   │──▶│  JWT Auth Filter   │──▶│  Authorization   │  │
│  │  Filter   │   │  (Token Parsing)   │   │  (RBAC Check)    │  │
│  └──────────┘   └────────────────────┘   └──────────────────┘  │
└──────────────────────────┬──────────────────────────────────────┘
                           ▼
┌─────────────────────────────────────────────────────────────────┐
│                      REST Controllers                           │
│     /auth/*          /users/*            /roles/*               │
└──────────────────────────┬──────────────────────────────────────┘
                           ▼
┌─────────────────────────────────────────────────────────────────┐
│                      Service Layer                              │
│   AuthService        UserService         RoleService            │
└──────────┬───────────────────────────────────────┬──────────────┘
           ▼                                       ▼
  ┌─────────────────┐                    ┌─────────────────┐
  │   PostgreSQL     │                    │    MongoDB       │
  │  ─────────────   │                    │  ─────────────   │
  │  Users           │                    │  Token Documents │
  │  Roles           │                    │  (JWT storage,   │
  │  Permissions     │                    │   device info,   │
  │  (JPA/Hibernate) │                    │   audit trail)   │
  └─────────────────┘                    └─────────────────┘
```

---

## 📁 Project Structure

```
src/main/java/com/authenticationAPI/Authentication_System/
│
├── configuration/
│   ├── CorsConfig.java              # Cross-origin resource sharing
│   ├── DataInitializer.java         # Seeds default roles & admin user
│   ├── PasswordEncoderConfig.java   # BCrypt password encoder bean
│   └── SecurityConfig.java          # Security filter chain configuration
│
├── controller/
│   ├── AuthController.java          # Login, register, logout, validate
│   ├── RoleController.java          # Role CRUD & permission management
│   └── UserController.java          # User CRUD & role assignment
│
├── exceptionHandling/
│   ├── GlobalExceptionHandler.java  # Centralized error responses
│   └── UnauthorizedException.java   # Custom 401 exception
│
├── model/
│   ├── Permission.java              # Permission entity (JPA)
│   ├── Role.java                    # Role entity with permissions (JPA)
│   ├── TokenDocument.java           # JWT token document (MongoDB)
│   └── User.java                    # User entity with roles (JPA)
│
├── repo/
│   ├── PermissionRepository.java    # JPA repository
│   ├── RoleRepository.java          # JPA repository
│   ├── TokenRepository.java         # MongoDB repository
│   └── UserRepository.java          # JPA repository
│
├── securityComponent/
│   ├── CustomUserDetailsService.java # Loads user for Spring Security
│   ├── JwtAuthenticationFilter.java  # Intercepts & validates JWT tokens
│   ├── JwtTokenProvider.java         # Token generation & parsing
│   └── UserPrincipal.java            # Security principal wrapper
│
├── service/
│   ├── AuthService.java             # Authentication business logic
│   ├── RoleService.java             # Role management logic
│   └── UserService.java             # User management logic
│
└── AuthenticationSystemApplication.java  # Spring Boot entry point
```

---

## 🔌 API Reference

### Authentication — `/auth`

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| `POST` | `/auth/register` | Register a new user | ❌ Public |
| `POST` | `/auth/login` | Authenticate & get JWT | ❌ Public |
| `POST` | `/auth/logout` | Revoke current token | 🔒 Bearer |
| `GET` | `/auth/validate` | Validate a token | 🔒 Bearer |

### User Management — `/users`

| Method | Endpoint | Description | Permission |
|--------|----------|-------------|------------|
| `GET` | `/users` | List all users | `READ_USER` |
| `GET` | `/users/{id}` | Get user by ID | `READ_USER` |
| `GET` | `/users/username/{username}` | Get user by username | `READ_USER` |
| `POST` | `/users/create` | Create a user | `CREATE_USER` |
| `PUT` | `/users/{id}/update` | Update user details | `UPDATE_USER` |
| `DELETE` | `/users/{id}/delete` | Delete a user | `DELETE_USER` |
| `POST` | `/users/{id}/roles/assign` | Assign roles to user | `MANAGE_ROLES` |
| `POST` | `/users/{id}/roles/remove` | Remove roles from user | `MANAGE_ROLES` |
| `PUT` | `/users/{id}/enable` | Enable user account | `UPDATE_USER` |
| `PUT` | `/users/{id}/disable` | Disable user account | `UPDATE_USER` |

### Role Management — `/roles`

| Method | Endpoint | Description | Permission |
|--------|----------|-------------|------------|
| `GET` | `/roles` | List all roles | `MANAGE_ROLES` |
| `GET` | `/roles/{id}` | Get role by ID | `MANAGE_ROLES` |
| `GET` | `/roles/name/{name}` | Get role by name | `MANAGE_ROLES` |
| `POST` | `/roles` | Create a new role | `MANAGE_ROLES` |
| `PUT` | `/roles/{id}` | Update a role | `MANAGE_ROLES` |
| `DELETE` | `/roles/{id}` | Delete a role | `MANAGE_ROLES` |
| `POST` | `/roles/{id}/permissions/assign` | Assign permissions | `MANAGE_ROLES` |
| `POST` | `/roles/{id}/permissions/remove` | Remove permissions | `MANAGE_ROLES` |

---

## 🚀 Getting Started

### Prerequisites

- **Java 21** or later
- **Maven 3.9+**
- **PostgreSQL** (running on port 5432)
- **MongoDB** (local or Atlas cluster)

### 1. Clone the repository

```bash
git clone https://github.com/PriyamJaiswal/Authentication-Authorization_Service.git
cd Authentication-Authorization_Service
```

### 2. Configure databases

Edit `src/main/resources/application.properties`:

```properties
# PostgreSQL
spring.datasource.url=jdbc:postgresql://localhost:5432/authdb
spring.datasource.username=your_pg_user
spring.datasource.password=your_pg_password

# MongoDB
spring.data.mongodb.uri=mongodb://localhost:27017/authdb
# Or for Atlas:
# spring.data.mongodb.uri=mongodb+srv://<USER>:<PASS>@cluster.mongodb.net/authdb

# JWT (use a strong secret in production!)
jwt.secret=your-256-bit-secret-key-here
jwt.expiration=86400000          # 24 hours
jwt.refresh-expiration=604800000  # 7 days
```

### 3. Build & run

```bash
# Build
./mvnw clean install

# Run
./mvnw spring-boot:run
```

The server starts at **`http://localhost:8080`**

### 4. Default credentials

> On first startup, the `DataInitializer` seeds a default admin account:

| Field | Value |
|-------|-------|
| Username | `admin` |
| Password | `admin123` |

> ⚠️ **Change the default password immediately in production.**

---

## 📝 Usage Examples

### Register a new user

```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_doe",
    "email": "john@example.com",
    "password": "SecurePass123!"
  }'
```

```json
{
  "message": "User registered successfully",
  "userId": "550e8400-e29b-41d4-a716-446655440000",
  "username": "john_doe"
}
```

### Login

```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123"
  }'
```

```json
{
  "message": "Login successful",
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "tokenType": "Bearer"
}
```

### Access a protected endpoint

```bash
curl http://localhost:8080/users \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9..."
```

---

## 🗃️ Database Schema

### PostgreSQL — Entity Relationships

```
┌──────────────┐       ┌──────────────┐       ┌──────────────────┐
│    users     │       │    roles     │       │   permissions    │
├──────────────┤       ├──────────────┤       ├──────────────────┤
│ id (UUID PK) │       │ id (LONG PK) │       │ id (LONG PK)     │
│ username     │──M:N──│ name         │──M:N──│ name             │
│ email        │       │ description  │       │ description      │
│ password     │       └──────────────┘       └──────────────────┘
│ enabled      │
│ created_at   │
└──────────────┘
```

### MongoDB — Token Documents

```json
{
  "_id": "ObjectId",
  "userId": "UUID",
  "token": "JWT string",
  "issuedAt": "DateTime",
  "expiresAt": "DateTime",
  "revoked": false,
  "ipAddress": "192.168.1.1",
  "userAgent": "Mozilla/5.0..."
}
```

---

## 🔒 Security Highlights

| Layer | Implementation |
|-------|---------------|
| Password Storage | BCrypt hashing (adaptive cost factor) |
| Token Signing | HMAC-SHA256 via jjwt 0.12.6 |
| Authorization | Method-level `@PreAuthorize` with permission checks |
| CORS | Configurable allowed origins in `CorsConfig.java` |
| Token Revocation | Server-side revocation check on every request |
| Request Logging | IP address and User-Agent captured per login |

---

## 🛠️ Tech Stack

| Technology | Purpose |
|------------|---------|
| **Spring Boot 4.0.2** | Application framework |
| **Spring Security** | Authentication & authorization |
| **Spring Data JPA** | PostgreSQL ORM |
| **Spring Data MongoDB** | Token document storage |
| **jjwt 0.12.6** | JWT creation & validation |
| **PostgreSQL** | Relational data (users, roles, permissions) |
| **MongoDB** | Document data (tokens, audit) |
| **Lombok** | Boilerplate reduction |
| **Jakarta Validation** | Request body validation |
| **Maven** | Build & dependency management |
| **Java 21** | Language runtime |

---

## 🤝 Contributing

1. **Fork** the repository
2. Create a feature branch — `git checkout -b feature/amazing-feature`
3. Commit your changes — `git commit -m "Add amazing feature"`
4. Push to the branch — `git push origin feature/amazing-feature`
5. Open a **Pull Request**

---

## 📬 Contact

**Priyam Jaiswal** — [priyamj608@gmail.com](mailto:priyamj608@gmail.com)

Project Link: [github.com/PriyamJaiswal/Authentication-Authorization_Service](https://github.com/PriyamJaiswal/Authentication-Authorization_Service)

---

<p align="center">
  <sub>Built with ❤️ using Spring Boot</sub>
</p>
