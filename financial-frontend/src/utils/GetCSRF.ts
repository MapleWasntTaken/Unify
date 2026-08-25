let csrfToken: string | null = null;
let csrfHeader: string | null = null;
let refreshInFlight: Promise<void> | null = null;

type ApiError = { code?: string; message?: string; requestId?: string };

export async function loadCsrfToken(force = false): Promise<void> {
  if (!force && csrfToken && csrfHeader) return;
  if (refreshInFlight) return refreshInFlight;
  refreshInFlight = (async () => {
    const response = await fetch("/api/csrf-token", { credentials: "include" });
    if (!response.ok) throw new Error(`Unable to obtain CSRF token: ${response.status}`);
    const data = await response.json();
    if (!data.token || !data.headerName) throw new Error("CSRF response omitted required fields");
    csrfToken = data.token;
    csrfHeader = data.headerName;
  })();
  try {
    await refreshInFlight;
  } finally {
    refreshInFlight = null;
  }
}

export async function GetCsrf(): Promise<Record<string, string>> {
  await loadCsrfToken();
  return { [csrfHeader!]: csrfToken! };
}

export function WipeCSRF(): void {
  csrfToken = null;
  csrfHeader = null;
}

export async function FetchWithCsrf(input: RequestInfo | URL, init: RequestInit = {}): Promise<Response> {
  const firstResponse = await execute(input, init);
  const firstError = await readApiError(firstResponse);
  if (!isCsrfError(firstResponse, firstError)) return firstResponse;

  WipeCSRF();
  await loadCsrfToken(true);
  const retryResponse = await execute(input, init);
  const retryError = await readApiError(retryResponse);
  if (isCsrfError(retryResponse, retryError)) {
    window.alert("Your security session expired. Please refresh the page and try again.");
  }
  return retryResponse;
}

async function execute(input: RequestInfo | URL, init: RequestInit): Promise<Response> {
  const csrf = await GetCsrf();
  const headers = new Headers(init.headers);
  for (const [name, value] of Object.entries(csrf)) headers.set(name, value);
  return fetch(input, { ...init, headers, credentials: "include" });
}

async function readApiError(response: Response): Promise<ApiError | null> {
  if (response.status !== 403) return null;
  try {
    return await response.clone().json();
  } catch {
    return null;
  }
}

function isCsrfError(response: Response, error: ApiError | null): boolean {
  return response.status === 403 &&
    (error?.code === "CSRF_TOKEN_INVALID" || error?.code === "CSRF_TOKEN_MISSING");
}
