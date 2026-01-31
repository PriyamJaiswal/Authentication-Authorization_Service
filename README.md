# Authentication & Authorization Service

A production-ready REST API built with Spring Boot implementing
JWT-based authentication and role-based access control (RBAC).

Key features
- JWT authentication (stateless)
- Role-based and permission-level authorization
- Token management (issue/revoke)
- Multi-device sessions and audit logging
- PostgreSQL (users, roles) + MongoDB (tokens, logs)

Quick tech summary
- Java 21, Spring Boot 4.0.2
- Spring Security, Spring Data JPA, Spring Data MongoDB
- PostgreSQL + MongoDB
- JWT (jjwt), BCrypt, Maven, Lombok

Why mobile-friendly README?
- Short paragraphs and lines
- No wide ASCII diagrams
- Compact code examples for easy viewing on phones

Getting started

1. Clone
```
git clone https://github.com/Authentication-Authorization_Service/auth-service.git
cd Authentication-Authorization_Service
```

2. Databases
- PostgreSQL (users, roles, permissions)
- MongoDB (tokens, login history)



3. Build & run
```
mvn clean install
mvn spring-boot:run
```
App runs at: http://localhost:8080

Important: do NOT commit secrets. Replace jwt.secret with a secure key in production.

Configuration (examples)
- application.properties (short)
```
server.port=8080
spring.datasource.url=jdbc:postgresql://localhost:5432/authdb
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.jpa.hibernate.ddl-auto=update
spring.data.mongodb.uri=mongodb://localhost:27017/authdb

# JWT
jwt.secret=your-secret-key-here
jwt.expiration=86400000
jwt.refresh-expiration=604800000
```

Architecture (compact)
- Client (React / Mobile / Postman) → HTTP/JSON
- Spring Security filter chain (CORS, JWT, auth)
- REST Controllers → Service layer → Repositories
- PostgreSQL: users, roles, permissions
- MongoDB: tokens, audit logs

Database overview (short)
- users (UUID PK): username, email, password (BCrypt), enabled, created_at
- roles, permissions (many-to-many)
- Mongo tokens: userId, token, issuedAt, expiresAt, revoked, ipAddress, userAgent

API highlights
- POST /auth/register — register user
- POST /auth/login — returns JWT
- POST /auth/logout — revoke token
- GET /auth/validate — validate token
- CRUD users and roles endpoints (require permissions)

Small example — login
```
POST /auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123"
}
```
Response (example):
```
{
  "message": "Login successful",
  "token": "eyJ... (Bearer token)"
}
```

Security notes (short)
- Passwords: BCrypt
- JWT: sign, verify, set expirations
- Use HTTPS in production
- Use environment variables for DB and JWT secrets

CORS
Edit CorsConfig.java to allow your frontend origins:
```
configuration.setAllowedOrigins(Arrays.asList(
  "http://localhost:3000",
  "https://yourapp.com"
));
```

Default dev credentials
- Username: admin
- Password: admin123
(Please change immediately in production.)

Testing
- Health check: GET http://localhost:8080/auth/validate
- Import Postman collection (if available) and set baseUrl = http://localhost:8080

Contributing
- Fork → branch → PR
- Follow Java conventions and add tests for new features

Support
- Issues: https://github.com/PriyamJaiswal/Authentication-Authorization_Service/issues
- Email: priyamj608@gmail.com

License & acknowledgments
- Thanks to Spring, JWT.io, and community tutorials.
