import { GetCsrf } from "./GetCSRF";
import { AccountItem } from "../types/AccountItem";
import { TransactionItem } from "../types/TransactionItem";
import { BankData } from "../types/BankData";

let isLoaded = false;
let csrf: Record<string, string> | null = null;

export function isBankDataLoaded(): boolean {
  return isLoaded;
}

export async function GetBankDetails(): Promise<void> {
  if (isLoaded) return;

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
  const Statuses = data.Statuses;
  const parsedAccounts: AccountItem[] = [];
  const grouped = new Map<string, TransactionItem[]>();

  for (const x of Accounts) {
  parsedAccounts.push(
    new AccountItem(
      x.accountId,
      x.name,
      x.officialName,
      x.subtype,
      x.currentBalance,
      x.availableBalance,
      x.creditLimit,
      x.mask,
      x.statementBalance ?? null,
      x.lastStatementDate ?? null,
      x.nextPaymentDueDate ?? null,
      x.minimumPaymentAmount ?? null
    )
  );
}

  for (const x of Transactions) {
    if (!grouped.has(x.accountId)) {
      grouped.set(x.accountId, []);
    }
    grouped.get(x.accountId)!.push(
      new TransactionItem(
        x.transactionId,
        x.accountId,
        x.name,
        x.merchantName,
        x.category,
        x.amount,
        x.date,
        x.pending,
        x.paymentChannel
      )
    );
  }
  for(const x of Statuses){
    if(x.Filled ==="false"){
      console.log("Account not filled yet, retrying in 5 seconds...");

      setTimeout(() => {
        GetBankDetails();
      }, 5000);

      return;
    }
    if(x.Update ==="true"){
      parsedAccounts.forEach((y)=>{
        if( y.accountId===x.AccountId){
            y.status = true;
        }
      })
    }
  }

  BankData.setAccounts(parsedAccounts);
  BankData.setTransactions(grouped);
  isLoaded = true;
}

export function ResetBankDataCache(){
  isLoaded = false;
  GetBankDetails();
}
