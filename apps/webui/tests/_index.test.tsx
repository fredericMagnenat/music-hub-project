/// <reference types="vitest/globals" />
/// <reference types="@testing-library/jest-dom" />

import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import { vi } from "vitest";
import React from "react";
import Index from "~/routes/_index";
import { ToastProvider, Toaster } from "~/components/ui/toast";

// Mock registerTrack to control responses
vi.mock("~/lib/utils", async (orig) => {
  const actual = await (orig as any)();
  return {
    ...actual,
    registerTrack: vi.fn(async (_isrc: string) => ({ 
      ok: true, 
      status: 202,
      data: {
        id: "test-id",
        producerCode: "TEST123",
        name: "Test Producer",
        tracks: [{
          title: "Test Track",
          artists: ["Test Artist"],
          isrc: "FRLA12400001",
          status: "Provisional"
        }]
      },
      trackInfo: {
        title: "Test Track",
        artists: "Test Artist"
      }
    })),
  };
});

function typeIsrc(value: string) {
  const input = screen.getByLabelText(/isrc/i) as HTMLInputElement;
  fireEvent.change(input, { target: { value } });
  return input;
}

function renderWithProviders(ui: React.ReactElement) {
  return render(
    <ToastProvider>
      {ui}
      <Toaster />
    </ToastProvider>
  );
}

describe("Index route (ISRC form)", () => {
  it("disables button when ISRC invalid and enables when valid", () => {
    renderWithProviders(<Index />);
    const button = screen.getByRole("button", { name: /validate/i });

    typeIsrc("INVALID");
    expect(button).toBeDisabled();

    typeIsrc("FR-LA1-24-00001"); // normalizes to FRLA12400001
    expect(button).not.toBeDisabled();
  });

  it("shows spinner while submitting and displays success toast with track info on 202", async () => {
    renderWithProviders(<Index />);
    typeIsrc("FRLA12400001");

    const button = screen.getByRole("button", { name: /validate/i });
    fireEvent.click(button);

    await waitFor(() => {
      expect(screen.getByText(/validate/i)).toBeInTheDocument();
    });

    await waitFor(() => {
      expect(screen.getByText(/Accepted \(202\)/i)).toBeInTheDocument();
    });

    // Check for the enhanced toast with track information
    await waitFor(() => {
      expect(screen.getByText("Track Registered Successfully")).toBeInTheDocument();
    });

    await waitFor(() => {
      expect(screen.getByText(/Track 'Test Track' by 'Test Artist'/)).toBeInTheDocument();
    });
  });

  it("shows enhanced error messages with Toast titles on 400 and 422", async () => {
    const { registerTrack } = await import("~/lib/utils");
    (registerTrack as any).mockResolvedValueOnce({ ok: false, status: 400 });

    renderWithProviders(<Index />);
    typeIsrc("FRLA12400001");

    fireEvent.click(screen.getByRole("button", { name: /validate/i }));

    await waitFor(() => {
      expect(screen.getByText(/Invalid ISRC format \(400\)/i)).toBeInTheDocument();
    });

    // Check for enhanced Toast title for 400 error
    await waitFor(() => {
      expect(screen.getByText("Invalid ISRC Format")).toBeInTheDocument();
    });

    // Test 422 error with descriptive message
    (registerTrack as any).mockResolvedValueOnce({ 
      ok: false, 
      status: 422, 
      message: "External service unavailable" 
    });

    fireEvent.click(screen.getByRole("button", { name: /validate/i }));

    await waitFor(() => {
      expect(screen.getByText(/unresolvable/i)).toBeInTheDocument();
    });

    // Check for enhanced Toast title and description for 422 error
    await waitFor(() => {
      expect(screen.getByText("External Service Error")).toBeInTheDocument();
    });

    await waitFor(() => {
      expect(screen.getByText("External service unavailable")).toBeInTheDocument();
    });
  });

  it("displays success toast without track info when trackInfo is missing", async () => {
    const { registerTrack } = await import("~/lib/utils");
    (registerTrack as any).mockResolvedValueOnce({ ok: true, status: 202 }); // No trackInfo

    renderWithProviders(<Index />);
    typeIsrc("FRLA12400001");

    fireEvent.click(screen.getByRole("button", { name: /validate/i }));

    await waitFor(() => {
      expect(screen.getByText(/Accepted \(202\)/i)).toBeInTheDocument();
    });

    // Check for fallback toast without track details
    await waitFor(() => {
      expect(screen.getByText("Track Registered")).toBeInTheDocument();
    });

    await waitFor(() => {
      expect(screen.getByText("Track registration accepted and is being processed.")).toBeInTheDocument();
    });
  });
});
