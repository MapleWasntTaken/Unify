// src/components/settings/Settings.tsx
import { useEffect, useMemo, useState } from "react";
import { Trash2, CreditCard, Building2, Wallet, PiggyBank } from "lucide-react";
import { useDarkMode } from "../../hooks/UseDarkMode";
import { useBankData } from "../../hooks/UseBankData";
import { GetCsrf } from "../../utils/GetCSRF";
import type { AccountItem } from "../../types/AccountItem";
import "../../css/Settings.css";

type SettingsProps = {
  isOpen: boolean;
  onClose: () => void;
};

function prettySubtype(s?: string) {
  if (!s) return "Account";
  return s
    .replace(/_/g, " ")
    .split(" ")
    .map(w => (w ? w[0].toUpperCase() + w.slice(1).toLowerCase() : w))
    .join(" ");
}

function AccountIcon(subtype?: string) {
  const sub = (subtype || "").toLowerCase();
  if (sub.includes("credit")) return <CreditCard size={18} aria-hidden />;
  if (sub.includes("savings")) return <PiggyBank size={18} aria-hidden />;
  if (sub.includes("checking") || sub.includes("chequing")) return <Wallet size={18} aria-hidden />;
  return <Building2 size={18} aria-hidden />;
}

export default function Settings({ isOpen, onClose }: SettingsProps) {
  const [isDark] = useDarkMode();
  const [accounts] = useBankData();                      
  const [localAccounts, setLocalAccounts] = useState<AccountItem[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [csrf, setCsrf] = useState<Record<string, string> | null>(null);

  useEffect(() => {
    GetCsrf().then(setCsrf);
  }, []);
  useEffect(() => {
    if (accounts !== undefined && accounts !== null) {
      setIsLoading(false);
      if (Array.isArray(accounts)) setLocalAccounts(accounts);
    }
  }, [accounts]);

  useEffect(() => {
    if (!isOpen) return;
    const onKey = (e: KeyboardEvent) => e.key === "Escape" && onClose();
    window.addEventListener("keydown", onKey);
    return () => window.removeEventListener("keydown", onKey);
  }, [isOpen, onClose]);

  const count = useMemo(
    () => (Array.isArray(localAccounts) ? localAccounts.length : 0),
    [localAccounts]
  );

  const deleteAccountItem = async (acct: AccountItem) => {
    const title = acct.name || acct.officialName || "Account";
    const label = `${title} • ${acct.mask ? `••••${acct.mask}` : ""}`;

    if (!window.confirm(`Remove this account?\n\n${label}\n\nIf it’s the last account on the Plaid Item, the whole Item will be removed.`)) {
      return;
    }
    setLocalAccounts(prev => prev.filter(a => a.accountId !== acct.accountId));

    try {
      
      const res = await fetch("/api/plaid/remove-item", {
        method: "POST",
        headers: { "Content-Type": "text/plain" ,...csrf!},
        body: acct.accountId,
        credentials: "include",
      });
      console.log(res.body);
      if (!res.ok) {
        setLocalAccounts(prev =>
          [...prev, acct].sort((a, b) => (a.name || "").localeCompare(b.name || ""))
        );
        throw new Error(`Delete failed: ${res.status}`);
      }
    } catch (err) {
      console.error(err);
      alert("Couldn’t delete that account. Try again.");
    }
  };
  
  if (isLoading) return <div></div>;
  if (!isOpen) return null;

  return (
    <div className="uf-settings-overlay" onClick={onClose}>
      <div
        className={isDark ? "uf-settings-card uf-dark" : "uf-settings-card uf-light"}
        onClick={(e) => e.stopPropagation()}
        role="dialog"
        aria-modal="true"
        aria-labelledby="uf-settings-title"
      >
        <header className="uf-settings-header">
          <h2 id="uf-settings-title">Settings</h2>
          <button className="uf-settings-close" onClick={onClose} aria-label="Close">✕</button>
        </header>

        <section className="uf-settings-section">
          <h3 className="uf-settings-section-title">
            Linked Accounts <span className="uf-settings-meta">({count})</span>
          </h3>

          {isLoading ? (
            <div className="uf-empty">Loading accounts...</div>
          ) : count === 0 ? (
            <div className="uf-empty">No linked accounts yet.</div>
          ) : (
            <ul className="uf-accounts-list">
              {localAccounts
                .slice()
                .sort((a, b) => (a.name || "").localeCompare(b.name || ""))
                .map((acct) => {
                  const subtype = prettySubtype(acct.subtype);
                  const mask = acct.mask ? `••••${acct.mask}` : "";
                  const institution = acct.name || acct.officialName || "";
                  const title = acct.name || acct.officialName || "Account";

                  return (
                    <li key={acct.accountId} className="uf-account-row" title={title}>
                      <div className="uf-account-main">
                        <span className="uf-account-icon">{AccountIcon(acct.subtype)}</span>
                        <div className="uf-account-text">
                          <div className="uf-account-title">{title}</div>
                          <div className="uf-account-sub">
                            {[institution, subtype, mask].filter(Boolean).join(" • ")}
                          </div>
                        </div>
                      </div>

                      <button
                        className="uf-trash-btn"
                        aria-label={`Delete ${title}`}
                        onClick={() => deleteAccountItem(acct)}
                        title="Remove account"
                      >
                        <Trash2 size={18} />
                      </button>
                    </li>
                  );
                })}
            </ul>
          )}
        </section>

        <section className="uf-settings-section">
          <h3 className="uf-settings-section-title">Legal</h3>
          <ul className="uf-settings-list">
            <li className="uf-settings-row"><a href="/pp">Privacy Policy</a></li>
            <li className="uf-settings-row"><a href="/tos">Terms of Service</a></li>
          </ul>
        </section>
      </div>
    </div>
  );
}
