import { render, screen } from '@testing-library/react';
import { describe, it, expect } from 'vitest';
import { RecentTracksList, type RecentTrackItem } from './RecentTracksList';

const mockTracks: RecentTrackItem[] = [
  {
    id: '1',
    title: 'Test Track 1',
    artists: ['Artist 1'],
    isrc: 'FRLA12400001',
    status: 'Verified'
  },
  {
    id: '2', 
    title: 'Test Track 2',
    artists: ['Artist 2', 'Artist 3'],
    isrc: 'USRC17607839',
    status: 'Provisional'
  }
];

describe('RecentTracksList', () => {
  describe('empty state', () => {
    it('displays empty state when no tracks provided', () => {
      render(<RecentTracksList tracks={[]} />);
      
      const emptyState = screen.getByTestId('empty-state');
      expect(emptyState).toBeInTheDocument();
      expect(emptyState).toHaveTextContent('No recent tracks yet. Tracks you validate will appear here.');
      expect(emptyState).toHaveAttribute('role', 'status');
      expect(emptyState).toHaveAttribute('aria-live', 'polite');
    });
  });

  describe('with 1 track', () => {
    it('renders single track with all required information', () => {
      render(<RecentTracksList tracks={[mockTracks[0]]} />);
      
      const trackList = screen.getByRole('list', { name: 'Recent Tracks' });
      expect(trackList).toBeInTheDocument();
      
      const trackItem = screen.getByRole('listitem');
      expect(trackItem).toBeInTheDocument();
      expect(trackItem).toHaveAttribute('tabIndex', '0');
      
      expect(screen.getByText('Test Track 1')).toBeInTheDocument();
      expect(screen.getByText('Artist 1')).toBeInTheDocument();
      expect(screen.getByText('ISRC: FRLA12400001')).toBeInTheDocument();
      expect(screen.getByText('Verified')).toBeInTheDocument();
    });
  });

  describe('with 10 tracks', () => {
    const tenTracks: RecentTrackItem[] = Array.from({ length: 10 }, (_, i) => ({
      id: `${i + 1}`,
      title: `Track ${i + 1}`,
      artists: [`Artist ${i + 1}`],
      isrc: `TEST${String(i + 1).padStart(8, '0')}`,
      status: i % 2 === 0 ? 'Verified' : 'Provisional' as const
    }));

    it('renders all 10 tracks', () => {
      render(<RecentTracksList tracks={tenTracks} />);
      
      const trackItems = screen.getAllByRole('listitem');
      expect(trackItems).toHaveLength(10);
      
      // Check first and last tracks are rendered
      expect(screen.getByText('Track 1')).toBeInTheDocument();
      expect(screen.getByText('Track 10')).toBeInTheDocument();
    });

    it('limits display to maximum 10 items even with more tracks', () => {
      const elevenTracks = [...tenTracks, {
        id: '11',
        title: 'Track 11',
        artists: ['Artist 11'],
        isrc: 'TEST00000011',
        status: 'Verified' as const
      }];
      
      render(<RecentTracksList tracks={elevenTracks} />);
      
      const trackItems = screen.getAllByRole('listitem');
      expect(trackItems).toHaveLength(10);
      
      // Verify 11th track is not rendered
      expect(screen.queryByText('Track 11')).not.toBeInTheDocument();
    });
  });

  describe('accessibility', () => {
    it('has proper ARIA labels and roles', () => {
      render(<RecentTracksList tracks={mockTracks} />);
      
      const trackList = screen.getByRole('list', { name: 'Recent Tracks' });
      expect(trackList).toBeInTheDocument();
      
      const trackItems = screen.getAllByRole('listitem');
      trackItems.forEach((item, index) => {
        expect(item).toHaveAttribute('tabIndex', '0');
        expect(item).toHaveAttribute('aria-describedby', `track-${mockTracks[index].id}-info`);
      });
    });

    it('provides accessible track information', () => {
      render(<RecentTracksList tracks={mockTracks} />);
      
      // Check track info sections exist with proper IDs
      expect(document.querySelector('#track-1-info')).toBeInTheDocument();
      expect(document.querySelector('#track-2-info')).toBeInTheDocument();
    });

    it('supports keyboard navigation', () => {
      render(<RecentTracksList tracks={mockTracks} />);
      
      const trackItems = screen.getAllByRole('listitem');
      trackItems.forEach(item => {
        expect(item).toHaveAttribute('tabIndex', '0');
      });
    });
  });

  describe('multiple artists handling', () => {
    it('displays multiple artists correctly', () => {
      render(<RecentTracksList tracks={[mockTracks[1]]} />);
      
      expect(screen.getByText('Artist 2, Artist 3')).toBeInTheDocument();
    });

    it('sets correct title attribute for truncated artists', () => {
      render(<RecentTracksList tracks={[mockTracks[1]]} />);
      
      const artistElement = screen.getByText('Artist 2, Artist 3');
      expect(artistElement).toHaveAttribute('title', 'Artist 2, Artist 3');
    });
  });

  describe('responsive layout', () => {
    it('applies responsive grid classes', () => {
      render(<RecentTracksList tracks={mockTracks} />);
      
      const trackList = screen.getByRole('list');
      expect(trackList).toHaveClass('space-y-3', 'md:grid', 'md:grid-cols-2', 'md:gap-4', 'md:space-y-0');
    });
  });
});