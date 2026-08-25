import { beforeEach, describe, expect, it, vi } from "vitest";

describe("FetchWithCsrf", () => {
  beforeEach(() => {
    vi.resetModules();
    vi.restoreAllMocks();
    vi.stubGlobal("window", { alert: vi.fn() });
  });

  it("refreshes a rejected CSRF token and retries exactly once", async () => {
    const fetchMock = vi.fn()
      .mockResolvedValueOnce(json({ token: "old", headerName: "X-CSRF-TOKEN" }))
      .mockResolvedValueOnce(json({ code: "CSRF_TOKEN_INVALID" }, 403))
      .mockResolvedValueOnce(json({ token: "new", headerName: "X-CSRF-TOKEN" }))
      .mockResolvedValueOnce(json({ status: "ok" }));
    vi.stubGlobal("fetch", fetchMock);

    const { FetchWithCsrf } = await import("../utils/GetCSRF");
    const response = await FetchWithCsrf("/api/example", { method: "POST" });

    expect(response.status).toBe(200);
    expect(fetchMock).toHaveBeenCalledTimes(4);
    const retriedHeaders = fetchMock.mock.calls[3][1].headers as Headers;
    expect(retriedHeaders.get("X-CSRF-TOKEN")).toBe("new");
    expect(window.alert).not.toHaveBeenCalled();
  });

  it("returns the second failure and alerts without looping", async () => {
    const fetchMock = vi.fn()
      .mockResolvedValueOnce(json({ token: "old", headerName: "X-CSRF-TOKEN" }))
      .mockResolvedValueOnce(json({ code: "CSRF_TOKEN_MISSING" }, 403))
      .mockResolvedValueOnce(json({ token: "new", headerName: "X-CSRF-TOKEN" }))
      .mockResolvedValueOnce(json({ code: "CSRF_TOKEN_INVALID" }, 403));
    vi.stubGlobal("fetch", fetchMock);

    const { FetchWithCsrf } = await import("../utils/GetCSRF");
    const response = await FetchWithCsrf("/api/example", { method: "POST" });

    expect(response.status).toBe(403);
    expect(fetchMock).toHaveBeenCalledTimes(4);
    expect(window.alert).toHaveBeenCalledTimes(1);
  });
});

function json(body: object, status = 200): Response {
  return new Response(JSON.stringify(body), {
    status,
    headers: { "Content-Type": "application/json" },
  });
}
