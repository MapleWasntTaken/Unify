
import {useState } from "react";
import type { AccountItem } from "../../types/AccountItem";
import { useDarkMode } from "../../hooks/UseDarkMode";
import { Wallet, PiggyBank, CreditCard, Building2 } from "lucide-react";
import { AccountOverlay } from "./AccountOverlay";
import type { TransactionItem } from "./AccountOverlay";

type Props = AccountItem & {
  allTransactions?: TransactionItem[] | null | undefined;
  disabled?: boolean;
};

const fmtMoney = (n?: number) =>
  n == null ? "—" : new Intl.NumberFormat(undefined, { style: "currency", currency: "CAD" }).format(n);
const capWords = (s: string) => s.replace(/\b\w/g, c => c.toUpperCase());

export function BankTile({disabled, ...account }: Props) {
  const [isDark] = useDarkMode();
  const [open, setOpen] = useState(false);

  const subtypeRaw = account.subtype ?? "";
  const subtypeLC = subtypeRaw.toLowerCase();
  const isCreditCard = subtypeLC.includes("credit");

  const Icon =
    subtypeLC.includes("checking") || subtypeLC.includes("chequing")
      ? Wallet
      : subtypeLC.includes("savings") || subtypeLC.includes("save")
      ? PiggyBank
      : subtypeLC.includes("credit")
      ? CreditCard
      : Building2;

  const mask = account.mask?.toString().trim();
  const name = account.officialName || account.name || "Account";


  return (
    <>
      <button
        type="button"
        onClick={disabled ? undefined : () => setOpen(true)}
        disabled={disabled}
        className={`${isDark ? "bank-tile-dark" : "bank-tile-light"} tile-button-reset`}
        aria-label={`Open ${name}`}
      >
        <div className="bank-tile-header">
          <h1 className={isDark ? "bank-tile-name-dark" : "bank-tile-name-light"}>{name}</h1>
          <Icon
            size={22}
            className={isDark ? "bank-tile-icon-dark" : "bank-tile-icon-light"}
            aria-label={`${subtypeRaw || "account"} icon`}
          />
        </div>

        <div className={isDark ? "bank-tile-curbalance-dark" : "bank-tile-curbalance-light"}>
          <h2 className={isDark ? "tile-curbal-header-dark" : "tile-curbal-header-light"}>
            {fmtMoney(account.currentBalance ?? 0)}
          </h2>
          <h3 className={isDark ? "tile-curbal-subheader-dark" : "tile-curbal-subheader-light"}>
            {isCreditCard ? "Balance Owed" : "Current Balance"}
          </h3>

          <div className="tile-meta-row">
            <span className="badge-blue">{subtypeRaw ? capWords(subtypeRaw) : "Account"}</span>
            {mask && <span className="acct-mask">••••{mask}</span>}
          </div>
        </div>

        
      </button>

      <AccountOverlay
        open={open}
        onOpenChange={setOpen}
        account={account}
      />
    </>
  );
}
