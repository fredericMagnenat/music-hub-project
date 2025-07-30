# Security and Secrets Management

This section outlines the strategy for handling sensitive information like API keys and database credentials.

## Prerequisite: External API Access

**CRITICAL BLOCKER:** Development cannot begin until access details for the external Music Platform API are acquired. This includes:
*   API Base URL
*   Authentication method (e.g., API Key, OAuth)
*   The API Key or credentials themselves
*   Rate limits and usage policies

This information must be obtained by the project owner and securely shared with the development team.

## Secrets Management Strategy

*   **Local Development:** Secrets will be managed using environment variables loaded via a `.env` file at the root of the backend and frontend applications. These files **must be included in `.gitignore`** and never committed to version control. An `.env.example` file should be created to document required variables.
*   **Production (AWS):** Secrets will be stored securely in **AWS Secrets Manager**. The Quarkus and Remix applications will be granted IAM permissions to fetch these secrets at runtime. This avoids storing sensitive data in container images or environment variables on the production host.
