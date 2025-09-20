# **Product Requirements Document (PRD): Centralized Music Catalogue**

* **Version:** 1.2
* **Date:** September 19, 2025
* **Author:** PO (Sarah) - Standardized story naming conventions.

---

## Goals and Background Context

### Goals

* Create a **product** to centralize and verify music track catalogues for producers, labels, and artists.
* Provide immediate user value through a minimal interface as part of a PoC (Proof of Concept).
* Validate a Hexagonal and Event-Driven technical architecture to ensure the future maintainability and scalability of the solution.
* Eliminate the manual and time-consuming management of catalogues currently done in Excel files.

### Background Context

Currently, music professionals such as producers, labels, and artists face significant challenges in managing their catalogues. The process is often manual, relying on spreadsheets like Excel, which leads to errors, wasted time, and unreliable data. There is no automated link between a track's metadata (like its ISRC) and its presence on various streaming platforms, forcing tedious manual checks.

This project aims to solve this problem by creating a centralized **product**. The PoC will focus on the most critical functionality identified: allowing a user to add a track via its ISRC code, automatically retrieve metadata from streaming services, and integrate it into their unified catalogue, thus providing an immediate efficiency gain.

### Change Log

| Date | Version | Description | Author |
| :--- | :--- | :--- | :--- |
| 2025-07-25 | 1.0 | Final version following complete elicitation. | PM |
| 2025-09-19 | 1.1 | Integrated all epics (0-4) into the PRD for BMad V4 compliance. | PO (Sarah) |
| 2025-09-19 | 1.2 | Standardized all story identifiers to `Epic.Story` format for clarity and consistency. | PO (Sarah) |

---

## Epics & User Stories

### Epic 0: Project Initialization

* **Objective:** Establish the complete development foundation, including the monorepo structure, backend and frontend project skeletons, the CI/CD pipeline, and a fully functional local development environment.
* **Key Stories:**
    * **Story 0.01:** Set Up the Monorepo Structure
    * **Story 0.02:** Generate the Backend Application Skeleton
    * **Story 0.03:** Initialize the Frontend Application
    * **Story 0.04:** Configure the Unified Development Environment
    * **Story 0.05:** Implement the Basic CI/CD Pipeline

### Epic 1: Core Producer Management System

* **Objective:** Establish a robust producer management system with automatic ISRC validation and catalogue creation.
* **Key Stories:**
    * **Story 1.01:** Validate and Create a Producer
    * **Story 1.02:** Integrate a Track and Publish an Event
    * **Story 1.03:** External API Mock Services
    * **Story 1.04:** Recent Tracks API Endpoint

### Epic 2: Artist Context Event Processing

* **Objective:** Implement an autonomous artist management system that reacts to `TrackWasRegistered` events to maintain up-to-date profiles without manual intervention.
* **Key Stories:**
    * **Story 2.01:** Update an Artist Following an Event
    * **Story 2.02:** Manage an Artist's Data

### Epic 3: UI/UX Foundation & Dashboard

* **Objective:** Establish a solid UI/UX foundation with reusable components and a functional dashboard for a coherent and accessible user experience.
* **Key Stories:**
    * **Story 3.01:** Adopt shadcn-style UI Components for ISRC form
    * **Story 3.02:** Implement Dashboard with Recent Tracks list
    * **Story 3.03:** Accessibility Implementation (WCAG 2.1 AA)

### Epic 4: Technical Debt and Refactoring

* **Objective:** Improve the internal quality of the codebase, address technical debt, and perform refactorings to enhance maintainability, performance, and adherence to architectural standards. (Related to NFR4, NFR6)
* **Key Stories:**
    * **Story 4.01:** Improve Logging in RegisterTrackService
    * **Story 4.02:** Refactor Source Value Object for Cross-Context Reusability (was 1.05)
    * **Story 4.03:** Create and Maintain a Version Compatibility Matrix (was 0.13)

---

## User Interface Design Goals

* **Overall UX Vision**: The user experience must be simple, clean, and extremely efficient. The main user journey must be smooth and frictionless.
* **Key Interaction Paradigms**: The product will behave like a modern "Single-Page Application" (SPA) with immediate visual feedback and instant field validation.
* **Core Screens and Views**: Main dashboard (track list), ISRC entry form, Preview screen, Manual creation form.
* **Accessibility**: Aim for WCAG AA compliance level.
* **Branding**: None for the PoC. The design will be neutral and functional.
* **Target Device and Platforms**: Web Responsive (Desktop and tablet).

---

## Technical Assumptions

* **Repository Structure**: Monorepo.
* **Service Architecture**: Hexagonal, Domain-Driven Design (DDD), Event-Driven.
* **Testing Requirements**:
    * **Back-end (Quarkus)**: Unit and integration tests (JUnit). Code coverage measured with Jacoco (80% target).
    * **Front-end (Remix)**: Unit and component tests with Vitest and React Testing Library (80% target).
    * **End-to-End Tests**: Out of scope for the PoC.
* **Additional Technical Assumptions**:
    * **Stack**: Java (Quarkus) for the back-end, TypeScript (Remix) for the front-end.
    * **Database**: PostgreSQL.
    * **Deployment**: Docker containers.
    * **API Style**: REST.
    * **Event Bus**: Internal to the Quarkus application.
    * **Authentication**: Out of scope for the PoC.
* **Non-Functional Requirements**:
    * **NFR1**: Adherence to Hexagonal architecture principles.
    * **NFR2**: Response time < 5 seconds for ISRC search.
    * **NFR3**: 80% test coverage (back-end and front-end).
    * **NFR4**: Domain implementation according to DDD patterns.
    * **NFR5**: Use of an Event-Driven approach.
    * **NFR6**: Structured logging (JSON) for back-end errors.
    * **NFR7**: Exposure of a `/health` API endpoint for monitoring.