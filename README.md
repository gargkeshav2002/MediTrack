# 🏥 MediTrack - Hospital Management System

**Modern healthcare management made simple.**
MediTrack is a **Spring Boot + PostgreSQL** powered backend service designed to streamline hospital operations. It supports patient management, appointments, doctor scheduling, authentication, role-based access, and integrations like Twilio for notifications.

This project demonstrates **production-grade engineering** practices: containerization with Docker, CI/CD with GitHub Actions, environment-specific profiles, and scalable architecture.

---

## 🚀 Features  

- **Patient & Doctor Management** → CRUD APIs with validations.  
- **Appointments** → Book, update, cancel, and track appointments.  
- **Authentication & Authorization** → JWT-based security with role-based access.  
- **Twilio Integration** → (configurable) SMS notifications.  
- **PostgreSQL Database** → Structured relational storage.  
- **API Documentation** → Swagger/OpenAPI integration.  
- **Containerized Deployment** → Docker & Docker Compose ready.  
- **CI/CD Pipeline** → Auto-build & push Docker images to Docker Hub.  

---

## 🏗️ Tech Stack  

- **Backend:** Java 17, Spring Boot 3.x  
- **Database:** PostgreSQL 14  
- **ORM:** Spring Data JPA + Hibernate  
- **Security:** Spring Security + JWT  
- **Containerization:** Docker, Docker Compose  
- **CI/CD:** GitHub Actions → Docker Hub  
- **Other:** Lombok, Validation API, Swagger 

---

## ⚡ Quick Start  

### Clone the repository  
```bash
git clone https://github.com/gargkeshav2002/MediTrack.git
cd MediTrack
```

### Run with Docker (Recommended)
The project is pre-configured with a Docker profile.
```bash
docker-compose up --build
```

This will spin up:<br>
  hms-postgres → PostgreSQL database<br>
  hms-app → Spring Boot application

Swagger UI will be available at:
```bash
http://localhost:8080/swagger-ui.html
```

**Note**: Twilio is disabled by default. To enable, set environment variables TWILIO_ACC_SID, TWILIO_AUTH_TOKEN, TWILIO_PHONE and change twilio.enabled=true in application-docker.yml.

### Run locally without Docker
The project is pre-configured with a Docker profile.
```bash
mvn clean spring-boot:run
```
---

## 🔑 Authentication

- JWT-based authentication
- Roles supported: `ADMIN`, `DOCTOR`, `PATIENT`
- Use `/auth/login` endpoint to generate token

---

## 🐳 CI/CD with GitHub Actions

Every push to `master` triggers:

1. Build JAR (skip tests)
2. Build Docker image
3. Push latest image to Docker Hub

Pull the image anywhere using:

```bash
docker pull keshavgarg1/hms-app:latest
```

---

👨‍💻 Author: 
Keshav Garg

