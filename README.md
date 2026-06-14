# DigitalGoldWallet — Backend

Spring Boot 3.3 · Java 17 · PostgreSQL · JWT · Swagger UI

---

## Project Structure

```
src/
├── main/
│   ├── java/com/cg/
│   │   ├── config/          SecurityConfig
│   │   ├── controller/      REST controllers
│   │   ├── dto/             Request & Response DTOs
│   │   ├── entity/          JPA entities
│   │   ├── enums/           Role, PaymentMethod, etc.
│   │   ├── exception/       GlobalExceptionHandler
│   │   ├── repo/            Spring Data repositories
│   │   ├── security/        JWT service & filter
│   │   └── service/         Business logic
│   └── resources/
│       └── application.properties   ← single config file
└── test/
```

---

## Environment Variables

All secrets and environment-specific settings are provided via environment variables.
**Never hard-code credentials in source files.**

| Variable       | Required | Example / Default                                        | Notes                                               |
|----------------|----------|----------------------------------------------------------|-----------------------------------------------------|
| `DB_URL`       | Yes      | `jdbc:postgresql://localhost:5432/digitalgoldwallet`     | Full JDBC URL                                        |
| `DB_USERNAME`  | Yes      | `postgres`                                               |                                                     |
| `DB_PASSWORD`  | Yes      | *(your password)*                                        |                                                     |
| `JWT_SECRET`   | Yes      | *(see below)*                                            | Base64-encoded, min 32 bytes before encoding        |
| `DDL_AUTO`     | No       | `validate` (default)                                     | Use `update` locally if you want Hibernate to sync  |
| `SHOW_SQL`     | No       | `false` (default)                                        | Set `true` locally to log SQL                       |
| `PORT`         | No       | `8080` (default)                                         |                                                     |

### Generating a JWT secret

```bash
openssl rand -hex 32 | base64
```

Copy the output and set it as `JWT_SECRET`.

---

## Local Development Setup

### 1. Create the database

Run the provided `digitalgoldwallet_schema_copy.sql` against your local PostgreSQL:

```bash
psql -U postgres -f digitalgoldwallet_schema_copy.sql
```

### 2. Export environment variables

**Linux / macOS (bash/zsh):**
```bash
export DB_URL=jdbc:postgresql://localhost:5432/digitalgoldwallet
export DB_USERNAME=postgres
export DB_PASSWORD=your_password_here
export JWT_SECRET=<output of openssl rand -hex 32 | base64>
export DDL_AUTO=update
export SHOW_SQL=true
```

**Windows (PowerShell):**
```powershell
$env:DB_URL="jdbc:postgresql://localhost:5432/digitalgoldwallet"
$env:DB_USERNAME="postgres"
$env:DB_PASSWORD="your_password_here"
$env:JWT_SECRET="<your generated secret>"
$env:DDL_AUTO="update"
$env:SHOW_SQL="true"
```

### 3. Run the application

```bash
./mvnw spring-boot:run
```

Or build and run the JAR:
```bash
./mvnw clean package -DskipTests
java -jar target/DigitalGoldWallet-1.0.0.jar
```

---

## Deployment (Production)

Set all environment variables in your deployment platform (Railway, Render, AWS, etc.)  
and leave `DDL_AUTO` at its default `validate` — Hibernate will verify the schema without modifying it.

```bash
# Build production JAR
./mvnw clean package -DskipTests

# Run with environment variables already set on the server
java -jar target/DigitalGoldWallet-1.0.0.jar
```

---

## API Documentation

Once running, Swagger UI is available at:

```
http://localhost:8080/swagger-ui.html
```

---

## Tech Stack

- **Spring Boot 3.3.5** (Web, Data JPA, Security, Validation)
- **PostgreSQL** with Hibernate
- **JWT** via jjwt 0.12.6
- **Swagger / OpenAPI** via springdoc 2.6.0
- **Java 17**
