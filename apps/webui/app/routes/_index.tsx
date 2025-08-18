import type { MetaFunction } from "@remix-run/node";
import { useCallback, useMemo, useState } from "react";
import { registerTrack } from "~/lib/utils";
import { Input } from "~/components/ui/input";
import { Button } from "~/components/ui/button";
import { useToast } from "~/components/ui/toast";
import { StatusBadge } from "~/components/ui/status-badge";

export const meta: MetaFunction = () => {
  return [
    { title: "music-data-hub" },
    { name: "description", content: "Validate ISRC and register track" },
  ];
};

function normalizeISRC(input: string): string {
  if (!input) return "";
  return input.toUpperCase().replace(/[^A-Z0-9]/g, "").trim();
}

function isValidISRC(input: string): boolean {
  // ISRC format: 2 letters (country) + 3 alnum (registrant) + 2 digits (year) + 5 digits (designation)
  // After normalization (uppercase, remove dashes/spaces): 12 chars total, regex:
  // ^[A-Z]{2}[A-Z0-9]{3}\d{7}$
  const normalized = normalizeISRC(input);
  return /^[A-Z]{2}[A-Z0-9]{3}\d{7}$/.test(normalized);
}

type TrackStatus = "Provisional" | "Verified";

interface RecentTrackItem {
  id: string;
  title: string;
  artists: string[];
  isrc: string;
  status: TrackStatus;
}

export default function Index() {
  const [rawIsrc, setRawIsrc] = useState("");
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [message, setMessage] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);

  const normalized = useMemo(() => normalizeISRC(rawIsrc), [rawIsrc]);
  const valid = useMemo(() => isValidISRC(rawIsrc), [rawIsrc]);

  const { toast } = useToast();

  const onSubmit = useCallback(async () => {
    setMessage(null);
    setError(null);
    setIsSubmitting(true);
    try {
      const result = await registerTrack(normalized);
      if (result.ok) {
        setMessage("Accepted (202): track registration in progress.");
        if (result.trackInfo) {
          toast({
            title: "Track Registered Successfully",
            description: `Track '${result.trackInfo.title}' by '${result.trackInfo.artists}' has been successfully registered and is being processed.`,
            variant: "success",
            duration: 5000,
          });
        } else {
          toast({
            title: "Track Registered",
            description: "Track registration accepted and is being processed.",
            variant: "success",
            duration: 5000,
          });
        }
      } else if (result.status === 400) {
        const errorMsg = "Invalid ISRC format (400). Please check and try again.";
        setError(errorMsg);
        toast({
          title: "Invalid ISRC Format",
          description: "Please check your ISRC format and try again.",
          variant: "destructive",
          duration: 5000,
        });
      } else if (result.status === 422) {
        setError("ISRC valid but unresolvable upstream (422).");
        toast({
          title: "External Service Error",
          description: result.message || "The ISRC was valid, but we could not find metadata for it on external services.",
          variant: "destructive",
          duration: 7000,
        });
      } else {
        const errorMsg = `Request failed (${result.status}).`;
        setError(errorMsg);
        toast({
          title: "Request Failed",
          description: `Server responded with status ${result.status}. Please try again later.`,
          variant: "destructive",
          duration: 5000,
        });
      }
    } catch (err) {
      const errorMsg = "Network or server error. Please try again later.";
      setError(errorMsg);
      toast({
        title: "Network Error",
        description: "Unable to connect to server. Please check your connection and try again.",
        variant: "destructive",
        duration: 5000,
      });
    } finally {
      setIsSubmitting(false);
    }
  }, [normalized, toast]);

  // Placeholder recent tracks (replace with data when available)
  const recentTracks: RecentTrackItem[] = useMemo(
    () => [
      { id: "1", title: "Midnight Echoes", artists: ["N. Rivera"], isrc: "FRLA12400001", status: "Verified" },
      { id: "2", title: "Cloud Runner", artists: ["Ada Fox", "Miles K."], isrc: "USRC17607839", status: "Provisional" },
      { id: "3", title: "Neon Garden", artists: ["The Lumen"], isrc: "GBAYE6800011", status: "Verified" },
      { id: "4", title: "Low Tide", artists: ["Élodie"], isrc: "FRZAA2400123", status: "Provisional" },
      { id: "5", title: "Paper Planes", artists: ["Quiet Parade"], isrc: "USQX91501234", status: "Verified" },
      { id: "6", title: "Afterglow", artists: ["K. Tanaka"], isrc: "JPZ123400567", status: "Provisional" },
    ],
    []
  );

  return (
    <div className="min-h-dvh font-sans antialiased">
      <div className="mx-auto max-w-xl p-6 md:max-w-3xl">
        <h1 className="text-2xl font-bold mb-6">music-data-hub</h1>
        <div className="mb-3">
          <label htmlFor="isrc" className="block text-sm font-medium mb-1">
            ISRC
          </label>
          <Input
            id="isrc"
            name="isrc"
            type="text"
            value={rawIsrc}
            onChange={(e) => setRawIsrc(e.target.value)}
            placeholder="FR-LA1-24-00001 or FRLA12400001"
            aria-invalid={!valid && rawIsrc.length > 0}
            aria-describedby="isrc-help"
            className="focus-visible:ring-[var(--hub-primary)]"
          />
          <p id="isrc-help" className="mt-1 text-xs text-gray-600">
            Normalized: <span className="font-mono">{normalized || "—"}</span>
          </p>
          {!valid && rawIsrc.length > 0 && (
            <p className="mt-1 text-sm text-red-600">
              Please enter a valid 12-character ISRC (e.g., FRLA12400001).
            </p>
          )}
        </div>

        <Button
          type="button"
          onClick={onSubmit}
          disabled={!valid || isSubmitting}
          className="focus-visible:ring-[var(--hub-primary)]"
        >
          {isSubmitting ? (
            <span className="inline-flex items-center gap-2">
              <span className="h-4 w-4 animate-spin rounded-full border-2 border-white/40 border-t-white" />
              Validate
            </span>
          ) : (
            "Validate"
          )}
        </Button>

        {message && (
          <div className="mt-3 rounded border border-green-200 bg-green-50 p-3 text-green-800">
            {message}
          </div>
        )}
        {error && (
          <div className="mt-3 rounded border border-red-200 bg-red-50 p-3 text-red-800">
            {error}
          </div>
        )}

        <div className="mt-8">
          <h3 className="text-lg font-semibold mb-3">Recent Tracks</h3>
          {recentTracks.length === 0 ? (
            <div
              className="rounded-md border border-gray-200 bg-gray-50 p-4 text-sm text-gray-700"
              role="status"
              aria-live="polite"
            >
              No recent tracks yet. Tracks you validate will appear here.
            </div>
          ) : (
            <ul className="space-y-3 md:grid md:grid-cols-2 md:gap-4 md:space-y-0" aria-label="Recent Tracks">
              {recentTracks.slice(0, 10).map((t) => (
                <li key={t.id} className="rounded-md border border-gray-200 bg-white p-3 shadow-sm">
                  <div className="flex items-start justify-between gap-3">
                    <div className="min-w-0">
                      <p className="truncate text-sm font-medium text-gray-900" title={t.title}>
                        {t.title}
                      </p>
                      <p className="truncate text-sm text-gray-600" title={t.artists.join(", ")}>
                        {t.artists.join(", ")}
                      </p>
                      <p className="mt-1 font-mono text-xs text-gray-500">ISRC: {t.isrc}</p>
                    </div>
                    <StatusBadge status={t.status} />
                  </div>
                </li>
              ))}
            </ul>
          )}
        </div>
      </div>
    </div>
  );
}