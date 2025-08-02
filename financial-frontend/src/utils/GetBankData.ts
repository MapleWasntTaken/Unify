import { BankData } from "../types/BankData";
import { GetCsrf } from "./GetCSRF";
import { AccountItem } from "../types/AccountItem";
import { TransactionItem } from "../types/TransactionItem";

// Singleton instance
const BankDetails: BankData = new BankData();
let isLoaded = false;
let csrf: Record<string, string> | null = null;

export async function GetBankDetails(): Promise<BankData> {
  if (isLoaded) return BankDetails;

  try {
    if (!csrf) {
      csrf = await GetCsrf();
    }

    const res = await fetch("/api/getUserData", {
      method: "GET",
      credentials: "include",
      headers: {
        "Content-Type": "application/x-www-form-urlencoded",
        ...csrf,
      },
    });

    if (!res.ok) throw new Error(`Failed to fetch: ${res.statusText}`);

    const data = await res.json();
    const Accounts = data.Accounts;
    const Transactions = data.Transactions;

    BankDetails.accounts = [];
    BankDetails.transactions = new Map();

    for (const x of Accounts) {
      BankDetails.accounts.push(new AccountItem(
        x.accountId,
        x.name,
        x.officialName,
        x.subtype,
        x.currentBalance,
        x.availableBalance,
        x.creditLimit
      ));
    }

    const grouped = new Map<string, TransactionItem[]>();

    for (const x of Transactions) {
      if (!grouped.has(x.accountId)) {
        grouped.set(x.accountId, []);
      }
      grouped.get(x.accountId)!.push(new TransactionItem(
        x.transactionId,
        x.accountId,
        x.name,
        x.merchantName,
        x.category,
        x.amount,
        x.date,
        x.pending,
        x.paymentChannel
      ));
    }

    for (const [accountId, transactions] of grouped) {
      BankDetails.transactions.set(accountId, transactions);
    }

    isLoaded = true;
    return BankDetails;
  } catch (err) {
    console.error("BANK SESSION FATAL:", err);
    throw new Error("Failed to load bank details");
  }
}
