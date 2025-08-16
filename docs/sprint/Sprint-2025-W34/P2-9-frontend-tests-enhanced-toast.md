# P2-9: Frontend tests for enhanced Toast scenarios

Story: docs/stories/story-P2.md

## Description
Create comprehensive frontend tests using Vitest and React Testing Library to verify the enhanced Toast notification behavior for successful track registration with metadata and 422 error scenarios.

## Acceptance Criteria
- Given successful API response with track metadata, when form is submitted, then success Toast displays track title and artist names
- Given 422 API response, when form is submitted, then error Toast displays external service failure message
- Tests verify correct Toast styling (success vs error variants)
- Tests verify appropriate Toast duration for different scenarios
- Existing Toast tests for other error scenarios continue to pass

## Dependencies
- P2-7: Enhanced frontend Toast implementation must be complete
- P2-8: Backend integration tests should be working (for understanding API contract)

## Estimate
- 2 pts

## Status
- Not Started

## Technical Details

### Files to modify:
- `apps/webui/app/routes/_index.test.tsx` (or similar test file)

### Test scenarios to implement:
```typescript
describe("Enhanced Toast notifications for track registration", () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it("shows success Toast with track details when API returns track metadata", async () => {
    // Given - mock successful API response with track metadata
    const mockSuccessResponse = {
      ok: true,
      status: 202,
      tracks: [{
        isrc: "FRLA12400001",
        title: "Bohemian Rhapsody",
        artistNames: ["Queen"]
      }]
    };
    
    vi.mocked(registerTrack).mockResolvedValue(mockSuccessResponse);
    
    renderWithProviders(<Index />);
    
    // When - submit valid ISRC
    const input = screen.getByLabelText(/isrc/i);
    const button = screen.getByRole("button", { name: /validate/i });
    
    fireEvent.change(input, { target: { value: "FRLA12400001" } });
    fireEvent.click(button);
    
    // Then - verify success Toast with track details
    await waitFor(() => {
      expect(screen.getByText(/Track Registered Successfully/i)).toBeInTheDocument();
      expect(screen.getByText(/Track 'Bohemian Rhapsody' by 'Queen'/i)).toBeInTheDocument();
    });
    
    // Verify success styling
    const toastElement = screen.getByRole("status");
    expect(toastElement).not.toHaveClass("destructive");
  });

  it("shows error Toast with external service message on 422 response", async () => {
    // Given - mock 422 API response
    const mock422Response = {
      ok: false,
      status: 422,
      error: "TRACK_NOT_FOUND_EXTERNAL",
      message: "The ISRC was valid, but we could not find metadata for it on external services."
    };
    
    vi.mocked(registerTrack).mockResolvedValue(mock422Response);
    
    renderWithProviders(<Index />);
    
    // When - submit ISRC that external API cannot resolve
    const input = screen.getByLabelText(/isrc/i);
    const button = screen.getByRole("button", { name: /validate/i });
    
    fireEvent.change(input, { target: { value: "UNKN12400001" } });
    fireEvent.click(button);
    
    // Then - verify error Toast with external service message
    await waitFor(() => {
      expect(screen.getByText(/External Service Error/i)).toBeInTheDocument();
      expect(screen.getByText(/could not find metadata.*external services/i)).toBeInTheDocument();
    });
    
    // Verify error styling
    const toastElement = screen.getByRole("alert");
    expect(toastElement).toHaveClass("destructive");
  });

  it("maintains existing error handling for other status codes", async () => {
    // Test that existing 400, 500 error handling still works
    const mock400Response = {
      ok: false,
      status: 400,
      error: "INVALID_ISRC",
      message: "Invalid ISRC format"
    };
    
    // Similar test pattern for existing error scenarios
  });
});
```

### Mock configuration:
```typescript
// Mock registerTrack to control API responses
vi.mock("~/lib/utils", async (orig) => {
  const actual = await (orig as any)();
  return {
    ...actual,
    registerTrack: vi.fn(),
  };
});
```

## Validation Steps
1. Update existing test file or create new test file for enhanced Toast scenarios
2. Mock registerTrack function to return various response types
3. Test success scenario:
   - Mock API response with track metadata
   - Verify Toast title and description show track details
   - Verify success styling (no destructive class)
   - Test Toast duration and auto-dismiss behavior
4. Test 422 error scenario:
   - Mock 422 API response with external service error
   - Verify Toast shows external service error message
   - Verify error styling (destructive variant)
   - Test longer error Toast duration
5. Verify existing error tests still pass (400, 500)
6. Test edge cases:
   - Very long track titles and artist names
   - Multiple artist names formatting
   - Missing track metadata in success response
7. Test Toast accessibility (roles, ARIA attributes)
8. Test Toast behavior on multiple rapid submissions

## Next Task
- Story P2 completion and acceptance criteria verification

## Artifacts
- Created/Modified: Frontend test files for enhanced Toast scenarios
- Updated: Test utilities for Toast verification
- Created: Mock response templates for various API scenarios