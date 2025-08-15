# Bug: WebUI - 404 on creation request

- Type: Bug
- Module: Web UI (creation flow)
- Environment: dev
- Severity: [Blocker|Critical|Major|Minor] (specify)
- Detected by: Métier
- Report date: 2025-08-15
- Owner: [assignee]
- Sprint target: 2025-W34

## Summary
When user triggers the creation request from the Web UI, the app returns HTTP 404.
In dev, the frontend issues a relative request to `/api/v1/producers` instead of targeting the backend host, which should be `http://localhost:8080`.

## Steps to Reproduce
1. Go to [page/screen]
2. Click on [Create]
3. Fill [fields]
4. Submit

## Actual Result
HTTP 404 on the creation request.

## Expected Result
Resource is created, API returns 201 and UI navigates to the new resource detail page.

## Evidence / Logs
- Code reference (frontend):
```21:29:apps/webui/app/lib/utils.ts
export async function registerTrack(isrc: string): Promise<HttpResult<ProducerDto>> {
  const response = await fetch("/api/v1/producers", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({ isrc }),
  })
```
- Expected dev backend base URL: `http://localhost:8080`
- Expected full request in dev: `POST http://localhost:8080/api/v1/producers`
- Actual request in dev: `POST /api/v1/producers` (relative → handled by Remix+Vite dev server, resulting in 404)
- Response body: [paste if available]
- Console logs: [paste]
- Backend logs/trace-id: [paste]

## Root cause hypothesis
- In dev, no proxy/base URL is configured for the frontend to reach the backend. The relative path `/api/v1/producers` hits the front server (Remix+Vite) instead of the Spring Boot API at `localhost:8080`, causing 404.

## Scope / Impact
- Affected users: [who/how many]
- Frequency: [always/often/intermittent]
- Regression since: [version|PR]

## Suspected Areas
- Frontend: missing API base URL or dev proxy configuration
- Backend: verify controller route `/api/v1/producers` exists and method is POST
- Reverse proxy (if any): ensure rewrite from `/api/v1/*` → backend in dev

## Acceptance Criteria
- Creating from the Web UI succeeds with 201 Created
- In dev, frontend calls `http://localhost:8080/api/v1/producers` (via base URL or proxy)
- UI shows success state and navigates to created resource
- Error handling path displays actionable message if backend is unavailable
- E2E test covers the creation happy path

## Tasks
- [ ] Confirm failing request URL and method from DevTools
- [ ] Compare against Swagger/OpenAPI (`docs/swagger/*`) for `/api/v1/producers`
- [ ] Configure Vite dev proxy in `apps/webui/vite.config.ts` (to be implemented by dev team):
  - `server.proxy['/api'] → http://localhost:8080`
- [ ] Re-test creation flow in dev; expect `POST /api/v1/producers` to be proxied and return 201/202
- [ ] Verify backend controller route and HTTP method (POST `/api/v1/producers`)
- [ ] Add/adjust integration and E2E tests for creation flow in dev
- [ ] Update documentation if configuration changed

## Links
- Sprint: `docs/sprint/Sprint-2025-W34/`
- Previous sprint: `docs/sprint/Sprint-2025-W33/`
