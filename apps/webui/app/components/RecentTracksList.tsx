import React from "react";
import { StatusBadge } from "~/components/ui/status-badge";

export type TrackStatus = "Provisional" | "Verified";

export interface RecentTrackItem {
  id: string;
  title: string;
  artists: string[];
  isrc: string;
  status: TrackStatus;
}

export interface RecentTracksListProps {
  tracks: RecentTrackItem[];
}

export function RecentTracksList({ tracks }: RecentTracksListProps) {
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
              <p className="truncate text-sm text-gray-600" title={track.artists.join(", ")}>
                {track.artists.join(", ")}
              </p>
              <p className="mt-1 font-mono text-xs text-gray-500">ISRC: {track.isrc}</p>
            </div>
            <StatusBadge status={track.status} />
          </div>
        </li>
      ))}
    </ul>
  );
}