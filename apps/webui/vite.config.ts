import { vitePlugin as remix } from "@remix-run/dev";
import { installGlobals } from "@remix-run/node";
import { defineConfig } from "vite";
import { configDefaults } from "vitest/config";
import tsconfigPaths from "vite-tsconfig-paths";
import tailwindcss from "tailwindcss";

installGlobals();

export default defineConfig(({ mode }) => {
  const isTest = process.env.VITEST === "true" || mode === "test";
  return {
    plugins: [
      // Remix plugin interferes with Vitest for component tests; disable in test mode
      ...(isTest ? [] : [remix()]),
      tsconfigPaths(),
    ],
    css: {
      // si nécessaire, Vite détectera postcss.config automatiquement
    },
    server: {
      proxy: {
        // Proxy API calls to backend in development
        "/api": {
          target: "http://localhost:8080",
          changeOrigin: true,
          secure: false,
        },
      },
    },
    test: {
      environment: "jsdom",
      setupFiles: "./vitest.setup.ts",
      globals: true,
      css: true,
      exclude: [...configDefaults.exclude, "e2e/**"],
    },
  };
});
