# SmartShop: Advanced E-Commerce Backend

SmartShop is a robust, high-performance e-commerce backend built with **Java 25** and **Spring Boot 4.0.1**. It provides a comprehensive set of features for managing products, categories, orders, inventory, and users, supporting both RESTful and GraphQL interfaces.

The project emphasizes performance and scalability, featuring custom-built sorting algorithms, an AOP-based monitoring system, and a lightweight in-memory caching mechanism.

## 🚀 Key Features

- **Hybrid API Architecture**: Full support for both **REST** and **GraphQL** endpoints.
- **Advanced Security**:
  - Custom token-based authentication.
  - Role-Based Access Control (RBAC) using custom annotations (`@RequiresRole`).
  - Password hashing using BCrypt.
- **Performance Optimized**:
  - **Custom Sorting**: Implementation of QuickSort and MergeSort for efficient data handling.
  - **In-Memory Caching**: Custom `CacheManager` with hit/miss tracking.
  - **Performance Monitoring**: AOP-based tracking of database query execution times and cache performance.
- **Data Management**:
  - Automated DTO mapping using **MapStruct**.
  - Database versioning/schema management with Hibernate (DDL-auto).
  - Seed data generation for development.
- **Documentation**: Integrated **Swagger/OpenAPI** for REST API exploration and **GraphiQL** for GraphQL queries.
- **Performance Report**: Comprehensive analytical comparison between REST and GraphQL endpoints (see `PERFORMANCE_REPORT.md`).

## 🛠 Tech Stack

- **Core**: Java 25, Spring Boot 4.0.1
- **Data**: Spring Data JPA, PostgreSQL
- **API**: Spring Web (REST), Spring GraphQL
- **Utilities**: Lombok, MapStruct, BCrypt (jBCrypt)
- **Monitoring**: Spring AOP, Spring Boot Actuator
- **Documentation**: Springdoc OpenAPI, GraphiQL

## 🏗 Project Architecture

### 1. Performance Monitoring (AOP)

The project uses Aspect-Oriented Programming to monitor performance without polluting business logic. The `PerformanceMonitoringAspect` intercepts repository calls to record execution times and tracks cache statistics.

- **Metrics Endpoint**: `/api/performance/db-metrics` & `/api/performance/cache-metrics` (Admin Only).

### 2. Custom Sorting Service

Instead of relying solely on database sorting, the project includes a `SortingService` that implements:

- **QuickSort**: Optimized for in-place sorting.
- **MergeSort**: Stable sorting algorithm.
- Supports sorting by Price, Name, Quantity, and Date.

### 3. Security Model

A custom security layer is implemented via `AuthInterceptor` and `RoleInterceptor`.

- **Authentication**: Uses a Bearer token format (`Bearer username-id`).
- **Authorization**: Custom `@RequiresRole` annotation allows fine-grained access control at the controller level.

## 📂 Project Structure

```text
src/main/java/com/amalitech/smartshop/
├── aspects/          # AOP for Logging and Performance Monitoring
├── cache/            # Custom In-memory Caching System
├── config/           # Security Interceptors and App Config
├── controllers/      # REST API Controllers
├── dtos/             # Data Transfer Objects
├── entities/         # Domain Entities (User, Product, Category, Order, Inventory)
├── enums/            # Shared Enumerations (Roles, Status)
├── exceptions/       # Global Exception Handling
├── graphql/          # GraphQL Resolvers and Controllers
├── interfaces/       # Repository and Service Interfaces
├── mappers/          # MapStruct Interface Definitions
├── repositories/     # JDBC Repository Implementations
├── services/         # Business Logic Layer (Service Implementations)
└── utils/            # Sorting Utilities (QuickSort, MergeSort)
```

## 🚦 Getting Started

### Prerequisites

- JDK 25
- Maven 3.9+
- PostgreSQL

### Environment Configuration

Create a `.env` file or set environment variables:

```properties
DATABASE_URL=jdbc:postgresql://localhost:5432/commerce_db
DATABASE_USERNAME=your_username
DATABASE_PASSWORD=your_password
```

### Installation & Run

1. Clone the repository.
2. Build the project:
   ```bash
   mvn clean install
   ```
3. Run the application:
   ```bash
   mvn spring-boot:run
   ```

## 📖 API Documentation

### REST API

- **Swagger UI**: `http://localhost:8080/swagger-ui/index.html`
- **Core Endpoints**:
  - `POST /api/users/register`: Register a new user.
  - `POST /api/users/login`: Login and receive a token.
  - `GET /api/products`: List products (supports custom sorting).
  - `GET /api/performance/db-metrics`: View DB performance stats (Admin).

### GraphQL

- **GraphiQL**: `http://localhost:8080/graphiql`
- **Example Query**:
  ```graphql
  query {
    allProducts {
      name
      price
      categoryName
    }
  }
  ```

## 🔐 Security Roles

| Role         | Permissions                                           |
| :----------- | :---------------------------------------------------- |
| **ADMIN**    | Full access to all resources and performance metrics. |
| **SELLER**   | Manage products and inventory.                        |
| **CUSTOMER** | Browse products and manage personal orders.           |

---

## mvn spring-boot:run -Dspring-boot.run.profiles=dev

cd backend/SmartShop && mvn spring-boot:run -Dspring-boot.run.profiles=dev

_Note: This documentation focuses on the Java backend. For frontend documentation, please refer to the `/frontend` directory._
