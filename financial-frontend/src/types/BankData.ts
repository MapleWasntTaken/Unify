import { AccountItem } from "./AccountItem";
import { TransactionItem } from "./TransactionItem";

let accounts: AccountItem[] = [];
let transactions: Map<string, TransactionItem[]> = new Map();

const subscribers: (() => void)[] = [];

export const BankData = {
  getAccounts() {
    return accounts;
  },
  getTransactions() {
    return transactions;
  },
  setAccounts(newAccounts: AccountItem[]) {
    accounts = newAccounts;
    notify();
  },
  setTransactions(newTransactions: Map<string, TransactionItem[]>) {
    transactions = newTransactions;
    notify();
  },
  clear() {
    accounts = [];
    transactions = new Map();
    notify();
  },
  subscribe(fn: () => void) {
    subscribers.push(fn);
    return () => {
      const i = subscribers.indexOf(fn);
      if (i !== -1) subscribers.splice(i, 1);
    };
  }
};

function notify() {
  for (const fn of subscribers) fn();
}
