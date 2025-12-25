**S3M Formation Management Application**
**Overview**

S3M Formation is a backend application designed to manage professional training formations for enterprises.
It allows companies to browse formations, request training sessions, manage participants, and track training sessions from planning to completion.

The application follows a database-first, clean layered architecture, and uses JWT-based authentication for secured operations.

**Business Context**

Enterprises want to:

Browse available formations

Request customized training sessions

Specify dates, technical requirements, and comments

Track the lifecycle of training sessions

Manage participants and formateurs (trainers)

This application models that process from training discovery to session execution.

**Architecture**

The application is built with:

Spring Boot 3

Spring Data JPA (Hibernate)

Spring Security (JWT)

PostgreSQL

RESTful API design

Stateless authentication

**Design Principles**

Database-first approach

Explicit mappings (no implicit schema generation)

Stateless security

Clear separation of concerns

Production-ready structure
