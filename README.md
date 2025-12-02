# 5EHIF-DBI-Projekt

Repository for the DBI project in the 5EHIF, focusing on comparing **SQL vs NoSQL** databases and measuring speed differences.

---

## ⚙️ Technology Stack

- **Spring Boot** (Java 25)  
- **PostgreSQL** for SQL operations  
- **MongoDB** for NoSQL operations  
- **Docker** (required dependency for running databases locally)  

---

## 🛠️ Setup & Build

### 1. Clone the repository

```bash
git clone https://github.com/TheRealCheeseLord/5EHIF-DBI-Projekt.git
cd 5EHIF-DBI-Projekt
```

### 2. Build the application
Make sure you have Java 25 and Maven installed.

```bash
./mvnw clean verify
```

---

## 🏃 Running the Application

1. Run the Spring Boot (Test) application

2. Access the main dashboard and API docs in your browser:
- **Dashboard:** http://localhost:8080/  
- **Swagger UI:** http://localhost:8080/swagger  
- **OpenAPI JSON:** http://localhost:8080/openapi

---

## 📡 Available Endpoints

| Endpoint | Description |
|----------|-------------|
| `/` | Main dashboard |
| `/swagger` | Swagger UI for interactive API testing |
| `/openapi` | OpenAPI JSON specification |

> Additional endpoints for benchmarking (writes, reads, updates, deletes) are available under `/api/benchmarks/*`.

---

## 📝 Notes

- The project is designed to measure **raw database performance**, with optimizations to bypass entity loading and caching for more accurate timing.  
- For reproducible benchmarks: 
  - Make sure databases are started fresh if cold-cache timing is required.  

