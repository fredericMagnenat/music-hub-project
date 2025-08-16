import { describe, it, expect, beforeEach, afterEach, vi } from 'vitest';
import { registerTrack } from '~/lib/utils';

/**
 * Integration test for the API proxy configuration.
 * This test simulates the scenario described in BUG-webui-creation-404.md
 */
describe('API Proxy Integration', () => {
  beforeEach(() => {
    // Reset fetch mock before each test
    global.fetch = vi.fn();
  });

  afterEach(() => {
    vi.restoreAllMocks();
  });

  it('should make POST request to /api/v1/producers with correct payload', async () => {
    // Mock successful response (202 Accepted)
    const mockResponse = {
      ok: true,
      status: 202,
      json: vi.fn().mockResolvedValue({
        id: 'f36e54fa-ce8b-5498-9713-c231236ef2e8',
        producerCode: 'FRLA1',
        name: 'Universal Music France',
        tracks: ['FRLA12400001']
      })
    };
    
    (global.fetch as any).mockResolvedValue(mockResponse);

    // Call the function
    const result = await registerTrack('FRLA12400001');

    // Verify the request was made correctly
    expect(fetch).toHaveBeenCalledWith('/api/v1/producers', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ isrc: 'FRLA12400001' }),
    });

    // Verify the response is handled correctly
    expect(result.ok).toBe(true);
    expect(result.status).toBe(202);
    expect(result.data).toEqual({
      id: 'f36e54fa-ce8b-5498-9713-c231236ef2e8',
      producerCode: 'FRLA1',
      name: 'Universal Music France',
      tracks: ['FRLA12400001']
    });
  });

  it('should handle 404 errors gracefully', async () => {
    // Mock 404 response (simulating the bug scenario)
    const mockResponse = {
      ok: false,
      status: 404,
      json: vi.fn()
    };
    
    (global.fetch as any).mockResolvedValue(mockResponse);

    const result = await registerTrack('FRLA12400001');

    expect(result.ok).toBe(false);
    expect(result.status).toBe(404);
  });

  it('should handle 400 Bad Request errors', async () => {
    const mockResponse = {
      ok: false,
      status: 400,
      json: vi.fn()
    };
    
    (global.fetch as any).mockResolvedValue(mockResponse);

    const result = await registerTrack('INVALID');

    expect(result.ok).toBe(false);
    expect(result.status).toBe(400);
  });

  it('should handle 422 Unprocessable Entity errors', async () => {
    const mockResponse = {
      ok: false,
      status: 422,
      json: vi.fn()
    };
    
    (global.fetch as any).mockResolvedValue(mockResponse);

    const result = await registerTrack('FRLA12400001');

    expect(result.ok).toBe(false);
    expect(result.status).toBe(422);
  });
});