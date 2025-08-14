import type { MetaFunction } from "@remix-run/node";
import { useCallback, useMemo, useState } from "react";
import { registerTrack } from "~/lib/utils";

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

export default function Index() {
  const [rawIsrc, setRawIsrc] = useState("");
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [message, setMessage] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);

  const normalized = useMemo(() => normalizeISRC(rawIsrc), [rawIsrc]);
  const valid = useMemo(() => isValidISRC(rawIsrc), [rawIsrc]);

  const onSubmit = useCallback(async () => {
    setMessage(null);
    setError(null);
    setIsSubmitting(true);
    try {
      const result = await registerTrack(normalized);
      if (result.ok) {
        setMessage("Accepted (202): track registration in progress.");
      } else if (result.status === 400) {
        setError("Invalid ISRC format (400). Please check and try again.");
      } else if (result.status === 422) {
        setError("ISRC valid but unresolvable upstream (422).");
      } else {
        setError(`Request failed (${result.status}).`);
      }
    } catch (err) {
      setError("Network or server error. Please try again later.");
    } finally {
      setIsSubmitting(false);
    }
  }, [normalized]);

  return (
    <div className="min-h-dvh font-sans antialiased p-6">
      <div className="mx-auto max-w-xl">
        <h1 className="text-2xl font-bold mb-4">music-data-hub</h1>
        <div className="mb-2">
          <label htmlFor="isrc" className="block text-sm font-medium mb-1">
            ISRC
          </label>
          <input
            id="isrc"
            name="isrc"
            type="text"
            value={rawIsrc}
            onChange={(e) => setRawIsrc(e.target.value)}
            placeholder="FR-LA1-24-00001 or FRLA12400001"
            className="w-full rounded border px-3 py-2 outline-none focus:ring-2 focus:ring-blue-500"
            aria-invalid={!valid && rawIsrc.length > 0}
            aria-describedby="isrc-help"
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

        <button
          type="button"
          onClick={onSubmit}
          disabled={!valid || isSubmitting}
          className="inline-flex items-center gap-2 rounded bg-blue-600 px-4 py-2 text-white disabled:opacity-50"
        >
          {isSubmitting ? (
            <span className="inline-flex items-center gap-2">
              <span className="h-4 w-4 animate-spin rounded-full border-2 border-white/40 border-t-white" />
              Validate
            </span>
          ) : (
            "Validate"
          )}
        </button>

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
      </div>
    </div>
  );
}