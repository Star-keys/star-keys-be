# Star Keys Backend

A modern backend API server for the Star Keys project, built with Spring Boot 3.5 and Java 21. This application leverages the latest Java features and follows Hexagonal Architecture (Ports and Adapters) for scalability and maintainability.

## Table of Contents

- [About The Project](#about-the-project)
- [Tech Stack](#tech-stack)
- [System Requirements](#system-requirements)
- [Getting Started](#getting-started)
- [Development Guide](#development-guide)
- [Project Structure](#project-structure)
- [API Documentation](#api-documentation)
- [Deployment](#deployment)
- [Contributing](#contributing)

## About The Project

Star Keys is a modern backend system that utilizes multiple data sources for optimal performance and flexibility. Key features include:

- **Multi-Database Architecture**: Each database is chosen for its specific strengths
  - PostgreSQL: Relational data and ACID transactions
  - MongoDB: Flexible schema for document storage
  - Elasticsearch: High-performance search and analytics

- **Hexagonal Architecture**: Clear separation between business logic and external dependencies, making the system highly testable and maintainable

- **Type-Safe Queries**: QueryDSL enables compile-time query validation, reducing runtime errors

## Tech Stack

### Core Framework
- **Spring Boot 3.5.7**: Leveraging the latest Spring ecosystem features
- **Java 21**: Modern Java features including Virtual Threads and Pattern Matching
- **Gradle (Kotlin DSL)**: Type-safe build configuration

### Database & Persistence
- **PostgreSQL**: Primary relational database for core business data
- **MongoDB**: Document database for flexible schema requirements
- **Elasticsearch**: Full-text search and log analytics
- **Spring Data JPA**: Repository pattern implementation with JPA
- **QueryDSL**: Type-safe dynamic query construction
- **Hibernate**: JPA implementation

### Development Tools
- **Lombok**: Reduces boilerplate code and improves productivity
- **Spring Boot DevTools**: Auto-reload and LiveReload during development
- **Docker Compose**: Simplified local database management

## System Requirements

### Prerequisites

| Tool | Version | Check Installation | Download |
|------|---------|-------------------|----------|
| Java | 21 or higher | `java -version` | [Oracle JDK](https://www.oracle.com/java/technologies/downloads/#java21) / [OpenJDK](https://adoptium.net/) |
| Docker | Latest | `docker --version` | [Docker Desktop](https://www.docker.com/products/docker-desktop) |
| Docker Compose | Latest | `docker compose version` | Included with Docker Desktop |

### Recommended
- **IDE**: IntelliJ IDEA (Ultimate preferred, Community also works)
- **Memory**: Minimum 8GB RAM (16GB recommended)
- **Disk Space**: At least 10GB free space

## Getting Started

A step-by-step guide for first-time setup.

### 1. Clone the Repository

```bash
git clone <repository-url>
cd star-keys-be
```

### 2. Setup Git Hooks

The project enforces Conventional Commits format. Install Git hooks before making any commits:

```bash
./setup-hooks.sh
```

**Expected output:**
```
Git hooks have been installed successfully!
```

> **Note**: Skipping this step will cause commits to be rejected if they don't follow the conventional format.

### 3. Verify Development Environment

Ensure all required tools are properly installed:

```bash
# Check Java version (must be 21 or higher)
java -version

# Check Docker
docker --version

# Check Docker Compose
docker compose version
```

### 4. Start Databases

Use Docker Compose to start all required databases at once:

```bash
docker compose up -d
```

**Running Services:**
- **PostgreSQL**: `localhost:5432`
  - Database: `starkeys`
  - Username: `starkeys`
  - Password: `secret`

- **MongoDB**: `localhost:27017`
  - Database: `starkeys`

- **Elasticsearch**: `localhost:9200`
  - Single node mode
  - Security disabled (for development)

**Verify Status:**
```bash
# Check all containers are running
docker compose ps

# View logs
docker compose logs -f
```

### 5. Run the Application

Start the application using Gradle:

```bash
./gradlew bootRun
```

**Success message:**
```
Started StarKeysBeApplication in X.XXX seconds
```

**Access the Application:**
- API Base URL: `http://localhost:8080/api`
- Health Check: `http://localhost:8080/api/health`

### 6. Verify Installation

Test the health check endpoint to confirm everything is working:

```bash
curl http://localhost:8080/api/health
```

**Expected response:**
```json
{
  "status": "UP"
}
```

Congratulations! You're now ready to start developing.

## Development Guide

### Building the Project

#### Standard Build
```bash
./gradlew build              # Build and run all tests
./gradlew assemble           # Build without running tests
./gradlew clean build        # Clean build from scratch
```

#### Create Executable JAR
```bash
./gradlew bootJar            # Creates executable JAR in build/libs/
```

The JAR file can be run with:
```bash
java -jar build/libs/be-0.0.1-SNAPSHOT.jar
```

### Testing

#### Run Tests
```bash
./gradlew test                                      # Run all tests
./gradlew test --tests ClassName                   # Run specific test class
./gradlew test --tests ClassName.methodName        # Run specific test method
./gradlew check                                     # Run all checks including tests
```

#### Test Reports
After running tests, view the HTML report at:
```
build/reports/tests/test/index.html
```

### IDE Setup

#### IntelliJ IDEA
1. Open the project: `File > Open` and select the project directory
2. Wait for Gradle to sync dependencies
3. Enable annotation processing:
   - `Settings > Build, Execution, Deployment > Compiler > Annotation Processors`
   - Check "Enable annotation processing"
4. Install Lombok plugin if not already installed

### Hot Reload

Spring Boot DevTools is included for development. Changes to your code will automatically reload:

1. Make changes to your code
2. Build the project (`Cmd+F9` in IntelliJ)
3. Application will restart automatically

### Database Management

#### Start/Stop Databases
```bash
docker compose up -d         # Start all databases
docker compose down          # Stop all databases
docker compose down -v       # Stop and remove volumes (fresh start)
```

#### Access Database Directly

**PostgreSQL:**
```bash
docker compose exec postgres psql -U starkeys -d starkeys
```

**MongoDB:**
```bash
docker compose exec mongo mongosh starkeys
```

**Elasticsearch:**
```bash
curl http://localhost:9200/_cat/indices?v
```

### Docker Image

Build an OCI-compliant container image:
```bash
./gradlew bootBuildImage
```

This creates a layered Docker image optimized for Spring Boot applications.

## Project Structure

This project follows Hexagonal Architecture (Ports and Adapters pattern) for clean separation of concerns:

```
src/main/java/com/starkeys/be/
├── adapter/                    # Adapters layer (external world)
│   ├── api/                   # Inbound adapters (REST Controllers)
│   │   ├── HealthController.java
│   │   └── ExternalController.java
│   └── out/                   # Outbound adapters (persistence, external services)
│       ├── elasticSearch/     # Elasticsearch repository implementations
│       └── mongo/             # MongoDB repository implementations
├── application/               # Application layer (use cases)
│   ├── service/              # Business logic services
│   │   └── ExternalService.java
│   └── repository/           # Repository interfaces (ports)
│       ├── ExternalRepository.java
│       └── ExternalRepositoryImpl.java
├── common/                    # Shared components
│   ├── config/               # Application configuration
│   │   └── CorsConfig.java
│   └── response/             # Standard response models
│       ├── ApiResponse.java
│       ├── ErrorDetail.java
│       └── PageMeta.java
├── entity/                   # Domain entities
│   └── Paper.java
└── StarKeysBeApplication.java # Main application entry point
```

### Architecture Layers Explained

**Adapter Layer**: Handles all external interactions
- **API (Inbound)**: REST endpoints that receive requests from clients
- **Out (Outbound)**: Implementations for databases and external services

**Application Layer**: Contains business logic
- **Service**: Implements use cases and business rules
- **Repository**: Defines interfaces for data access (dependency inversion)

**Common Layer**: Shared utilities and configurations
- **Config**: Spring configuration classes
- **Response**: Standardized API response formats

**Entity Layer**: Core domain models representing business objects

### Configuration Files

```
src/main/resources/
└── application.properties     # Application configuration with profiles
    ├── local profile          # Development settings (default)
    └── prod profile           # Production settings
```

## API Documentation

### Base URL
All API endpoints are prefixed with `/api`:
```
http://localhost:8080/api
```

### Available Endpoints

#### Health Check
Check if the application is running and healthy.

```bash
GET /api/health
```

**Example Request:**
```bash
curl http://localhost:8080/api/health
```

**Example Response:**
```json
{
  "status": "UP"
}
```

### Standard API Response Format

All API responses follow a consistent format using `ApiResponse`:

```json
{
  "data": { },
  "meta": {
    "page": 1,
    "size": 20,
    "total": 100
  },
  "errors": []
}
```

## Environment Profiles

The application supports multiple profiles for different environments.

### Local Profile (Default)

Activated automatically when running locally. Features:
- JPA DDL auto-update enabled
- SQL query logging enabled
- Docker Compose auto-start for databases
- H2 in-memory database available for testing

**Configuration:**
```properties
spring.profiles.active=local
spring.jpa.show-sql=true
spring.jpa.hibernate.ddl-auto=update
```

### Production Profile

For production deployments. Features:
- JPA DDL validation only (no auto-update)
- SQL logging disabled
- Environment variable-based configuration

**Activate production profile:**
```bash
./gradlew bootRun --args='--spring.profiles.active=prod'
```

**Required Environment Variables:**
```bash
# PostgreSQL
DB_URL=jdbc:postgresql://your-host:5432/starkeys
DB_USERNAME=your-username
DB_PASSWORD=your-password

# MongoDB
MONGO_URI=mongodb://your-host:27017/starkeys

# Elasticsearch
ES_URIS=http://your-host:9200
ES_USERNAME=your-username
ES_PASSWORD=your-password
```

## Deployment

### Building for Production

1. **Build the JAR:**
```bash
./gradlew clean build
```

2. **Run the JAR:**
```bash
java -jar build/libs/be-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

### Docker Deployment

1. **Build Docker image:**
```bash
./gradlew bootBuildImage
```

2. **Run with Docker:**
```bash
docker run -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e DB_URL=jdbc:postgresql://host:5432/starkeys \
  -e DB_USERNAME=user \
  -e DB_PASSWORD=pass \
  be:0.0.1-SNAPSHOT
```

### Infrastructure as Code

This project includes infrastructure automation:
- **Terraform**: Infrastructure provisioning (see `terraform/`)
- **Ansible**: Configuration management (see `ansible/`)

For detailed infrastructure setup, refer to [INFRASTRUCTURE.md](INFRASTRUCTURE.md).

## Contributing

### Commit Message Convention

This project enforces [Conventional Commits](https://www.conventionalcommits.org/) format:

```
<type>: <description>

[optional body]
[optional footer]
```

**Valid types:**
- `init`: Initial commit
- `feat`: New feature
- `fix`: Bug fix
- `docs`: Documentation changes
- `style`: Code style changes (formatting, missing semicolons, etc.)
- `refactor`: Code refactoring
- `test`: Adding or modifying tests
- `chore`: Maintenance tasks
- `perf`: Performance improvements
- `ci`: CI/CD changes
- `build`: Build system changes
- `revert`: Revert a previous commit

**Examples:**
```bash
feat: add user authentication
fix: resolve database connection timeout
docs: update API documentation
refactor(auth): simplify token validation
```

**Scope (optional):**
You can add a scope to provide more context:
```bash
feat(api): add new endpoint for user profile
fix(db): handle connection pool exhaustion
```

### Development Workflow

1. **Create a branch:**
```bash
git checkout -b feat/your-feature-name
```

2. **Make changes and commit:**
```bash
git add .
git commit -m "feat: your feature description"
```

3. **Run tests:**
```bash
./gradlew test
```

4. **Push and create PR:**
```bash
git push origin feat/your-feature-name
```

### Code Style

- Follow Java naming conventions
- Use Lombok to reduce boilerplate
- Write meaningful variable and method names
- Add JavaDoc for public APIs
- Keep methods small and focused
- Write tests for new features

## Additional Resources

- [CLAUDE.md](CLAUDE.md) - Detailed development guidelines for AI assistants
- [INFRASTRUCTURE.md](INFRASTRUCTURE.md) - Infrastructure setup and deployment
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Conventional Commits](https://www.conventionalcommits.org/)

## License

[Add your license here]

## Contact

[Add contact information or project links here]
