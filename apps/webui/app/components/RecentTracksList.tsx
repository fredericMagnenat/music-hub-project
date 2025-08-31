import React, { useEffect, useState } from "react";
import { StatusBadge } from "~/components/ui/status-badge";
import { getRecentTracks } from "~/lib/utils";
import { RecentTracksApiResponse, RecentTrackResponse } from "@repo/shared-types";

export type TrackStatus = "PROVISIONAL" | "VALIDATED" | "REJECTED";

export interface RecentTracksListProps {
  // No props needed - component will fetch its own data
}

export function RecentTracksList({ }: RecentTracksListProps) {
  const [tracks, setTracks] = useState<RecentTrackResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const fetchTracks = async () => {
    setLoading(true);
    setError(null);
    
    try {
      const result = await getRecentTracks();
      
      if (result.ok && result.data) {
        setTracks(result.data);
      } else {
        setError(result.message || "Failed to load recent tracks");
      }
    } catch (err) {
      setError("Failed to load recent tracks");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchTracks();
  }, []);

  if (loading) {
    return (
      <div
        className="rounded-md border border-gray-200 bg-gray-50 p-4 text-sm text-gray-700"
        role="status"
        aria-live="polite"
        data-testid="loading-state"
      >
        <div className="flex items-center gap-2">
          <span className="h-4 w-4 animate-spin rounded-full border-2 border-gray-400 border-t-gray-700" />
          Loading recent tracks...
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div
        className="rounded-md border border-red-200 bg-red-50 p-4 text-sm text-red-700"
        role="alert"
        data-testid="error-state"
      >
        <div className="flex items-center justify-between">
          <span>{error}</span>
          <button 
            onClick={fetchTracks}
            className="text-red-600 underline hover:text-red-800 focus:outline-none focus:ring-2 focus:ring-red-500 focus:ring-offset-1 rounded"
            aria-label="Retry loading recent tracks"
          >
            Retry
          </button>
        </div>
      </div>
    );
  }

  if (tracks.length === 0) {
    return (
      <div
        className="rounded-md border border-gray-200 bg-gray-50 p-4 text-sm text-gray-700"
        role="status"
        aria-live="polite"
        data-testid="empty-state"
      >
        No recent tracks yet. Tracks you validate will appear here.
      </div>
    );
  }

  // Convert status to match StatusBadge component
  const mapStatus = (status: string): "Provisional" | "Verified" => {
    switch (status) {
      case "VALIDATED":
        return "Verified";
      case "PROVISIONAL":
      case "REJECTED":
      default:
        return "Provisional";
    }
  };

  return (
    <ul className="space-y-3 md:grid md:grid-cols-2 md:gap-4 md:space-y-0" aria-label="Recent Tracks">
      {tracks.slice(0, 10).map((track, index) => (
        <li 
          key={track.id} 
          className="rounded-md border border-gray-200 bg-white p-3 shadow-sm"
          tabIndex={0}
          role="listitem"
          aria-describedby={`track-${track.id}-info`}
        >
          <div className="flex items-start justify-between gap-3">
            <div className="min-w-0" id={`track-${track.id}-info`}>
              <p className="truncate text-sm font-medium text-gray-900" title={track.title}>
                {track.title}
              </p>
              <p className="truncate text-sm text-gray-600" title={track.artistNames.join(", ")}>
                {track.artistNames.join(", ")}
              </p>
              <p className="mt-1 font-mono text-xs text-gray-500">ISRC: {track.isrc}</p>
            </div>
            <StatusBadge status={mapStatus(track.status)} />
          </div>
        </li>
      ))}
    </ul>
  );
}