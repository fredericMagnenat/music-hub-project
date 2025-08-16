# P2-7: Update frontend Toast notifications for enhanced API responses

Story: docs/stories/story-P2.md

## Description
Enhance the frontend UI to display appropriate Toast notifications for successful track registration (with track metadata) and handle the new 422 error scenario with descriptive messaging using shadcn/ui Toast components.

## Acceptance Criteria
- Given successful 202 response with track metadata, when request completes, then Toast shows track title and artist names (e.g., "Track 'Bohemian Rhapsody' by 'Queen' has been successfully registered")
- Given 422 Unprocessable Entity response, when request completes, then Toast shows descriptive error message about external service failure
- Toast notifications use consistent styling and timing across success/error scenarios
- Messages are user-friendly and provide actionable information
- Existing error handling for other status codes remains unchanged

## Dependencies
- P2-6: REST controller must return 422 for external service failures
- P2-5: API response must include track metadata in successful cases
- Story P1: Existing Toast implementation from previous story

## Estimate
- 2 pts

## Status
- Not Started

## Technical Details

### Files to modify:
- `apps/webui/app/lib/utils.ts` (registerTrack function)
- `apps/webui/app/routes/_index.tsx` (form handling)
- Potentially: Toast component styling/configuration

### Enhanced registerTrack function:
```typescript
export async function registerTrack(isrc: string): Promise<HttpResult<ProducerDto>> {
  const response = await fetch("/api/v1/producers", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({ isrc }),
  });

  if (response.ok) {
    const data = await response.json();
    return { 
      ok: true, 
      status: response.status, 
      data,
      // Extract track info for success message
      trackInfo: extractTrackInfo(data)
    };
  } else {
    const errorData = await response.json();
    return { 
      ok: false, 
      status: response.status, 
      error: errorData.error,
      message: errorData.message 
    };
  }
}
```

### Enhanced Toast messaging:
```typescript
// Success case - show track details
if (result.ok && result.trackInfo) {
  toast({
    title: "Track Registered Successfully",
    description: `Track '${result.trackInfo.title}' by '${result.trackInfo.artists}' has been successfully registered and is being processed.`,
    duration: 5000,
  });
}

// 422 error case - external service failure
if (!result.ok && result.status === 422) {
  toast({
    title: "External Service Error",
    description: result.message || "The ISRC was valid, but we could not find metadata for it on external services.",
    variant: "destructive",
    duration: 7000,
  });
}
```

## Validation Steps
1. Update registerTrack function to extract track metadata from successful responses
2. Enhance Toast success message to include track title and artist names
3. Add specific handling for 422 errors with descriptive message
4. Test success flow:
   - Submit valid ISRC that external API can resolve
   - Verify Toast shows track title and artists
   - Verify success styling and timing
5. Test 422 error flow:
   - Submit valid ISRC that external API cannot resolve (mock scenario)
   - Verify Toast shows external service error message
   - Verify error styling and longer duration
6. Test existing error scenarios still work (400, 500)
7. Verify Toast accessibility and responsive behavior
8. Test with various track titles and artist name lengths

## Next Task
- P2-8: Backend integration tests with WireMock

## Artifacts
- Modified: `utils.ts` with enhanced API response handling
- Modified: `_index.tsx` with improved Toast messaging
- Updated: Frontend tests for new Toast scenarios