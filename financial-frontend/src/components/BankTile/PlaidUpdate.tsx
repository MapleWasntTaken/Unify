import { useEffect, useMemo, useRef, useState } from "react";
import { usePlaidLink } from "react-plaid-link";

import { usePlaidUpdate } from "../../hooks/UsePlaidUpdate";
import { GetCsrf } from "../../utils/GetCSRF";
import { ResetBankDataCache } from "../../utils/GetBankData";

type Props = {
  onClose?: () => void;
};

export function PlaidUpdate({ onClose }: Props) {
  const { needsUpdate, linkTokens } = usePlaidUpdate();

  // Track processed accounts this session so we go one-by-one
  const processedRef = useRef<Set<string>>(new Set());

  // Pick the next unprocessed account/token
  const current = useMemo(() => {
    for (const [accountId, token] of Object.entries(linkTokens || {})) {
      if (!processedRef.current.has(accountId) && token) {
        return { accountId, token };
      }
    }
    return null;
  }, [linkTokens]);

  // Only render the runner when we have something to do
  if (!needsUpdate || !current) return null;

  return (
    <PlaidUpdateRunner
      accountId={current.accountId}
      token={current.token}
      onDone={(accountId: string) => {
        processedRef.current.add(accountId);
        ResetBankDataCache();
        // If nothing left unprocessed, close
        const remaining = Object.keys(linkTokens || {}).some(
          (id) => !processedRef.current.has(id)
        );
        if (!remaining && onClose) onClose();
      }}
      onExit={(accountId: string) => {
        processedRef.current.add(accountId);
        ResetBankDataCache();
        const remaining = Object.keys(linkTokens || {}).some(
          (id) => !processedRef.current.has(id)
        );
        if (!remaining && onClose) onClose();
      }}
    />
  );
}

function PlaidUpdateRunner({
  accountId,
  token,
  onDone,
  onExit,
}: {
  accountId: string;
  token: string;
  onDone: (accountId: string) => void;
  onExit: (accountId: string) => void;
}) {
  const [csrf, setCsrf] = useState<Record<string, string> | null>(null);

  // Fetch CSRF once
  useEffect(() => {
    let mounted = true;
    GetCsrf()
      .then((t) => {
        if (mounted) setCsrf(t);
      })
      .catch((e) => console.error("CSRF fetch failed", e));
    return () => {
      mounted = false;
    };
  }, []);

  // Hooks are always called (component is only rendered when token is ready)
  const { open, ready } = usePlaidLink({
    token,
    onSuccess: async (public_token: string) => {
      try {
        const res = await fetch("/api/plaid/exchange-public-token", {
          method: "POST",
          credentials: "include",
          headers: {
            "Content-Type": "application/json",
            ...(csrf || {}),
          },
          body: JSON.stringify({ public_token }),
        });
        if (!res.ok) throw new Error(`Token exchange failed: ${res.status} ${res.statusText}`);
      } catch (err) {
        console.error("❌ Error exchanging token:", err);
      } finally {
        onDone(accountId);
      }
    },
    onExit: () => {
      onExit(accountId);
    },
  });

  // Auto-open when ready
  useEffect(() => {
    if (ready) open();
  }, [ready, open]);

  // No visible UI — runner just manages the Link flow
  return null;
}
