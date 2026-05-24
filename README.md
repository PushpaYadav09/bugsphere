# BugSphere 🐛

A full-stack bug tracking system built with Spring Boot and React.
Manage bugs, assign developers, track progress — role-based access control with JWT authentication.

<img width="1615" height="850" alt="dashboard" src="https://github.com/user-attachments/assets/3356577a-5c08-4830-894f-143f26280446" />

BugSphere Dashboard

---

## What is BugSphere?

BugSphere is a mini Jira clone where:
- **Admins** can create projects, assign bugs, manage users, delete records
- **Users** can report bugs, update status, view assigned work
- Every action is secured — wrong role = 403 Forbidden
- Login gives you a JWT token — no sessions, fully stateless

---

## Tech Stack

### Backend
| Technology | Purpose |
|---|---|
| Java 17 | Programming language |
| Spring Boot 3.3 | Backend framework |
| Spring Security | Authentication and authorization |
| JWT (jjwt 0.11.5) | Stateless token-based auth |
| Spring Data JPA | Database access layer |
| Hibernate | ORM — maps Java classes to DB tables |
| PostgreSQL | Relational database |
| Lombok | Reduces boilerplate code |
| Maven | Dependency management |

### Frontend
| Technology | Purpose |
|---|---|
| React 18 | UI library |
| Vite | Build tool and dev server |
| Tailwind CSS 3 | Utility-first styling |
| Axios | HTTP client with interceptors |
| React Router v6 | Client-side routing |
| Context API | Global auth state management |

---

## Features

### Authentication
- Register as User or Admin
- Admin registration requires a secret code
- JWT token issued on login — expires in 24 hours
- Token auto-attached to every API request via Axios interceptor
- Expired token → automatic logout and redirect to login

### Bug Management
- Create bugs with title, description, priority, project
- Priority levels: LOW / MEDIUM / HIGH / CRITICAL
- Status lifecycle: OPEN → IN_PROGRESS → RESOLVED → CLOSED
- Assign bugs to specific developers
- Search bugs by keyword
- Filter bugs by status
- View bugs assigned to you on the dashboard

### Project Management
- Create and manage projects (Admin only)
- Each project contains its own bugs
- Delete project cascades — all its bugs are deleted too
- Bug count displayed per project

### Dashboard
- Real-time stats: total, open, in-progress, resolved, closed
- Bugs assigned to the logged-in user
- Live data from the backend on every page load

### Role-Based Access Control
| Action | User | Admin |
|---|---|---|
| View bugs and projects | ✅ | ✅ |
| Create bugs | ✅ | ✅ |
| Update bug status | ✅ | ✅ |
| Create/edit/delete projects | ❌ | ✅ |
| Assign bugs to users | ❌ | ✅ |
| Delete bugs | ❌ | ✅ |
| View all users | ❌ | ✅ |

---

## Project Structure

```
bugsphere/
├── src/main/java/com/bugsphere/
│   ├── config/
│   │   └── SecurityConfig.java
│   ├── controller/
│   │   ├── AuthController.java
│   │   ├── BugController.java
│   │   ├── ProjectController.java
│   │   └── UserController.java
│   ├── dto/
│   ├── entity/
│   ├── exception/
│   ├── repository/
│   ├── security/
│   └── service/
└── bugsphere-frontend/
    └── src/
        ├── api/
        ├── components/
        ├── context/
        └── pages/
```
## Getting Started

### Prerequisites
- Java 17+
- Maven 3.8+
- PostgreSQL 14+
- Node.js 18+
- Git

### 1. Clone the repository
```bash
git clone https://github.com/yourusername/bugsphere.git
cd bugsphere
```

### 2. Create the database
Open pgAdmin or psql and run:
```sql
CREATE DATABASE bugsphere_db;
```

### 3. Configure the backend
Open `src/main/resources/application.properties` and set:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/bugsphere_db
spring.datasource.username=your_postgres_username
spring.datasource.password=your_postgres_password
admin.secret.code=your_chosen_admin_code
```

### 4. Run the backend
```bash
# From the bugsphere root folder
./mvnw spring-boot:run
```
Backend starts at `http://localhost:8080`

Tables are auto-created by Hibernate on first run.

### 5. Run the frontend
```bash
cd bugsphere-frontend
npm install
npm run dev
```
Frontend starts at `http://localhost:5173`

---

## API Reference

### Auth (Public — no token needed)

| Method | Endpoint | Body | Description |
|---|---|---|---|
| POST | `/api/auth/register` | `{username, email, password, role, adminCode}` | Register new user |
| POST | `/api/auth/login` | `{username, password}` | Login, returns JWT token |

### Projects (Authenticated)

| Method | Endpoint | Role | Description |
|---|---|---|---|
| GET | `/api/projects` | User | Get all projects |
| GET | `/api/projects/{id}` | User | Get one project |
| GET | `/api/projects/search?name=x` | User | Search by name |
| POST | `/api/projects` | Admin | Create project |
| PUT | `/api/projects/{id}` | Admin | Update project |
| DELETE | `/api/projects/{id}` | Admin | Delete project + all its bugs |

### Bugs (Authenticated)

| Method | Endpoint | Role | Description |
|---|---|---|---|
| GET | `/api/bugs` | User | Get all bugs |
| GET | `/api/bugs/{id}` | User | Get one bug |
| GET | `/api/bugs/my` | User | Get bugs assigned to me |
| GET | `/api/bugs/stats` | User | Get status counts |
| GET | `/api/bugs/project/{id}` | User | Get bugs by project |
| POST | `/api/bugs` | User | Create bug |
| PUT | `/api/bugs/{id}` | User | Update bug |
| PATCH | `/api/bugs/{id}/status?status=X` | User | Update status only |
| PATCH | `/api/bugs/{id}/assign?userId=X` | Admin | Assign to user |
| DELETE | `/api/bugs/{id}` | Admin | Delete bug |

### Users (Authenticated)

| Method | Endpoint | Role | Description |
|---|---|---|---|
| GET | `/api/users` | Admin | Get all users |
| GET | `/api/users/{id}` | User | Get one user |
| PATCH | `/api/users/{id}/make-admin` | Admin | Promote to admin |
| DELETE | `/api/users/{id}` | Admin | Delete user |

### How to authenticate
Add this header to every request after login:
---

## How JWT Authentication Works
1. POST /auth/login → server verifies password against BCrypt hash
2. Server generates JWT: header.payload.signature
3. Payload contains: username, issued time, expiry time
4. Token signed with secret key using HMAC-SHA256
5. Frontend stores token in localStorage
6. Every request: Axios interceptor adds "Authorization: Bearer <token>"
7. JwtAuthenticationFilter reads token, verifies signature, sets user in SecurityContext
8. @PreAuthorize checks role from SecurityContext before controller runs
---

## Security Design Decisions

**Why JWT over sessions?**
Stateless — server stores nothing. Every request is self-contained. Scales horizontally without shared session storage.

**Why BCrypt over MD5/SHA?**
BCrypt generates a random salt per hash (defeats rainbow tables) and has a configurable cost factor that makes brute force computationally infeasible.

**Why CSRF is disabled?**
We use JWT in the Authorization header, not cookies. Malicious sites can't set custom headers cross-origin, so CSRF attacks are not possible in this setup.

**Why DTOs instead of returning entities?**
Prevents accidental exposure of password hashes and Spring Security internals. Decouples the API contract from the database schema.

**Admin code protection?**
Admin registration requires a secret code stored in application.properties. In production this would be an environment variable, never in the codebase.

---

## Known Limitations

- JWT doesn't support refresh tokens — user must re-login after 24 hours
- No unit tests yet (planned)
- No bug detail page — navigate to `/bugs/{id}/edit` to view/edit a bug
- No email notifications on bug assignment
- Admin code is in application.properties — should be an env variable in production

---

