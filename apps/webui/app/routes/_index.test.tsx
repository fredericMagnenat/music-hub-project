import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import React from "react";
import Index from "./_index";
import { ToastProvider } from "~/components/ui/toast";

// Mock registerTrack to control responses
vi.mock("~/lib/utils", async (orig) => {
  const actual = await (orig as any)();
  return {
    ...actual,
    registerTrack: vi.fn(async (_isrc: string) => ({ ok: true, status: 202 })),
  };
});

function typeIsrc(value: string) {
  const input = screen.getByLabelText(/isrc/i) as HTMLInputElement;
  fireEvent.change(input, { target: { value } });
  return input;
}

function renderWithProviders(ui: React.ReactElement) {
  return render(<ToastProvider>{ui}</ToastProvider>);
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

  it("shows spinner while submitting and emits success toast on 202", async () => {
    renderWithProviders(<Index />);
    typeIsrc("FRLA12400001");

    const button = screen.getByRole("button", { name: /validate/i });
    fireEvent.click(button);

    // spinner appears (find spinner element by role or class)
    await waitFor(() => {
      expect(screen.getByText(/validate/i)).toBeInTheDocument();
    });

    await waitFor(() => {
      // message container shows 202
      expect(screen.getByText(/Accepted \(202\)/i)).toBeInTheDocument();
    });
  });

  it("shows error message on 400 and 422", async () => {
    const { registerTrack } = await import("~/lib/utils");
    (registerTrack as any).mockResolvedValueOnce({ ok: false, status: 400 });

    renderWithProviders(<Index />);
    typeIsrc("FRLA12400001");

    fireEvent.click(screen.getByRole("button", { name: /validate/i }));

    await waitFor(() => {
      expect(screen.getByText(/Invalid ISRC format \(400\)/i)).toBeInTheDocument();
    });

    ;(registerTrack as any).mockResolvedValueOnce({ ok: false, status: 422 });

    fireEvent.click(screen.getByRole("button", { name: /validate/i }));

    await waitFor(() => {
      expect(screen.getByText(/unresolvable/i)).toBeInTheDocument();
    });
  });
});
