# 5EHIF-DBI-Projekt

Repository for the DBI project in the 5EHIF, focusing on comparing **SQL vs NoSQL** databases and measuring speed differences.

---

## ⚙️ Technology Stack

- **Spring Boot** (Java 25)  
- **PostgreSQL** for SQL operations  
- **MongoDB** for NoSQL operations  
- **Docker** (required dependency for running databases locally)  

---

## Download and Run the Release JAR

You can download a prebuilt version of the application from the **Releases** page. This allows you to run the application without building it from source.

### Steps

1. **Go to the Releases page**  
   [GitHub Releases](https://github.com/TheRealCheeseLord/5EHIF-DBI-Projekt/releases)

2. **Download the JAR**  
   Look for the latest release (e.g., `v1.0.0`) and download the file named similar to: `5EHIF-DBI-Projekt-0.0.1-SNAPSHOT.jar`

4. **Run the JAR**  
Make sure you have Java 25+ installed. Then run the JAR from the command line:
```bash
java -jar 5EHIF-DBI-Projekt-0.0.1-SNAPSHOT.jar
```

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
./mvnw clean package
```

This will generate a **JAR file** in the `target/` directory.

---

## 🏃 Running the Application

1. Run the Spring Boot application:

```bash
java -jar target/5EHIF-DBI-Projekt-0.0.1-SNAPSHOT.jar
```

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

