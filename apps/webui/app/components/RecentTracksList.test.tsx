import { render, screen, waitFor } from '@testing-library/react';
import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest';
import { RecentTracksList } from './RecentTracksList';
import * as utils from '~/lib/utils';
import { RecentTrackResponse } from '@repo/shared-types';

// Mock the utils module
vi.mock('~/lib/utils', async (importOriginal) => {
  const actual = await importOriginal<typeof import('~/lib/utils')>();
  return {
    ...actual,
    getRecentTracks: vi.fn()
  };
});

const mockTracks: RecentTrackResponse[] = [
  {
    id: '1',
    isrc: 'FRLA12400001',
    title: 'Test Track 1',
    artistNames: ['Artist 1'],
    source: { name: 'TIDAL', externalId: 'ext-1' },
    status: 'VALIDATED',
    submissionDate: '2025-08-25T10:30:00Z',
    producer: { id: 'prod-1', producerCode: 'FRLA1', name: 'Producer 1' }
  },
  {
    id: '2',
    isrc: 'USRC17607839',
    title: 'Test Track 2',
    artistNames: ['Artist 2', 'Artist 3'],
    source: { name: 'TIDAL', externalId: 'ext-2' },
    status: 'PROVISIONAL',
    submissionDate: '2025-08-25T09:30:00Z',
    producer: { id: 'prod-2', producerCode: 'USRC1', name: 'Producer 2' }
  }
];

describe('RecentTracksList', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  afterEach(() => {
    vi.resetAllMocks();
  });

  describe('loading state', () => {
    it('displays loading state initially', async () => {
      const mockGetRecentTracks = vi.mocked(utils.getRecentTracks);
      mockGetRecentTracks.mockImplementation(() => new Promise(() => {})); // Never resolves

      render(<RecentTracksList />);
      
      const loadingState = screen.getByTestId('loading-state');
      expect(loadingState).toBeInTheDocument();
      expect(loadingState).toHaveTextContent('Loading recent tracks...');
      expect(loadingState).toHaveAttribute('role', 'status');
      expect(loadingState).toHaveAttribute('aria-live', 'polite');
    });
  });

  describe('error state', () => {
    it('displays error state when API call fails', async () => {
      const mockGetRecentTracks = vi.mocked(utils.getRecentTracks);
      mockGetRecentTracks.mockResolvedValue({
        ok: false,
        status: 500,
        error: 'InternalError',
        message: 'Failed to load recent tracks'
      });

      render(<RecentTracksList />);
      
      await waitFor(() => {
        const errorState = screen.getByTestId('error-state');
        expect(errorState).toBeInTheDocument();
        expect(errorState).toHaveTextContent('Failed to load recent tracks');
        expect(errorState).toHaveAttribute('role', 'alert');
      });
    });

    it('displays retry button in error state', async () => {
      const mockGetRecentTracks = vi.mocked(utils.getRecentTracks);
      mockGetRecentTracks.mockResolvedValue({
        ok: false,
        status: 500,
        error: 'NetworkError',
        message: 'Failed to load recent tracks'
      });

      render(<RecentTracksList />);
      
      await waitFor(() => {
        const retryButton = screen.getByRole('button', { name: 'Retry loading recent tracks' });
        expect(retryButton).toBeInTheDocument();
      });
    });
  });

  describe('empty state', () => {
    it('displays empty state when no tracks are returned', async () => {
      const mockGetRecentTracks = vi.mocked(utils.getRecentTracks);
      mockGetRecentTracks.mockResolvedValue({
        ok: true,
        status: 200,
        data: []
      });

      render(<RecentTracksList />);
      
      await waitFor(() => {
        const emptyState = screen.getByTestId('empty-state');
        expect(emptyState).toBeInTheDocument();
        expect(emptyState).toHaveTextContent('No recent tracks yet. Tracks you validate will appear here.');
        expect(emptyState).toHaveAttribute('role', 'status');
        expect(emptyState).toHaveAttribute('aria-live', 'polite');
      });
    });
  });

  describe('success state', () => {
    it('renders tracks when API call succeeds', async () => {
      const mockGetRecentTracks = vi.mocked(utils.getRecentTracks);
      mockGetRecentTracks.mockResolvedValue({
        ok: true,
        status: 200,
        data: mockTracks
      });

      render(<RecentTracksList />);
      
      await waitFor(() => {
        const trackList = screen.getByRole('list', { name: 'Recent Tracks' });
        expect(trackList).toBeInTheDocument();
      });

      const trackItems = screen.getAllByRole('listitem');
      expect(trackItems).toHaveLength(2);
      
      // Check first track
      expect(screen.getByText('Test Track 1')).toBeInTheDocument();
      expect(screen.getByText('Artist 1')).toBeInTheDocument();
      expect(screen.getByText('ISRC: FRLA12400001')).toBeInTheDocument();
      
      // Check second track
      expect(screen.getByText('Test Track 2')).toBeInTheDocument();
      expect(screen.getByText('Artist 2, Artist 3')).toBeInTheDocument();
      expect(screen.getByText('ISRC: USRC17607839')).toBeInTheDocument();
    });

    it('maps track status correctly', async () => {
      const mockGetRecentTracks = vi.mocked(utils.getRecentTracks);
      mockGetRecentTracks.mockResolvedValue({
        ok: true,
        status: 200,
        data: mockTracks
      });

      render(<RecentTracksList />);
      
      await waitFor(() => {
        // VALIDATED should map to "Verified"
        expect(screen.getByText('Verified')).toBeInTheDocument();
        
        // PROVISIONAL should map to "Provisional"
        expect(screen.getByText('Provisional')).toBeInTheDocument();
      });
    });

    it('limits display to 10 tracks maximum', async () => {
      const manyTracks = Array.from({ length: 15 }, (_, i) => ({
        ...mockTracks[0],
        id: `track-${i}`,
        isrc: `FRLA1240${String(i).padStart(4, '0')}`,
        title: `Track ${i + 1}`
      }));

      const mockGetRecentTracks = vi.mocked(utils.getRecentTracks);
      mockGetRecentTracks.mockResolvedValue({
        ok: true,
        status: 200,
        data: manyTracks
      });

      render(<RecentTracksList />);
      
      await waitFor(() => {
        const trackItems = screen.getAllByRole('listitem');
        expect(trackItems).toHaveLength(10);
      });
    });
  });

  describe('accessibility', () => {
    it('has proper ARIA labels and roles', async () => {
      const mockGetRecentTracks = vi.mocked(utils.getRecentTracks);
      mockGetRecentTracks.mockResolvedValue({
        ok: true,
        status: 200,
        data: mockTracks
      });

      render(<RecentTracksList />);
      
      await waitFor(() => {
        const trackList = screen.getByRole('list', { name: 'Recent Tracks' });
        expect(trackList).toBeInTheDocument();
        
        const trackItems = screen.getAllByRole('listitem');
        trackItems.forEach((item, index) => {
          expect(item).toHaveAttribute('tabIndex', '0');
          expect(item).toHaveAttribute('aria-describedby', `track-${mockTracks[index].id}-info`);
        });
      });
    });

    it('provides accessible track information', async () => {
      const mockGetRecentTracks = vi.mocked(utils.getRecentTracks);
      mockGetRecentTracks.mockResolvedValue({
        ok: true,
        status: 200,
        data: mockTracks
      });

      render(<RecentTracksList />);
      
      await waitFor(() => {
        // Check track info sections exist with proper IDs
        expect(document.querySelector('#track-1-info')).toBeInTheDocument();
        expect(document.querySelector('#track-2-info')).toBeInTheDocument();
      });
    });
  });

  describe('API integration', () => {
    it('calls getRecentTracks on component mount', () => {
      const mockGetRecentTracks = vi.mocked(utils.getRecentTracks);
      mockGetRecentTracks.mockResolvedValue({
        ok: true,
        status: 200,
        data: []
      });

      render(<RecentTracksList />);
      
      expect(mockGetRecentTracks).toHaveBeenCalledTimes(1);
    });

    it('calls getRecentTracks again when retry button is clicked', async () => {
      const mockGetRecentTracks = vi.mocked(utils.getRecentTracks);
      mockGetRecentTracks.mockResolvedValue({
        ok: false,
        status: 500,
        message: 'Server error'
      });

      render(<RecentTracksList />);
      
      await waitFor(() => {
        const retryButton = screen.getByRole('button', { name: 'Retry loading recent tracks' });
        retryButton.click();
      });

      expect(mockGetRecentTracks).toHaveBeenCalledTimes(2);
    });
  });
});