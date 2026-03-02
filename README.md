# Decentralised Peer Code Review Platform - Backend

Spring Boot 3 REST API for the Decentralised Peer Code Review Platform.

## Project Overview

This backend provides a complete REST API for a platform where developers submit code snippets for anonymous peer review. Reviewers are assigned randomly, rate code quality across multiple dimensions (readability, efficiency, security, style), and earn reputation scores. The system includes ML-based review quality prediction using Apache Commons Math SimpleRegression.

## Tech Stack

- Java 17 with Spring Boot 3.2
- Spring Security 6 with JWT authentication
- Spring Data JPA with PostgreSQL
- Apache Commons Math 3 for ML predictions
- SpringDoc OpenAPI for Swagger documentation
- Lombok for boilerplate reduction
- Maven for build and dependency management

## Entities

- User: Registration, login, profile management
- CodeSubmission: Code snippets with language, title, description, and status lifecycle
- Review: Peer reviews with multi-dimensional ratings and comments
- ReviewDimension: Configurable review criteria (readability, efficiency, security, style)
- ReviewRating: Individual dimension scores within a review
- ReputationScore: Accumulated reviewer reputation based on review quality

## API Endpoints

### Authentication
- POST /api/auth/register - Register a new user
- POST /api/auth/login - Authenticate and receive JWT token

### Code Submissions
- GET /api/submissions - List all submissions
- GET /api/submissions/{id} - Get submission by ID
- POST /api/submissions - Create new submission
- PUT /api/submissions/{id} - Update submission
- DELETE /api/submissions/{id} - Delete submission
- POST /api/submissions/{id}/assign-reviewer - Assign random reviewer

### Reviews
- GET /api/reviews - List all reviews
- GET /api/reviews/{id} - Get review by ID
- POST /api/reviews - Create new review with dimension ratings
- PUT /api/reviews/{id} - Update review
- DELETE /api/reviews/{id} - Delete review

### Review Dimensions
- GET /api/dimensions - List all dimensions
- POST /api/dimensions - Create dimension
- PUT /api/dimensions/{id} - Update dimension
- DELETE /api/dimensions/{id} - Delete dimension

### Reputation and Leaderboard
- GET /api/reputation/leaderboard - Get reviewer leaderboard
- GET /api/reputation/user/{userId} - Get user reputation

### Dashboard
- GET /api/dashboard - Get platform statistics

### Forecast (ML Predictions)
- GET /api/forecast/predict?codeLength=500 - Predict review quality score
- GET /api/forecast/trend?periodsAhead=5 - Predict score trend

### Health Check
- GET /api/health - Application health status

## Running Locally

### Prerequisites
- Java 17
- Maven 3.8+
- PostgreSQL 15

### Setup
1. Create a PostgreSQL database named codereview_db
2. Update application.properties with your database credentials
3. Run the application:

```bash
mvn clean install
mvn spring-boot:run
```

4. Access Swagger UI at http://localhost:8080/swagger-ui.html

## Static Analysis

The following static analysis tools are configured in pom.xml and run during mvn verify:

- SpotBugs: Static bug detection
- PMD: Code style and complexity analysis
- OWASP Dependency-Check: CVE vulnerability scanning
- JaCoCo: Code coverage (minimum 60% threshold)
- SonarQube: Comprehensive code quality analysis

Run all analysis tools:
```bash
mvn verify
```

## Testing

Run tests with coverage:
```bash
mvn test
```

Tests use H2 in-memory database (configured in application-test.properties).

## CI/CD Pipeline

GitHub Actions workflow (.github/workflows/ci-cd.yml) includes:

CI stages: Build, test, SpotBugs, PMD, OWASP Dependency-Check, JaCoCo coverage
CD stages: Deploy to AWS EC2 via SSH, smoke test health endpoint

## Infrastructure

Terraform configuration (terraform/) provisions:
- AWS VPC with public and private subnets
- EC2 t2.micro instance with Java 17
- RDS PostgreSQL db.t3.micro
- S3 bucket with static website hosting

## Student Information

- Student: Rakshan
- Student ID: 25180754
- Module: Cloud DevOpsSec (H9CDOS)
- Project: Decentralised Peer Code Review Platform
