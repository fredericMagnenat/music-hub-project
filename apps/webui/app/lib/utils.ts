import { clsx, type ClassValue } from "clsx"
import { twMerge } from "tailwind-merge"
import { RecentTracksApiResponse } from "@repo/shared-types"

export function cn(...inputs: ClassValue[]) {
  return twMerge(clsx(inputs))
}

export interface TrackInfo {
  title: string;
  artists: string;
}

export interface HttpResult<T> {
  ok: boolean;
  status: number;
  data?: T;
  error?: string;
  message?: string;
  trackInfo?: TrackInfo;
}

export interface TrackDto {
  title: string;
  artists: string[];
  isrc: string;
  status?: string;
}

export interface ProducerDto {
  id: string;
  producerCode: string;
  name?: string | null;
  tracks: TrackDto[];
}

function extractTrackInfo(data: ProducerDto): TrackInfo | undefined {
  if (!data.tracks || data.tracks.length === 0) return undefined;
  
  const track = data.tracks[0]; // Get the first/latest track
  if (!track.title || !track.artists || track.artists.length === 0) return undefined;
  
  return {
    title: track.title,
    artists: track.artists.join(", ")
  };
}

export async function registerTrack(isrc: string): Promise<HttpResult<ProducerDto>> {
  const response = await fetch("/api/v1/producers", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({ isrc }),
  })

  if (response.ok) {
    const data = await response.json() as ProducerDto;
    return { 
      ok: true, 
      status: response.status, 
      data,
      trackInfo: extractTrackInfo(data)
    };
  } else {
    try {
      const errorData = await response.json();
      return { 
        ok: false, 
        status: response.status, 
        error: errorData.error,
        message: errorData.message 
      };
    } catch {
      return { 
        ok: false, 
        status: response.status 
      };
    }
  }
}

export async function getRecentTracks(): Promise<HttpResult<RecentTracksApiResponse>> {
  try {
    const response = await fetch("/api/v1/tracks/recent", {
      method: "GET",
      headers: {
        "Accept": "application/json",
      },
    });

    if (response.ok) {
      const data = await response.json() as RecentTracksApiResponse;
      return {
        ok: true,
        status: response.status,
        data
      };
    } else {
      try {
        const errorData = await response.json();
        return {
          ok: false,
          status: response.status,
          error: errorData.error,
          message: errorData.message
        };
      } catch {
        return {
          ok: false,
          status: response.status,
          error: "NetworkError",
          message: "Failed to fetch recent tracks"
        };
      }
    }
  } catch (error) {
    return {
      ok: false,
      status: 0,
      error: "NetworkError",
      message: error instanceof Error ? error.message : "Network error occurred"
    };
  }
}
