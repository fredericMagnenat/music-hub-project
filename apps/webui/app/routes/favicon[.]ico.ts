import type { LoaderFunctionArgs } from "@remix-run/node";

export async function loader(_args: LoaderFunctionArgs) {
  // No favicon file yet; return 204 to avoid 404 noise in logs
  return new Response(null, { status: 204 });
}
