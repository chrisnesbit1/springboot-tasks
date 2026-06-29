# Spring Boot Task Tracker API

## Overview

This project is a lightweight Task Tracker REST API built with Spring Boot.

The goal was to spend a focused weekend ramping up on Spring Boot and produce a small, working backend service with clear structure, tests, validation, and API documentation. The scope is intentionally narrow: it is not a full product, but it is meant to show the kind of production-oriented habits I would bring while learning a newer stack.

My production background has primarily been in the .NET ecosystem, so this repository is also a practical exercise in transferring backend engineering patterns—layered architecture, dependency injection, validation, error handling, and automated tests—into a Java/Spring Boot project.

## Current Scope

This version includes:

* A Spring Boot REST API for task management
* Layered controller, service, and repository structure
* In-memory persistence through a repository abstraction
* DTO-based request and response models
* Bean Validation for request input
* Global exception handling with consistent API error responses
* Swagger/OpenAPI documentation for local exploration
* Unit and web-layer tests around the main API behavior
* A GitHub Actions workflow that runs the Gradle build on pushes and pull requests to `main`

Deployment, durable persistence, authentication, and production operations are intentionally out of scope for this initial version and are listed under [Future Enhancements](#future-enhancements).

## Tech Stack

| Technology | Purpose |
| --- | --- |
| Java 21 | Language |
| Spring Boot 4.x | Application framework |
| Gradle | Build |
| Spring Web | REST API |
| Spring Validation | Request validation |
| SpringDoc OpenAPI | Swagger documentation |
| JUnit 5 | Testing |
| Mockito | Mocking |
| Git / GitHub | Source control |
| GitHub Actions | CI build and test workflow |
| ConcurrentHashMap | Initial in-memory persistence |

## Architecture

```text
Controller
    ↓
Service
    ↓
Repository
    ↓
In-Memory Data Store
```

## Project Structure

```text
controller/
service/
repository/
dto/
model/
exception/
config/
```

## What I Built

At a practical level, this demo includes:

* CRUD task management
* RESTful API endpoints
* Optional task filtering by status
* Summary endpoint with aggregate counts by status
* Created and completed timestamps on task records
* Automatic `OPEN` status assignment when tasks are created
* Completion timestamp handling when tasks move to `COMPLETED`
* DTO-based requests and responses
* Dependency injection
* Bean Validation
* Global exception handling
* Swagger/OpenAPI documentation with operation metadata
* Automated tests for service, controller, and exception behavior
* GitHub Actions workflow that runs `./gradlew build` on pushes and pull requests to `main`
* Gradle WAR packaging and `SpringBootServletInitializer` setup as preparation for servlet-container-style deployment
* Clean separation of concerns

## Task Model

```text
Task
-----
id : UUID
title : String
description : String
status : OPEN | IN_PROGRESS | COMPLETED
createdDate
completedDate
```

## REST Endpoints

```http
GET     /tasks
GET     /tasks/{id}
GET     /tasks?status=OPEN
POST    /tasks
PUT     /tasks/{id}
DELETE  /tasks/{id}
GET     /tasks/summary
```

The summary endpoint returns aggregate task counts by status. It is included to show a small amount of business-oriented API behavior beyond basic CRUD operations.

## Validation

Request validation is handled at the API boundary. Examples include:

* Title is required
* Title has a maximum length
* Description has a maximum length
* Status is required when updating a task

## Error Handling

A global exception handler returns consistent error responses for:

* Validation failures
* Resource not found errors
* Unexpected server errors

## Testing and CI

Tests cover the main behavior in this initial scope, including:

* Service-layer task operations
* Status filtering and summary counts
* Controller request/response behavior
* Validation failures
* Not-found handling
* Global exception handling
* Repository interactions through mocked dependencies

There is also a GitHub Actions workflow that runs `./gradlew build` for pushes and pull requests targeting `main`. That build includes the Gradle test task as part of the normal lifecycle. A second workflow job submits the Gradle dependency graph on pushes to `main`.

## API Documentation

Swagger/OpenAPI is enabled for local use.

After starting the application, the API can be explored interactively in the browser:

```text
http://localhost:8080/swagger-ui/index.html
```

## Running Locally

```bash
./gradlew bootRun
```

Run the test suite with:

```bash
./gradlew test
```

## What Is Not Included Yet

A few pieces are deliberately not presented as complete in this repository:

* **AWS Elastic Beanstalk deployment** - the project has not been deployed yet. Deployment is a logical next step, not part of the current demo scope.
* **Durable persistence** - tasks are stored in memory, so data resets when the application restarts.
* **Authentication and authorization** - the API is currently open for local/demo usage.
* **Deployment automation** - CI exists for build/test, but there is not yet an automated deployment pipeline.
* **Containerization** - Docker support has not been added.
* **Production observability** - Actuator is available as a dependency, but health checks, metrics, dashboards, and log aggregation have not been configured as a complete operational setup.
* **Integration tests against real infrastructure** - current tests focus on service, controller, validation, and exception behavior.

## Future Enhancements

Potential next steps include:

* Deploy the application to AWS Elastic Beanstalk
* Replace the in-memory repository with DynamoDB or another durable datastore
* Add Spring Security with Basic Authentication or token-based authentication
* Add Docker containerization
* Extend the existing GitHub Actions workflow with automated deployment
* Configure CloudWatch logging and monitoring
* Expose and document Spring Boot Actuator health checks
* Add pagination and sorting
* Add search endpoints
* Add metrics and observability dashboards
* Add integration tests
* Support multiple repository implementations behind the existing repository abstraction

These were intentionally deferred so the initial version could stay focused on learning the Spring Boot workflow and building a clean, reviewable API in a short timeframe.
