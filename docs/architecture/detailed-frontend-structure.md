# Detailed Frontend Structure

The frontend architecture is built on a modern stack using Remix as the framework, Vite for the build process, Tailwind CSS for styling, and Vitest with React Testing Library for testing.

## Directory Structure

The file structure follows Remix conventions, optimized for clarity and separation of concerns.

```plaintext
webui/
├── app/                  # Core of the Remix application
│   ├── components/
│   │   ├── ui/           # Base components (primitives, shadcn/ui style)
│   │   │   ├── button.tsx
│   │   │   ├── input.tsx
│   │   │   ├── status-badge.tsx
│   │   │   └── toast.tsx
│   │   └── RecentTracksList.tsx # Custom business component
│   │
│   ├── lib/
│   │   └── utils.ts      # Utility functions and centralized API calls
│   │
│   ├── routes/
│   │   └── _index.tsx    # File for the main route ("/")
│   │
│   ├── root.tsx          # Global application layout, includes <head>, <body>
│   └── tailwind.css      # Global styles and Tailwind configuration
│
├── tests/
│   ├── _index.test.tsx   # Tests for the main route
│   └── api.test.tsx      # Tests for the API call layer
│
├── vite.config.ts        # Vite configuration (including API proxy)
├── tailwind.config.ts    # Tailwind CSS configuration
└── package.json          # Frontend project dependencies and scripts
```

## 11.2. Component Philosophy

The component organization is two-tiered, which is a robust practice:

*   **Base Components (`app/components/ui/`)**: These are reusable, primitive components inspired by **shadcn/ui**, such as `Button`, `Input`, and `Toast`. They contain no business logic and are styled via `class-variance-authority`.
*   **Business Components (`app/components/`)**: These are composite components that assemble the base components to build specific features. `RecentTracksList.tsx` is a perfect example: it manages its own state (loading, error, data) and makes the API call to display the list of recent tracks.

## 11.3. API Integration

Communication with the Quarkus backend is managed centrally and efficiently:

*   **Centralized API Calls**: All functions that communicate with the backend are located in `app/lib/utils.ts`. The `registerTrack` and `getRecentTracks` functions encapsulate the `fetch` logic, header management, and response handling.
*   **Development Proxy**: The `vite.config.ts` file configures a proxy for all requests starting with `/api`, redirecting them to the backend running on `http://localhost:8080`. This simplifies `fetch` calls in the code, allowing the use of relative paths (e.g., `/api/producers`) without worrying about CORS or full URLs in a development environment.

## 11.4. State Management

For the current scope of the PoC, state management is **local to the components**. The project uses standard React hooks (`useState`, `useCallback`, `useMemo`) to manage the ISRC form state and the loading state of the tracks list. There is no global state management library (like Redux or Zustand), which is a pragmatic approach suited to the current application complexity.

## 11.5. Testing Strategy

The testing strategy is robust and well-defined:

*   **Tooling**: **Vitest** is used as the test runner, with **React Testing Library** for rendering and interacting with components.
*   **API Mocking**: Component tests simulate API calls by mocking the `~/lib/utils` module. This allows testing the UI behavior in different scenarios (success, 4xx error, 5xx error) without dependency on a running backend.

-----
