import { clsx, type ClassValue } from "clsx"
import { twMerge } from "tailwind-merge"

export function cn(...inputs: ClassValue[]) {
  return twMerge(clsx(inputs))
}

export interface HttpResult<T> {
  ok: boolean;
  status: number;
  data?: T;
}

export interface ProducerDto {
  id: string;
  producerCode: string;
  name?: string | null;
  tracks: Array<unknown>;
}

export async function registerTrack(isrc: string): Promise<HttpResult<ProducerDto>> {
  const response = await fetch("/api/v1/producers", {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify({ isrc }),
  })

  if (response.status === 202) {
    try {
      const data = (await response.json()) as ProducerDto
      return { ok: true, status: 202, data }
    } catch {
      return { ok: true, status: 202 }
    }
  }

  return { ok: false, status: response.status }
}
