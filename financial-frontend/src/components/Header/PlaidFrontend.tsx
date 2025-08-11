import { useEffect, useState } from "react";
import { usePlaidLink } from "react-plaid-link";
import type {
  PlaidLinkOnSuccessMetadata,
} from "react-plaid-link";
import { GetCsrf } from "../../utils/GetCSRF";
import { GetBankDetails, ResetBankDataCache } from "../../utils/GetBankData";

export function PlaidFrontend({ onClose }: { onClose: () => void }) {
  const [csrf, setCsrf] = useState<Record<string, string> | null>(null);
  const [linkToken, setLinkToken] = useState<string | null>(null);

  // Grab CSRF token
  useEffect(() => {
    GetCsrf().then(setCsrf);
  }, []);

  // Fetch Plaid link token
  useEffect(() => {
    if (!csrf) return;

    const fetchLinkToken = async () => {
      try {
        const res = await fetch("/api/plaid/create-link-token", {
          method: "GET",
          credentials: "include",
          headers: {
            ...csrf,
          },
        });

        if (!res.ok) throw new Error("Failed to fetch link token");

        const token = await res.json();
        setLinkToken(token.link_token);
      } catch (error) {
        console.error("Error fetching link token:", error);
      }
    };

    fetchLinkToken();
  }, [csrf]);

  const { open, ready } = usePlaidLink({
    token: linkToken || "placeholder-token",
    onSuccess: async (
      public_token: string,
      metadata: PlaidLinkOnSuccessMetadata
    ) => {
      console.log("✅ Plaid link success:", metadata);

      try {
        const res = await fetch("/api/plaid/exchange-public-token", {
          method: "POST",
          credentials: "include",
          headers: {
            "Content-Type": "application/json",
            ...csrf!,
          },
          body: JSON.stringify({ public_token }),
        });

        if (!res.ok) throw new Error("Token exchange failed");
        ResetBankDataCache();
        GetBankDetails();
        onClose();
      } catch (err) {
        console.error("❌ Error exchanging token or fetching bank data:", err);
      }
    },
    onExit: (
      
    ) => {onClose();

    },
  });

  // Auto-open Plaid link when ready
  useEffect(() => {
    if (linkToken && ready) {
      open();
    }
  }, [linkToken, ready, open]);

  return<></>;

}
