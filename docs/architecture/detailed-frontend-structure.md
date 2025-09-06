# Detailed Frontend Structure

The frontend architecture is built on a modern stack using Remix as the framework, Vite for the build process, Tailwind CSS for styling, and Vitest with React Testing Library for testing.

## Directory Structure

The file structure follows Remix conventions and is organized for clarity and separation of concerns. A dedicated `types` directory is established to hold all data contract interfaces.

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
│   ├── types/            # DEDICATED DIRECTORY FOR TYPES
│   │   ├── index.ts      # Exports all types from the directory
│   │   ├── artist.ts     # Contains the "Artist" interface and its dependencies
│   │   ├── track.ts      # Contains the "Track" interface and its dependencies
│   │   └── producer.ts   # Contains the "Producer" interface
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

## Component Philosophy

The component organization is two-tiered:

* **Base Components (`app/components/ui/`)**: Reusable, primitive components inspired by **shadcn/ui**, such as `Button` and `Input`. They contain no business logic.
* **Business Components (`app/components/`)**: Composite components that assemble base components to build specific features, like `RecentTracksList.tsx`.

## API Integration

Communication with the Quarkus backend is managed centrally:

* **Centralized API Calls**: All functions that communicate with the backend are located in `app/lib/utils.ts`.
* **Development Proxy**: The `vite.config.ts` file configures a proxy for all requests starting with `/api`, redirecting them to the backend running on `http://localhost:8080`.

## State Management

For the current scope of the PoC, state management is **local to the components**, using standard React hooks (`useState`, `useEffect`). No global state management library is used, which is a pragmatic approach suited to the current application complexity.

## Testing Strategy

* **Tooling**: **Vitest** is used as the test runner, with **React Testing Library** for rendering and interacting with components.
* **API Mocking**: Component tests simulate API calls by mocking the `~/lib/utils` module, allowing UI behavior to be tested in isolation.


-----
