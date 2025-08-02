import type { AccountItem } from "./AccountItem";
import type { TransactionItem } from "./TransactionItem";

export class BankData {
  accounts: AccountItem[];
  transactions: Map<string, TransactionItem[]>;
  constructor() {
    this.accounts = [];
    this.transactions = new Map<string,TransactionItem[]>;
  }
  
}
