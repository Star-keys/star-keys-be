# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a Spring Boot 3.5.7 backend application built with Java 21 and Gradle. The project uses:
- Spring Boot Web for REST APIs
- PostgreSQL as the database (configured as runtime dependency)
- JUnit Platform for testing

## Build & Development Commands

### Building the Project
```bash
./gradlew build              # Build and test the project
./gradlew assemble           # Build without running tests
./gradlew clean build        # Clean build
./gradlew bootJar            # Create executable JAR
```

### Running the Application
```bash
./gradlew bootRun            # Run the Spring Boot application
```

### Testing
```bash
./gradlew test               # Run all tests
./gradlew test --tests <ClassName>            # Run specific test class
./gradlew test --tests <ClassName>.<methodName>  # Run specific test method
./gradlew check              # Run all checks including tests
```

### Docker/Container
```bash
./gradlew bootBuildImage     # Build OCI container image
```

## Project Structure

- **Package Root**: `com.starkeys.be`
- **Main Application**: `StarKeysBeApplication.java` - Standard Spring Boot application entry point
- **Database**: PostgreSQL (configuration needed in `application.properties`)
- **Build System**: Gradle with Kotlin DSL
- **Java Version**: 21 (enforced via toolchain)

## Key Configuration

- **Application Properties**: `src/main/resources/application.properties`
  - Spring application name: `star-keys-be`
  - Database configuration will be needed for PostgreSQL connection

## Git Hooks Setup

This repository enforces conventional commit message format. **All contributors must install Git hooks after cloning:**

```bash
./setup-hooks.sh
```

### Commit Message Format
All commits must follow the conventional commit format:
```
<type>: <description>
```

**Valid types**: `init`, `feat`, `fix`, `docs`, `style`, `refactor`, `test`, `chore`, `perf`, `ci`, `build`, `revert`

**Note**: Types must be in English, but descriptions can be in English or Korean.

**Examples (English)**:
- `feat: add user authentication`
- `fix: resolve database connection timeout`
- `docs: update API documentation`
- `refactor(auth): simplify token validation`

**Examples (Korean)**:
- `feat: 사용자 인증 추가`
- `fix: 데이터베이스 연결 타임아웃 해결`
- `docs: API 문서 업데이트`
- `refactor(auth): 토큰 검증 로직 단순화`

## Repository Information

- Uses snapshot version of Spring Boot (3.5.7-SNAPSHOT) from `https://repo.spring.io/snapshot`
- Project artifact: `com.starkeys:be:0.0.1-SNAPSHOT`