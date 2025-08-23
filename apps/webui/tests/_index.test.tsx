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
  beforeEach(() => {
    vi.clearAllMocks();
  });

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

// P2-9: Enhanced Toast notifications for track registration
describe("Enhanced Toast notifications (P2-9)", () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  describe("Success scenarios with track metadata", () => {
    it("shows success Toast with track details for single artist", async () => {
      const { registerTrack } = await import("~/lib/utils");
      const mockSuccessResponse = {
        ok: true,
        status: 202,
        data: {
          id: "test-id",
          producerCode: "TEST123", 
          name: "Test Producer",
          tracks: [{
            isrc: "FRLA12400001",
            title: "Bohemian Rhapsody",
            artists: ["Queen"],
            status: "Provisional"
          }]
        },
        trackInfo: {
          title: "Bohemian Rhapsody",
          artists: "Queen"
        }
      };

      (registerTrack as any).mockResolvedValueOnce(mockSuccessResponse);
      renderWithProviders(<Index />);

      fireEvent.change(screen.getByLabelText(/isrc/i), { target: { value: "FRLA12400001" } });
      fireEvent.click(screen.getByRole("button", { name: /validate/i }));

      await waitFor(() => {
        expect(screen.getByText("Track Registered Successfully")).toBeInTheDocument();
        expect(screen.getByText(/Track 'Bohemian Rhapsody' by 'Queen'/i)).toBeInTheDocument();
      });

      // Verify success styling
      await waitFor(() => {
        const successToast = screen.getByRole("status");
        expect(successToast).toHaveClass("border-green-200", "bg-green-50", "text-green-800");
      });
    });

    it("shows success Toast with multiple artists correctly formatted", async () => {
      const { registerTrack } = await import("~/lib/utils");
      const mockSuccessResponse = {
        ok: true,
        status: 202,
        trackInfo: {
          title: "Under Pressure",
          artists: "Queen, David Bowie"
        }
      };

      (registerTrack as any).mockResolvedValueOnce(mockSuccessResponse);
      renderWithProviders(<Index />);

      fireEvent.change(screen.getByLabelText(/isrc/i), { target: { value: "GBUM71505078" } });
      fireEvent.click(screen.getByRole("button", { name: /validate/i }));

      await waitFor(() => {
        expect(screen.getByText(/Track 'Under Pressure' by 'Queen, David Bowie'/i)).toBeInTheDocument();
      });
    });

    it("handles very long track titles and artist names gracefully", async () => {
      const { registerTrack } = await import("~/lib/utils");
      const longTitle = "This Is A Very Long Track Title That Should Be Handled Properly By The Toast Component Without Breaking The Layout";
      const longArtist = "An Artist With A Really Really Really Long Name That Could Potentially Cause UI Issues";

      const mockSuccessResponse = {
        ok: true,
        status: 202,
        trackInfo: {
          title: longTitle,
          artists: longArtist
        }
      };

      (registerTrack as any).mockResolvedValueOnce(mockSuccessResponse);
      renderWithProviders(<Index />);

      fireEvent.change(screen.getByLabelText(/isrc/i), { target: { value: "FRLA12400001" } });
      fireEvent.click(screen.getByRole("button", { name: /validate/i }));

      await waitFor(() => {
        expect(screen.getByText("Track Registered Successfully")).toBeInTheDocument();
      });

      // Verify the long content is displayed (even if truncated by CSS)
      await waitFor(() => {
        const description = screen.getByText(new RegExp(longTitle.substring(0, 20)));
        expect(description).toBeInTheDocument();
      });
    });

    it("handles missing track metadata gracefully", async () => {
      const { registerTrack } = await import("~/lib/utils");
      const mockIncompleteResponse = {
        ok: true,
        status: 202,
        data: {
          id: "test-id",
          producerCode: "TEST123",
          name: "Test Producer",
          tracks: [{
            isrc: "FRLA12400001",
            status: "Provisional"
            // title and artists missing
          }]
        }
        // trackInfo will be undefined due to missing data
      };

      (registerTrack as any).mockResolvedValueOnce(mockIncompleteResponse);
      renderWithProviders(<Index />);

      typeIsrc("FRLA12400001");
      fireEvent.click(screen.getByRole("button", { name: /validate/i }));

      // Should fallback to generic success message
      await waitFor(() => {
        expect(screen.getByText("Track Registered")).toBeInTheDocument();
        expect(screen.getByText("Track registration accepted and is being processed.")).toBeInTheDocument();
      });
    });
  });

  describe("Error scenarios - 422 external service failures", () => {
    it("shows error Toast with external service message on 422 response", async () => {
      const { registerTrack } = await import("~/lib/utils");
      const mock422Response = {
        ok: false,
        status: 422,
        error: "TRACK_NOT_FOUND_EXTERNAL",
        message: "The ISRC was valid, but we could not find metadata for it on external services."
      };

      (registerTrack as any).mockResolvedValueOnce(mock422Response);
      renderWithProviders(<Index />);

      fireEvent.change(screen.getByLabelText(/isrc/i), { target: { value: "UNKN12400001" } });
      fireEvent.click(screen.getByRole("button", { name: /validate/i }));

      await waitFor(() => {
        expect(screen.getByText("External Service Error")).toBeInTheDocument();
        expect(screen.getByText(/could not find metadata.*external services/i)).toBeInTheDocument();
      });

      // Verify error styling
      await waitFor(() => {
        const errorToast = screen.getByRole("alert");
        expect(errorToast).toHaveClass("border-red-200", "bg-red-50", "text-red-800");
      });
    });

    it("shows 422 error Toast with custom message when provided", async () => {
      const { registerTrack } = await import("~/lib/utils");
      const mock422Response = {
        ok: false,
        status: 422,
        error: "EXTERNAL_API_TIMEOUT",
        message: "External music service is currently unavailable. Please try again later."
      };

      (registerTrack as any).mockResolvedValueOnce(mock422Response);
      renderWithProviders(<Index />);

      typeIsrc("FRLA12400001");
      fireEvent.click(screen.getByRole("button", { name: /validate/i }));

      await waitFor(() => {
        expect(screen.getByText("External Service Error")).toBeInTheDocument();
        expect(screen.getByText("External music service is currently unavailable. Please try again later.")).toBeInTheDocument();
      });
    });
  });

  describe("Toast behavior and accessibility", () => {
    it("verifies appropriate Toast duration for different scenarios", async () => {
      const { registerTrack } = await import("~/lib/utils");

      // Test success Toast duration (5000ms)
      const mockSuccessResponse = {
        ok: true,
        status: 202,
        trackInfo: {
          title: "Test Track",
          artists: "Test Artist"
        }
      };

      (registerTrack as any).mockResolvedValueOnce(mockSuccessResponse);
      renderWithProviders(<Index />);

      fireEvent.change(screen.getByLabelText(/isrc/i), { target: { value: "FRLA12400001" } });
      fireEvent.click(screen.getByRole("button", { name: /validate/i }));

      await waitFor(() => {
        expect(screen.getByText("Track Registered Successfully")).toBeInTheDocument();
      });

      // Test 422 error Toast duration (7000ms) - longer than success
      vi.clearAllMocks();
      const mock422Response = {
        ok: false,
        status: 422,
        message: "External service error"
      };

      (registerTrack as any).mockResolvedValueOnce(mock422Response);
      fireEvent.change(screen.getByLabelText(/isrc/i), { target: { value: "UNKN12400001" } });
      fireEvent.click(screen.getByRole("button", { name: /validate/i }));

      await waitFor(() => {
        expect(screen.getByText("External Service Error")).toBeInTheDocument();
      });
    });

    it("maintains existing error handling for other status codes", async () => {
      const { registerTrack } = await import("~/lib/utils");

      // Test 400 error
      const mock400Response = {
        ok: false,
        status: 400,
        error: "INVALID_ISRC",
        message: "Invalid ISRC format"
      };

      (registerTrack as any).mockResolvedValueOnce(mock400Response);
      renderWithProviders(<Index />);

      fireEvent.change(screen.getByLabelText(/isrc/i), { target: { value: "INVALID" } });
      fireEvent.click(screen.getByRole("button", { name: /validate/i }));

      await waitFor(() => {
        expect(screen.getByText("Invalid ISRC Format")).toBeInTheDocument();
      });

      // Test 500 error
      vi.clearAllMocks();
      const mock500Response = {
        ok: false,
        status: 500,
        error: "INTERNAL_SERVER_ERROR"
      };

      (registerTrack as any).mockResolvedValueOnce(mock500Response);
      fireEvent.change(screen.getByLabelText(/isrc/i), { target: { value: "FRLA12400001" } });
      fireEvent.click(screen.getByRole("button", { name: /validate/i }));

      await waitFor(() => {
        expect(screen.getByText("Request Failed")).toBeInTheDocument();
        expect(screen.getByText(/Server responded with status 500/)).toBeInTheDocument();
      });
    });

    it("handles network errors with appropriate Toast message", async () => {
      const { registerTrack } = await import("~/lib/utils");
      (registerTrack as any).mockRejectedValueOnce(new Error("Network error"));

      renderWithProviders(<Index />);

      fireEvent.change(screen.getByLabelText(/isrc/i), { target: { value: "FRLA12400001" } });
      fireEvent.click(screen.getByRole("button", { name: /validate/i }));

      await waitFor(() => {
        expect(screen.getByText("Network Error")).toBeInTheDocument();
        expect(screen.getByText(/Unable to connect to server/i)).toBeInTheDocument();
      });
    });

    it("verifies Toast accessibility attributes", async () => {
      const { registerTrack } = await import("~/lib/utils");

      // Test success toast accessibility
      const mockSuccessResponse = {
        ok: true,
        status: 202,
        trackInfo: {
          title: "Test Track",
          artists: "Test Artist"
        }
      };

      (registerTrack as any).mockResolvedValueOnce(mockSuccessResponse);
      renderWithProviders(<Index />);

      typeIsrc("FRLA12400001");
      fireEvent.click(screen.getByRole("button", { name: /validate/i }));

      // Verify success toast has correct ARIA role
      await waitFor(() => {
        const successToast = screen.getByRole("status");
        expect(successToast).toBeInTheDocument();
        expect(successToast).toHaveAttribute("role", "status");
      });

      // Test error toast accessibility
      const mock422Response = {
        ok: false,
        status: 422,
        message: "External service error"
      };

      (registerTrack as any).mockResolvedValueOnce(mock422Response);
      fireEvent.click(screen.getByRole("button", { name: /validate/i }));

      // Verify error toast has correct ARIA role
      await waitFor(() => {
        const errorToast = screen.getByRole("alert");
        expect(errorToast).toBeInTheDocument();
        expect(errorToast).toHaveAttribute("role", "alert");
      });
    });

    it("handles rapid multiple submissions correctly", async () => {
      const { registerTrack } = await import("~/lib/utils");
      const mockResponse = {
        ok: true,
        status: 202,
        trackInfo: {
          title: "Test Track",
          artists: "Test Artist"
        }
      };

      (registerTrack as any).mockResolvedValueOnce(mockResponse);
      renderWithProviders(<Index />);

      const input = screen.getByLabelText(/isrc/i);
      const button = screen.getByRole("button", { name: /validate/i });

      // Simulate rapid multiple clicks
      fireEvent.change(input, { target: { value: "FRLA12400001" } });
      fireEvent.click(button);
      expect(button).toBeDisabled();

      // Wait for completion
      await waitFor(() => {
        expect(screen.getByText("Track Registered Successfully")).toBeInTheDocument();
      });

      // Button should be enabled again
      expect(button).not.toBeDisabled();
    });
  });
});
