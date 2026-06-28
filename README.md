# Spring Boot Task Tracker API

## Overview

This project is a lightweight Task Tracker REST API built with Spring Boot.

The intent of this project is to demonstrate familiarity with the Spring Boot ecosystem and modern backend development practices. Rather than building a feature-rich application, the focus is on clean architecture, maintainable code, REST API design, validation, testing, and documentation.

As an experienced backend engineer whose production experience has primarily been in the .NET ecosystem, this project demonstrates the ability to quickly learn and apply Spring Boot while following production-oriented software engineering practices.

## Goals

* Demonstrate Spring Boot fundamentals
* Build a well-structured REST API
* Follow clean architecture principles
* Showcase production-style coding practices
* Produce a polished GitHub portfolio project
* Deploy the application to AWS

## Tech Stack

| Technology | Purpose |
| --- | --- |
| Java 21 | Language |
| Spring Boot 3.x | Application Framework |
| Gradle | Build |
| Spring Web | REST API |
| Spring Validation | Request Validation |
| SpringDoc OpenAPI | Swagger Documentation |
| JUnit 5 | Unit Testing |
| Mockito | Mocking |
| Git / GitHub | Source Control |
| AWS Elastic Beanstalk | Hosting |
| ConcurrentHashMap | Initial In-Memory Persistence |

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

## Features

* CRUD Task Management
* RESTful API
* DTO-based requests/responses
* Dependency Injection
* Bean Validation
* Global Exception Handling
* Swagger/OpenAPI Documentation
* Unit Tests
* Clean Separation of Concerns

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

The optional summary endpoint returns aggregate information (counts by status) to demonstrate business-oriented API design beyond basic CRUD operations.

## Validation

### Examples

* Title is required
* Title maximum length
* Description maximum length

## Error Handling

A global exception handler returns consistent error responses for:

* Validation failures
* Resource not found
* Unexpected server errors

## Testing

Unit tests cover:

* Service layer
* Validation
* Exception handling
* Repository interactions (mocked)

## API Documentation

Swagger/OpenAPI is enabled.

The API can be explored interactively using Swagger UI, allowing reviewers to create, update, delete, and query tasks directly from the browser without additional tools.

## Deployment

### Deployment Target

AWS Elastic Beanstalk

### Deployment Artifact

```text
task-tracker.jar
```

## Future Enhancements

This project intentionally keeps its scope small so the emphasis remains on Spring Boot fundamentals and clean backend architecture rather than feature count.

Potential enhancements include:

* Replace the in-memory repository with DynamoDB
* Add Spring Security with Basic Authentication
* Docker containerization
* GitHub Actions CI/CD pipeline
* CloudWatch logging and monitoring
* Pagination and sorting
* Search endpoints
* Health checks with Spring Boot Actuator
* Metrics and observability
* Integration tests
* Repository abstraction backed by multiple persistence implementations

These enhancements were intentionally deferred to keep the initial implementation focused and to demonstrate that the project was scoped deliberately rather than left incomplete.

## Running Locally

```bash
./gradlew bootRun
```

## Swagger UI

```text
http://localhost:8080/swagger-ui/index.html
```

## Purpose

This project demonstrates the ability to quickly learn and apply a new framework while leveraging established backend engineering experience, including layered architecture, REST API design, dependency injection, validation, testing, and maintainable code organization.
