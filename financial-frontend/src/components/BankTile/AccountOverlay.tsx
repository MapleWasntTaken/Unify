import { useEffect, useMemo, useRef } from "react";
import { createPortal } from "react-dom";
import { X } from "lucide-react";
import { useDarkMode } from "../../hooks/UseDarkMode";
import { useBankData } from "../../hooks/UseBankData";
import "../../css/AccountOverlay.css";
import type { AccountItem } from "../../types/AccountItem";

export type TransactionItem = {
  transactionId: string;
  accountId: string;
  amount: number;
  date: string;
  name: string;
  merchantName?: string;
  category?: string;
  pending?: boolean;
  paymentChannel?: string;
};

export type OverlayAccount = {
  accountId: string;
  name?: string;
  officialName?: string;
  subtype?: string;
  mask?: string | null;
  currentBalance?: number | null;
  availableBalance?: number | null;
  creditLimit?: number | null;

  statementBalance?: number | null;
  lastStatementDate?: string | null;
  nextPaymentDueDate?: string | null;
  minimumPaymentAmount?: number | null;
};

const capWords = (s: string) => s.replace(/\b\w/g, (c) => c.toUpperCase());
const fmtMoney = (n?: number | null) =>
  n == null ? "—" : new Intl.NumberFormat(undefined, { style: "currency", currency: "CAD" }).format(n);

export function AccountOverlay({
  open,
  onOpenChange,
  account,
}: {
  open: boolean;
  onOpenChange: (next: boolean) => void;
  account: OverlayAccount | null;
}) {
  const [isDark] = useDarkMode();
  const backdropRef = useRef<HTMLDivElement>(null);

  const [, txMap] = useBankData() as unknown as [
    AccountItem[],
    Map<string, TransactionItem[]> | undefined
  ];

  const txForAccount = useMemo(() => {
    if (!account) return null;
    if (!txMap) return undefined;
    const list = txMap.get(account.accountId) ?? [];

    return [...list].sort((a, b) => b.date.localeCompare(a.date));
  }, [account, txMap]);

  useEffect(() => {
    if (!open) return;
    const prev = document.body.style.overflow;
    document.body.style.overflow = "hidden";
    return () => {
      document.body.style.overflow = prev;
    };
  }, [open]);

  useEffect(() => {
    if (!open) return;
    const onKey = (e: KeyboardEvent) => {
      if (e.key === "Escape") onOpenChange(false);
    };
    window.addEventListener("keydown", onKey);
    return () => window.removeEventListener("keydown", onKey);
  }, [open, onOpenChange]);

  const onBackdropClick = (e: React.MouseEvent) => {
    if (e.target === backdropRef.current) onOpenChange(false);
  };

  if (!open || !account) return null;

  const name = account.name || account.officialName || "Account";
  const subtypeLabel = account.subtype ? capWords(account.subtype) : "Account";
  const subtypeLC = (account.subtype || "").toLowerCase();
  const isCreditCard = subtypeLC.includes("credit");

  const owedAbs = Math.abs(account.currentBalance ?? 0);
  const availableToSpend =
    isCreditCard && account.creditLimit != null
      ? Math.max(0, (account.creditLimit ?? 0) - owedAbs)
      : null;

  const content = (
    <div
      ref={backdropRef}
      onMouseDown={onBackdropClick}
      className={`overlay-backdrop ${isDark ? "overlay-backdrop-dark" : "overlay-backdrop-light"}`}
      aria-modal="true"
      role="dialog"
      aria-labelledby="account-overlay-title"
    >
      <div className={`overlay-panel ${isDark ? "overlay-panel-dark" : "overlay-panel-light"}`}>
        <div className="overlay-header">
          <div className="overlay-title-wrap">
            <h2 id="account-overlay-title" className="overlay-title">{name}</h2>
            <div className="overlay-subtitle">
              <span className="pill">{subtypeLabel}</span>
              {account.mask && <span className="pill pill-muted">•••• {account.mask}</span>}
            </div>
          </div>
          <button className="icon-btn" aria-label="Close" onClick={() => onOpenChange(false)}>
            <X size={20} />
          </button>
        </div>

        <div className="overlay-content">
          <section className="section">
            <h3 className="section-title">Overview</h3>

            {!isCreditCard && (
              <div className="summary-grid" style={{ gridTemplateColumns: "1fr" }}>
                <div className="summary-card">
                  <div className="label">Current Balance</div>
                  <div className="value">{fmtMoney(account.currentBalance)}</div>
                </div>
              </div>
            )}

            {isCreditCard && (
              <div className="summary-grid">
                <div className="summary-card">
                  <div className="label">Current Balance</div>
                  <div className="value">{fmtMoney(account.currentBalance)}</div>
                </div>

                <div className="summary-card">
                  <div className="label">Available to Spend</div>
                  <div className="value">{fmtMoney(availableToSpend)}</div>
                </div>

                <div className="summary-card">
                  <div className="label">Credit Limit</div>
                  <div className="value">{fmtMoney(account.creditLimit)}</div>
                </div>
                {account.lastStatementDate && (
                  <div className="summary-card">
                    <div className="label">Statement Date</div>
                    <div className="value">{new Date(account.lastStatementDate).toLocaleDateString()}</div>
                  </div>
                )}
                <div className="summary-card">
                  <div className="label">Next Payment Due Date</div>
                  <div className="value">
                    {account.nextPaymentDueDate
                      ? new Date(account.nextPaymentDueDate).toLocaleDateString()
                      : "—"}
                  </div>
                </div>
              </div>
            )}
          </section>

          <section className="section">
            <h3 className="section-title">Transactions</h3>

            {txForAccount === undefined && <div className="placeholder">Loading transactions…</div>}
            {txForAccount && txForAccount.length === 0 && <div className="placeholder">No transactions.</div>}

            {txForAccount && txForAccount.length > 0 && (
              <ul className="tx-list">
                {txForAccount.map((t) => {
                  const amt = t.amount ?? 0;
                  const isOut = amt > 0;
                  return (
                    <li key={t.transactionId} className="tx-row">
                      <div className="tx-main">
                        <div className="tx-name">{t.name || t.merchantName || "Transaction"}</div>
                        <div className="tx-meta">
                          <span className="tx-date">{new Date(t.date).toLocaleDateString()}</span>
                          {t.category && <span className="tx-category">{t.category}</span>}
                          {t.pending && <span className="tx-badge">Pending</span>}
                        </div>
                      </div>
                      <div className={`tx-amount ${isOut ? "tx-out" : "tx-in"}`}>{fmtMoney(amt)}</div>
                    </li>
                  );
                })}
              </ul>
            )}
          </section>
        </div>
      </div>
    </div>
  );

  return createPortal(content, document.body);
}
