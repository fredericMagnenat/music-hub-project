# Introduction

This document describes the complete full-stack architecture for the Music Hub project, including backend systems, frontend implementation, and their integration. It serves as the single source of truth for development, ensuring consistency across the entire technology stack. This unified approach is designed to streamline the development process for this application where backend and frontend concerns are tightly coupled.

## Starter Template or Existing Project

Project analysis reveals that it is not based on a predefined full-stack starter template. It is a custom structure that combines:

  * A **Quarkus backend** following a rigorous hexagonal architecture.
  * A **Remix frontend** integrated and served by the backend via the **Quarkus Quinoa** extension.
  * The entire project is assembled in a **Maven monorepo**, with the `bootstrap` module acting as the main entry point.

## Change Log

| Date | Version | Description | Author |
| :--- | :--- | :--- | :--- |
| 06/09/2025 | 2.0 | Aligned with the final version of the Domain Charter, added design patterns and business logic implementation details. | Winston (Architect) |
| 04/09/2025 | 1.0 | Initial creation of the full-stack architecture document. | Winston (Architect) |

-----
