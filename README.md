# BiblioNode - Library API

[![Tests](https://github.com/mgrablo/BiblioNode/actions/workflows/test.yml/badge.svg)](https://github.com/mgrablo/BiblioNode/actions/workflows/test.yml)
![Coverage](.github/badges/jacoco.svg)
![Branches](.github/badges/branches.svg)
![Java](https://img.shields.io/badge/Java-21-blue)
![Spring](https://img.shields.io/badge/Spring_Boot-4.0.2-green)
![License](https://img.shields.io/badge/License-Apache_2.0-orange)

A library management system built with a layered architecture, focusing on data integrity, input validation, and comprehensive testing.

<!-- TOC -->
* [BiblioNode - Library API](#biblionode---library-api)
  * [Tech Stack](#tech-stack)
  * [Database Schema](#database-schema)
  * [Key Features](#key-features)
  * [Testing](#testing)
  * [Setup Instructions](#setup-instructions)
    * [Option 1: Quick Run (Docker only)](#option-1-quick-run-docker-only)
    * [Option 2: Development Mode (Hybrid)](#option-2-development-mode-hybrid)
    * [Cleaning up](#cleaning-up)
  * [Roadmap](#roadmap)
<!-- TOC -->

## Tech Stack
- **Language**: Java 21
- **Framework**: Spring Boot 4.0.2, Spring Security
- **Database**: PostgreSQL
- **Mapping**: MapStruct (Entity & DTO)
- **Documentation**: Swagger UI
- **Testing**: JUnit 5, Mockito, MockMvc

## Database Schema
```mermaid
erDiagram
   AUTHOR ||--o{ BOOK : "writes"
   READER ||--o{ LOAN : "borrows"
   BOOK ||--o{ LOAN : "is subject of"
   USER ||--|| READER : "is linked to"
   USER ||--o{ USER_ROLE : "has"
   ROLE ||--o{ USER_ROLE : "assigned to"

   AUTHOR {
      Long id PK
      String name
      String biography
   }
   BOOK {
      Long id PK
      String title
      String isbn
      boolean available
      Long authorId FK
   }
   USER {
      Long id PK
      String email
      String password
   }
   ROLE {
      Long id PK
      String name
   }
   USER_ROLE {
       Long userId FK
       Long roleId FK
   }
   READER {
      Long id PK
      String fullName
      Long userId FK
   }
   LOAN {
      Long id PK
      Long bookId FK
      Long readerId FK
      LocalDateTime loanDate
      LocalDateTime dueDate
      LocalDateTime returnDate
   }
```

## Key Features
- **Authentication & Authorization**: REST API secured with JSON Web Tokens (JWT). Role-based access control (RBAC) separating regular users and administrators.
- **Advanced Loan System**: Full lifecycle of book borrowing and returns with automated availability management and overdue tracking.
- **JPA Auditing**: Automated tracking of creation and modification timestamps for every resource using `@CreatedDate` and `@LastModifiedDate`.
- **Global Exception Handling**: Centralized error management using `@RestControllerAdvice` to ensure consistent JSON error responses across the API.
- **Validation**: Input data is strictly validated using Hibernate Validator annotations (e.g., `@NotBlank`, `@Size`) to maintain data quality.
- **Clean Architecture**: Strict separation between database entities and API response models using the DTO (Data Transfer Object) pattern.

## Testing
The project maintains a high standard of quality through different testing layers:
- **Unit Tests**: Focused on business logic within the Service layer, utilizing Mockito for dependency isolation.
- **Web Layer Tests**: Utilizing MockMvc to verify REST endpoints, HTTP status codes, JSON serialization, and validation logic without starting the full server.
- **Persistence Tests**: `@DataJpaTest` used to verify complex JPQL queries and relationship mapping.

## Setup Instructions

> [!NOTE]
> The application uses `SPRING_PROFILES_ACTIVE` environment variable to determine the active profile.
> Default is `prod`. To verify setup with sample data, use `dev` profile.

### Option 1: Quick Run (Docker only)
Best for quick preview. No Java/Gradle installation required.

1. **Clone the repository**:
   ```bash
   git clone https://github.com/mgrablo/BiblioNode.git
   cd BiblioNode
   ```
2. **Setup database & Run App**:
   You can specify the profile inline:
   ```bash
   SPRING_PROFILES_ACTIVE=dev docker-compose up -d
   ```
   Or just run with default `prod` profile:
   ```bash
   docker-compose up -d
   ```

   **Default Admin Credentials:**
   - Email: `root@biblionode.com`
   - Password: `root1234`

   *(You can change these via `BD_INITIAL_ADMIN_EMAIL` and `BD_INITIAL_ADMIN_PASSWORD` environment variables or update them later via API)*

   > [!TIP]
   > You can also create a `.env` file in the root directory to set these variables.
   > Check `.env.example` for reference.

3. **Access API documentation**:

    Once the server is running, navigate to:
    `http://localhost:8080/swagger-ui/index.html`

### Option 2: Development Mode (Hybrid)
Best for making changes to the code with fast feedback

1. **Start only database**:
    ```bash
    docker-compose up -d db
    ```
2. **Start the app locally**:
   ```bash
   # Linux/Mac
   SPRING_PROFILES_ACTIVE=dev ./gradlew bootRun
   
   # Windows (PowerShell)
   $env:SPRING_PROFILES_ACTIVE="dev"; ./gradlew bootRun
   ```
3. **Access API documentation**:

    Once the server is running, navigate to:
   `http://localhost:8080/swagger-ui/index.html`

### Cleaning up
If you want to easily reset the database use:
```bash
docker-compose down -v
```

## Roadmap
1. [x] Basic CRUD for Books and Authors.
2. [x] Database Auditing & Pagination.
3. [x] Database Migrations with Liquibase.
4. [x] Loan System Implementation.
   - [x] Automatic availability management.
   - [x] Overdue tracking.
   - [ ] Personal loan history for readers (`api/me/`).
5. [x] JWT Authentication & User Roles.
