# BugSphere 🐞

BugSphere is a full-stack bug tracking and project management system built using Spring Boot, React, PostgreSQL, and JWT Authentication.

The application allows teams to manage projects, track bugs, assign issues to users, and maintain secure role-based access through JWT authentication.

---

# 🚀 Features

## 🔐 Authentication & Security

* JWT-based Authentication
* Secure Login & Registration
* Role-Based Authorization (Admin/User)
* Protected Backend APIs using Spring Security
* Protected Frontend Routes using React Router
* Automatic JWT token handling using Axios Interceptors
* BCrypt Password Encryption

---

## 🐞 Bug Management

* Create Bugs
* Update Bugs
* Delete Bugs
* View All Bugs
* Assign Bugs to Users
* Bug Priority Levels
* Bug Status Tracking

  * OPEN
  * IN_PROGRESS
  * RESOLVED
  * CLOSED
* Search and Filter Bugs

---

## 📁 Project Management

* Create Projects
* View Projects
* Link Bugs to Projects
* Project-wise Bug Tracking

---

## 👤 User Management

* User Registration
* User Login
* Role Management
* Admin Secret Code Protection
* Assign Bugs to Team Members

---

# 🛠 Tech Stack

## Backend

* Java
* Spring Boot
* Spring Security
* JWT Authentication
* Hibernate / JPA
* PostgreSQL
* Maven

## Frontend

* React
* Vite
* Axios
* React Router DOM
* Tailwind CSS

---

# 📂 Backend Architecture

```text
src/main/java/com/bugsphere/bugsphere
├── config
├── controller
├── dto
├── entity
├── exception
├── repository
├── security
└── service
```

---

# 📂 Frontend Architecture

```text
bugsphere-frontend/src
├── api
├── assets
├── components
├── context
├── pages
└── main.jsx
```

---

# 🔐 Security Features

* JWT Token Validation
* BCrypt Password Encryption
* Authentication Filter
* Custom UserDetailsService
* Secure API Access
* Automatic Logout on Unauthorized Access
* Role-Based Route Protection

---

# ⚙️ Setup Instructions

## 1️⃣ Clone Repository

```bash
git clone https://github.com/PushpaYadav09/bugsphere.git
```

---

## 2️⃣ Backend Setup

### Configure PostgreSQL

Create database:

```sql
CREATE DATABASE bugsphere_db;
```

### Update application.properties

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/bugsphere_db
spring.datasource.username=postgres
spring.datasource.password=yourpassword
```

---

### Run Backend

```bash
mvn spring-boot:run
```

Backend runs on:

```text
http://localhost:8080
```

---

## 3️⃣ Frontend Setup

```bash
cd bugsphere-frontend
npm install
npm run dev
```

Frontend runs on:

```text
http://localhost:5173
```

---

# 📡 API Features

## Authentication APIs

* Register User
* Login User
* JWT Token Generation

## Bug APIs

* Create Bug
* Update Bug
* Delete Bug
* Fetch Bugs
* Assign Bugs
* Change Bug Status

## Project APIs

* Create Project
* Fetch Projects

## User APIs

* Fetch Users
* Role-Based Access

---

# 🧪 API Testing

API testing was performed using Postman with:

* JWT token environments
* Bearer token authorization
* Protected route testing
* CRUD API validation

---

# 💡 Future Improvements

* Email Notifications
* File Attachments
* Bug Comments
* Dashboard Analytics
* Docker Deployment
* CI/CD Pipeline

---

# 👩‍💻 Author

## Pushpa Yadav

MCA Student | Java Backend Developer | Full Stack Learner

GitHub:
[https://github.com/PushpaYadav09](https://github.com/PushpaYadav09)

---

# ⭐ Project Status

✅ Backend Completed
✅ Frontend Integrated
✅ JWT Authentication Working
✅ PostgreSQL Connected
✅ Full Stack Architecture Implemented
✅ Search & Filter Added
✅ Dashboard Statistics Working
