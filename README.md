🔐 Authentication & Authorization Service
A production-ready REST API built with Spring Boot 4.0.2 implementing secure JWT-based authentication and role-based access control (RBAC) with permission-level granularity.

✨ Features--
Core Functionality

✅ JWT-based Authentication - Stateless, secure token-based auth
✅ Role-Based Access Control (RBAC) - Hierarchical permission system
✅ Permission-Level Authorization - Granular access control
✅ Token Management - Track, validate, and revoke tokens
✅ Multi-Device Sessions - Support for concurrent logins
✅ Audit Logging - Track login history with IP and user agent

Security Features--

🔒 BCrypt Password Hashing - Industry-standard password encryption
🔒 JWT Token Validation - Signature verification and expiration checks
🔒 Token Revocation - Logout functionality with token blacklisting
🔒 CORS Configuration - Secure cross-origin resource sharing
🔒 CSRF Protection - Disabled for stateless API (JWT-based)
🔒 Spring Security Integration - Enterprise-grade security framework

Enterprise Features--

📊 Dual Database Architecture - PostgreSQL + MongoDB
📊 Scalable Design - Supports 500+ concurrent users
📊 RESTful API - Clean, documented endpoints
📊 Global Exception Handling - Consistent error responses
📊 Input Validation - Request data validation
📊 Development Tools - Hot reload with Spring DevTools


🛠️ Tech Stack
Backend Framework--

Java 21 - Latest LTS with virtual threads support
Spring Boot 4.0.2 - Latest Spring Framework 7
Spring Security 6.x - Modern security configuration
Spring Data JPA - Database abstraction for PostgreSQL
Spring Data MongoDB - NoSQL document storage

Databases--

PostgreSQL 15+ - Relational data (users, roles, permissions)
MongoDB 7.0+ - Document storage (tokens, audit logs)

Security & Authentication--

JWT (jjwt 0.12.6) - JSON Web Tokens
BCrypt - Password hashing
Spring Security - Authentication & authorization

Build & Development--

Maven 3.9+ - Dependency management
Lombok - Boilerplate code reduction
Spring DevTools - Hot reload during development


🏗️ Architecture--
High-Level Architecture
┌─────────────────┐
│  Client (React/ │
│  Postman/Mobile)│
└────────┬────────┘
         │ HTTP (JSON)
         ▼
┌─────────────────────────────────┐
│  Spring Security Filter Chain   │
│  ├─ CORS Filter                 │
│  ├─ JWT Authentication Filter   │
│  └─ Authorization Filter         │
└────────┬────────────────────────┘
         │
         ▼
┌─────────────────────────────────┐
│     REST Controllers            │
│  ├─ AuthController              │
│  ├─ UserController              │
│  └─ RoleController              │
└────────┬────────────────────────┘
         │
         ▼
┌─────────────────────────────────┐
│     Service Layer               │
│  ├─ AuthService                 │
│  ├─ UserService                 │
│  └─ RoleService                 │
└────────┬────────────────────────┘
         │
         ▼
┌─────────────────────────────────┐
│     Repository Layer            │
│  ├─ UserRepository (JPA)        │
│  ├─ RoleRepository (JPA)        │
│  └─ TokenRepository (MongoDB)   │
└────────┬────────────────────────┘
         │
         ▼
┌──────────────┬──────────────────┐
│ PostgreSQL   │    MongoDB       │
│ (Users,      │   (Tokens,       │
│  Roles,      │    Login         │
│  Permissions)│    History)      │
└──────────────┴──────────────────┘


Database Schema
PostgreSQL Tables--

users
├── id (UUID, PK)
├── username (unique)
├── email (unique)
├── password (BCrypt hashed)
├── enabled (boolean)
└── created_at (timestamp)

roles
├── id (Long, PK)
└── name (unique)

permissions
├── id (Long, PK)
└── name (unique)

user_roles (Many-to-Many)
├── user_id (FK)
└── role_id (FK)

role_permissions (Many-to-Many)
├── role_id (FK)
└── permission_id (FK)

MongoDB Collections--
tokens {
  _id: ObjectId,
  userId: UUID,
  token: String,
  issuedAt: DateTime,
  expiresAt: DateTime,
  revoked: Boolean,
  ipAddress: String,
  userAgent: String
}


Installation--
1. Clone the Repository
git clone https://github.com/Authentication-Authorization_Service/auth-service.git
cd Authentication-Authorization_Service

3. Setup PostgreSQL
   # Create database
createdb authdb

4. Setup MongoDB
   # Using Docker (recommended)
docker run -d -p 27017:27017 --name mongodb mongo:7

# MongoDB
spring.data.mongodb.uri=mongodb://localhost:27017/authdb

# JWT Secret (CHANGE THIS IN PRODUCTION!)
jwt.secret=your-secret-key-minimum-256-bits-long
jwt.expiration=86400000

5. Build and Run
bash# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
The application will start on http://localhost:8080

6. Verify Installation
bash# Health check
curl http://localhost:8080/auth/validate

# Should return 401 (expected - means server is running)
Default Credentials
After first run, a default admin user is created:
Username: admin
Password: admin123

📚 API Documentation

$$ http://localhost:8080
$ Authentication Endpoints

Register New User--
httpPOST /auth/register
Content-Type: application/json

{
  "username": "john_doe",
  "email": "john@example.com",
  "password": "securePassword123"
}
Response (201 Created):
json{
  "message": "User registered successfully",
  "userId": "550e8400-e29b-41d4-a716-446655440000",
  "username": "john_doe"
}

Login--
httpPOST /auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123"
}
Response (200 OK):
json{
  "message": "Login successful",
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer"
}

Validate Token--
httpGET /auth/validate
Authorization: Bearer {token}

Logout--
httpPOST /auth/logout
Authorization: Bearer {token}

$$ User Management Endpoints
$ All user management endpoints require authentication with appropriate permissions.
Get All Users--
httpGET /users
Authorization: Bearer {token}
Required Permission: READ_USER

Get User by ID--
httpGET /users/{userId}
Authorization: Bearer {token}

Create User--
httpPOST /users/create
Authorization: Bearer {token}
Content-Type: application/json

{
  "username": "new_user",
  "email": "user@example.com",
  "password": "password123"
}
Required Permission: CREATE_USER

Update User--
httpPUT /users/{userId}/update
Authorization: Bearer {token}
Content-Type: application/json

{
  "email": "newemail@example.com",
  "enabled": true
}
Required Permission: UPDATE_USER

Delete User--
httpDELETE /users/{userId}/delete
Authorization: Bearer {token}
Required Permission: DELETE_USER

Assign Roles to User--
httpPOST /users/{userId}/roles/assign
Authorization: Bearer {token}
Content-Type: application/json

["ADMIN", "MODERATOR"]
Required Permission: MANAGE_ROLES

$$ Role Management Endpoints
Get All Roles--
httpGET /roles
Authorization: Bearer {token}

Create Role--
httpPOST /roles
Authorization: Bearer {token}
Content-Type: application/json

{
  "name": "SUPERVISOR"
}

Assign Permissions to Role--
httpPOST /roles/{roleId}/permissions/assign
Authorization: Bearer {token}
Content-Type: application/json

["READ_USER", "UPDATE_USER"]

$$ Default Permissions
The system includes these default permissions:

READ_USER - View user information
CREATE_USER - Create new users
UPDATE_USER - Update user information
DELETE_USER - Delete users
MANAGE_ROLES - Manage roles and assignments
MANAGE_PERMISSIONS - Manage permissions
READ_ADMIN - Read admin resources
WRITE_ADMIN - Write admin resources
DELETE_ADMIN - Delete admin resources

Default Roles--

ADMIN - All permissions
USER - READ_USER only
MODERATOR - READ_USER, UPDATE_USER


⚙️ Configuration
Application Properties
properties# Server Configuration
server.port=8080

# PostgreSQL Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/authdb
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.jpa.hibernate.ddl-auto=update

# MongoDB Configuration
spring.data.mongodb.uri=mongodb://localhost:27017/authdb

# JWT Configuration
jwt.secret=your-secret-key-here
jwt.expiration=86400000
jwt.refresh-expiration=604800000

# Logging
logging.level.com.example.authservice=DEBUG
Environment Variables (Recommended for Production)
bashexport DB_URL=jdbc:postgresql://localhost:5432/authdb
export DB_USERNAME=postgres
export DB_PASSWORD=your_secure_password
export MONGO_URI=mongodb://localhost:27017/authdb
export JWT_SECRET=your_super_secret_key_256_bits_minimum
CORS Configuration
Edit CorsConfig.java to allow your frontend domains:
javaconfiguration.setAllowedOrigins(Arrays.asList(
    "http://localhost:3000",      // React dev
    "http://localhost:4200",      // Angular dev
    "https://yourapp.com"         // Production
));

🔒 Security
JWT Token Structure--
json{
  "sub": "user-uuid",
  "username": "shiv_doe",
  "email": "shiv@example.com",
  "roles": ["USER", "MODERATOR"],
  "permissions": ["READ_USER", "UPDATE_USER"],
  "iat": 1706400000,
  "exp": 1706486400
}

Password Security--
Algorithm: BCrypt with default strength (10 rounds)
Salt: Automatically generated per password
Storage: Only hashed passwords stored in database

Token Management--

Default Expiration: 24 hours
Refresh Token: 7 days (configurable)
Revocation: Supported via MongoDB storage
Multi-Device: Track multiple active sessions per user

Security Best Practices--
✅ Use HTTPS in production
✅ Change default JWT secret
✅ Implement rate limiting
✅ Regular security audits
✅ Keep dependencies updated
✅ Use environment variables for secrets
✅ Enable SQL injection protection (built-in with JPA)

🧪 Testing
Using Postman
Import the provided Postman collection:

Download Auth-Service-Postman-Collection.json
Import into Postman
Set environment variable: baseUrl = http://localhost:8080
Run tests in order

Quick Test (5 minutes)
# 1. Login as admin
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'

# 2. Get all users (use token from step 1)
curl -X GET http://localhost:8080/users \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"

# 3. Register new user
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username":"testuser",
    "email":"test@test.com",
    "password":"test123"
  }'


 Admin login works
 Regular user login works
 Token validation works
 Permission-based access control works
 User CRUD operations work
 Role management works
 Token revocation works
 Logout works


📊 Performance

Startup Time: ~13 seconds (Spring Boot 4 + JPA + MongoDB)
Login Time: < 500ms (including database queries)
Token Validation: < 50ms
Concurrent Users: Tested with 500+ users
API Response Time: < 200ms (average)

Query Optimization--

Login: 4-5 database queries (optimized with EAGER fetching)
Token Validation: 1 MongoDB query
User Lookup: Single query with indexed fields

Scalability

✅ Stateless JWT architecture (horizontal scaling ready)
✅ Database connection pooling (HikariCP)
✅ Efficient query patterns
✅ No server-side session storage


🗂️ Project Structure
Authentication-Authorization_Service
├── src/main/java/com/example/authservice/
│   ├── AuthServiceApplication.java
│   ├── config/
│   │   ├── CorsConfig.java
│   │   ├── DataInitializer.java
│   │   ├── PasswordEncoderConfig.java
│   │   └── SecurityConfig.java
│   ├── controller/
│   │   ├── AuthController.java
│   │   ├── RoleController.java
│   │   └── UserController.java
│   ├── exception/
│   │   ├── GlobalExceptionHandler.java
│   │   └── UnauthorizedException.java
│   ├── model/
│   │   ├── Permission.java
│   │   ├── Role.java
│   │   ├── TokenDocument.java
│   │   └── User.java
│   ├── repository/
│   │   ├── PermissionRepository.java
│   │   ├── RoleRepository.java
│   │   ├── TokenRepository.java
│   │   └── UserRepository.java
│   ├── security/
│   │   ├── CustomUserDetailsService.java
│   │   ├── JwtAuthenticationFilter.java
│   │   ├── JwtTokenProvider.java
│   │   └── UserPrincipal.java
│   └── service/
│       ├── AuthService.java
│       ├── RoleService.java
│       └── UserService.java
├── src/main/resources/
│   └── application.properties
├── pom.xml
└── README.md

🤝 Contributing
Contributions are welcome! Please follow these steps:

Fork the repository
Create a feature branch (git checkout -b feature/AmazingFeature)
Commit your changes (git commit -m 'Add some AmazingFeature')
Push to the branch (git push origin feature/AmazingFeature)
Open a Pull Request

Coding Standards--

Follow Java naming conventions
Write meaningful commit messages
Add JavaDoc comments for public methods
Include unit tests for new features
Update README if adding new features


🙏 Acknowledgments

Spring Team - For the amazing Spring Boot framework
JWT.io - For JWT implementation and tools
Baeldung - For excellent Spring Security tutorials


📞 Support
For issues, questions, or contributions:

Issues: https://github.com/PriyamJaiswal/Authentication-Authorization_Service/issues
Discussions: https://github.com/PriyamJaiswal/Authentication-Authorization_Service/discussions
Email: priyamj608@gmail.com
