# 🔍 Job Finder Application — Backend REST API

A RESTful backend application for a job-hunting platform, built with **Spring Boot 3** and **Java 17**. The system supports job posting management, resume submission, company management, role-based access control (RBAC), JWT authentication, and automated email notifications.

---

## 📋 Table of Contents

- [Features](#features)
- [Tech Stack](#tech-stack)
- [System Architecture](#system-architecture)
- [Database Design](#database-design)
- [API Endpoints](#api-endpoints)
- [Getting Started](#getting-started)
- [Configuration](#configuration)
- [Security](#security)
- [Project Structure](#project-structure)

---

## ✨ Features

- **Authentication & Authorization** — JWT-based login with Access Token + Refresh Token (stored in HTTP-only cookies)
- **User Registration & Login** — Email/password authentication with BCrypt password hashing
- **Company Management** — CRUD operations for companies
- **Job Management** — Post, update, filter, and paginate job listings; each job is linked to a company and a set of required skills
- **Resume Submission** — Users can upload and submit CVs to specific jobs; resume status is trackable (`PENDING`, `REVIEWING`, `APPROVED`, `REJECTED`)
- **Skill Management** — Create and manage a skill catalogue used to tag jobs
- **Role & Permission Management** — Fine-grained RBAC with dynamic permission interceptors; each role carries a set of API-level permissions
- **File Upload** — Upload CV files and company logos (up to 50 MB); files are stored on the server filesystem
- **Email Notifications** — Weekly automated job-alert emails sent to subscribers using Spring Mail + Thymeleaf templates
- **API Documentation** — Interactive Swagger UI via SpringDoc OpenAPI
- **Pagination & Filtering** — All list endpoints support pagination and dynamic query filtering via `springfilter`

---

## 🛠 Tech Stack

| Category | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.2.4 |
| Build Tool | Gradle (Kotlin DSL) |
| Database | MySQL 8 |
| ORM | Spring Data JPA / Hibernate |
| Security | Spring Security 6, OAuth2 Resource Server |
| Authentication | JWT (Nimbus JOSE) |
| Email | Spring Mail (SMTP / Gmail) |
| Template Engine | Thymeleaf |
| API Docs | SpringDoc OpenAPI 2.5 (Swagger UI) |
| Filtering | TurkraftSpringFilter 3.1.7 |
| Utilities | Lombok |

---

## 🏗 System Architecture

The application follows a standard **layered architecture**:

```
Client (HTTP)
     │
     ▼
[Controller Layer]      ← REST endpoints, request/response mapping
     │
     ▼
[Service Layer]         ← Business logic, DTO conversion
     │
     ▼
[Repository Layer]      ← Spring Data JPA interfaces
     │
     ▼
[Database — MySQL]
```

**Security flow:**

```
Request → JWT Filter → Permission Interceptor → Controller → Service → DB
```

All requests (except whitelisted public endpoints) require a valid Bearer Access Token. After token verification, the `PermissionInterceptor` checks whether the user's role has permission to access the specific API method and path.

---

## 🗄 Database Design

The system has **8 core entities**:

| Entity | Table | Description |
|---|---|---|
| `User` | `users` | Application user, belongs to a Company and Role |
| `Company` | `companies` | Employer company |
| `Job` | `jobs` | Job posting, belongs to Company, has many Skills |
| `Skill` | `skills` | Skill tags used on jobs |
| `Resume` | `resumes` | CV submission by a User for a specific Job |
| `Role` | `roles` | User role (e.g. `ADMIN`, `USER`, `HR`) |
| `Permission` | `permissions` | API-level permission (method + path + module) |
| `Subscriber` | `subscribers` | Email subscribers who receive job alert emails |

**Key relationships:**
- `User` → `Company` (Many-to-One)
- `User` → `Role` (Many-to-One)
- `Role` ↔ `Permission` (Many-to-Many)
- `Job` → `Company` (Many-to-One)
- `Job` ↔ `Skill` (Many-to-Many, via `job_skill` table)
- `Resume` → `User` (Many-to-One)
- `Resume` → `Job` (Many-to-One)
- `Subscriber` ↔ `Skill` (Many-to-Many)

---

## 📡 API Endpoints

All endpoints are prefixed with `/api/v1`.

### Authentication (`/auth`)
| Method | Path | Description | Auth Required |
|---|---|---|---|
| POST | `/auth/login` | Login, returns access token + sets refresh token cookie | ❌ |
| POST | `/auth/register` | Register new user | ❌ |
| GET | `/auth/account` | Get current logged-in user info | ✅ |
| GET | `/auth/refresh` | Refresh access token using cookie | ❌ |
| POST | `/auth/logout` | Logout, clears refresh token | ✅ |

### Users (`/users`)
| Method | Path | Description |
|---|---|---|
| GET | `/users` | Get paginated list of users |
| GET | `/users/{id}` | Get user by ID |
| POST | `/users` | Create new user |
| PUT | `/users` | Update user |
| DELETE | `/users/{id}` | Delete user |

### Companies (`/companies`) — public read
| Method | Path | Description |
|---|---|---|
| GET | `/companies` | Get paginated list of companies |
| GET | `/companies/{id}` | Get company by ID |
| POST | `/companies` | Create company |
| PUT | `/companies` | Update company |
| DELETE | `/companies/{id}` | Delete company |

### Jobs (`/jobs`) — public read
| Method | Path | Description |
|---|---|---|
| GET | `/jobs` | Get paginated, filterable job listings |
| GET | `/jobs/{id}` | Get job detail |
| POST | `/jobs` | Create job |
| PUT | `/jobs` | Update job |
| DELETE | `/jobs/{id}` | Delete job |

### Resumes (`/resumes`)
| Method | Path | Description |
|---|---|---|
| GET | `/resumes` | Get all resumes (admin) |
| GET | `/resumes/{id}` | Get resume by ID |
| GET | `/resumes/by-user` | Get all resumes of current user |
| POST | `/resumes` | Submit a resume |
| PUT | `/resumes` | Update resume status |
| DELETE | `/resumes/{id}` | Delete resume |

### Skills (`/skills`)
| Method | Path | Description |
|---|---|---|
| GET | `/skills` | List all skills |
| POST | `/skills` | Create skill |
| PUT | `/skills` | Update skill |
| DELETE | `/skills/{id}` | Delete skill |

### Roles & Permissions (`/roles`, `/permissions`)
| Method | Path | Description |
|---|---|---|
| GET/POST/PUT/DELETE | `/roles` | Manage roles |
| GET/POST/PUT/DELETE | `/permissions` | Manage permissions |

### File Upload (`/files`)
| Method | Path | Description |
|---|---|---|
| POST | `/files` | Upload a file (CV, logo, etc.) |

### Subscribers (`/subscribers`)
| Method | Path | Description |
|---|---|---|
| POST | `/subscribers` | Subscribe to job alerts |
| PUT | `/subscribers` | Update subscriber skills |
| POST | `/subscribers/skills` | Send email job alerts to all subscribers |

---

## 🚀 Getting Started

### Prerequisites

- Java 17+
- MySQL 8+
- Gradle 8.7+ (or use included `./gradlew` wrapper)

### 1. Clone the repository

```bash
git clone https://github.com/your-username/job-finder-application.git
cd job-finder-application
```

### 2. Create the database

```sql
CREATE DATABASE jobhunter CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 3. Configure the application

Edit `src/main/resources/application.properties` (see [Configuration](#configuration) section below).

### 4. Run the application

```bash
./gradlew bootRun
```

The server starts on `http://localhost:8080` by default.

### 5. Access Swagger UI

```
http://localhost:8080/swagger-ui/index.html
```

---

## ⚙️ Configuration

Key settings in `application.properties`:

```properties
# Database
spring.datasource.url=jdbc:mysql://localhost:3306/jobhunter
spring.datasource.username=root
spring.datasource.password=YOUR_DB_PASSWORD

# JWT
hoidanit.jwt.base64-secret=YOUR_BASE64_SECRET_KEY
hoidanit.jwt.access-token-validity-in-seconds=86400
hoidanit.jwt.refresh-token-validity-in-seconds=604800

# File Upload (set to an absolute path on your machine)
hoidanit.upload-file.base-uri=file:///path/to/upload/folder/

# Email (Gmail SMTP)
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password
```

> **Note:** For Gmail, use an [App Password](https://support.google.com/accounts/answer/185833) instead of your account password.

---

## 🔐 Security

- **Stateless JWT** — No server-side session. Access token is short-lived and sent in the `Authorization: Bearer` header.
- **Refresh Token** — Long-lived, stored as an HTTP-only secure cookie; rotated on every refresh.
- **BCrypt** — All passwords are hashed using BCrypt before storage.
- **Permission Interceptor** — Every protected API call is verified against the user's role permissions at the route level (`method` + `apiPath`).
- **CORS** — Configured to allow requests from the frontend origin.
- **CSRF** — Disabled (stateless API, JWT-based).

**Public (no auth required) endpoints:**
- `POST /api/v1/auth/login`
- `POST /api/v1/auth/register`
- `GET /api/v1/auth/refresh`
- `GET /api/v1/companies/**`
- `GET /api/v1/jobs/**`
- Swagger UI paths

---

## 📁 Project Structure

```
src/main/java/vn/hoidanit/jobhunter/
├── config/                  # Spring configurations (Security, CORS, OpenAPI, Interceptors)
├── controller/              # REST controllers (one per resource)
│   ├── AuthController.java
│   ├── UserController.java
│   ├── CompanyController.java
│   ├── JobController.java
│   ├── ResumeController.java
│   ├── SkillController.java
│   ├── RoleController.java
│   ├── PermissionController.java
│   ├── SubscriberController.java
│   └── FileUploadController.java
├── domain/                  # JPA entities
│   ├── User.java
│   ├── Company.java
│   ├── Job.java
│   ├── Skill.java
│   ├── Resume.java
│   ├── Role.java
│   ├── Permission.java
│   ├── Subscriber.java
│   ├── request/             # Request DTOs
│   └── response/            # Response DTOs (per resource)
├── repository/              # Spring Data JPA repositories
├── service/                 # Business logic interfaces
│   └── impl/                # Service implementations
├── utils/
│   ├── annotations/         # Custom annotation: @ApiMessage
│   ├── constant/            # Enums: GenderEnum, LevelEnum, ResumeStateEnum
│   └── error/               # Custom exceptions & GlobalException handler
└── JobhunterApplication.java

src/main/resources/
├── application.properties
└── templates/               # Thymeleaf email templates
    ├── job.html             # Job alert email template
    └── register.html        # Registration email template
```

---

## 📧 Email Notifications

The application uses **Spring Mail** with **Thymeleaf** HTML templates to send job alert emails. Subscribers can register with a list of preferred skills. When the notification endpoint is triggered, it fetches active jobs matching each subscriber's skills and sends a formatted email digest.

---

## 📄 License

This project is for educational/portfolio purposes.
