// utils/GetPlaidUpdateForAccount.ts
import { GetCsrf } from "./GetCSRF";

export async function GetPlaidUpdate(accountId: string): Promise<string> {
  const csrf = await GetCsrf();

  const res = await fetch("/api/plaid/create-update-token", {
    method: "POST",
    credentials: "include",
    headers: {
      "Content-Type": "application/json",
      ...csrf,
    },
    body: JSON.stringify({ accountId }),
  });

  if (!res.ok) {
    const body = await res.text().catch(() => "");
    throw new Error(`create-update-token failed: ${res.status} ${body}`);
  }

  // Controller should return { link_token: "..." }
  const data = await res.json();
  const token = data?.link_token ?? data?.linkToken ?? data; // tolerate shapes during dev
  if (!token || typeof token !== "string") {
    throw new Error("No link_token in response");
  }
  return token;
}
