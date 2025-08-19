import { useEffect, useState } from "react";
import { BankData } from "../types/BankData";
import { AccountItem } from "../types/AccountItem";
import { TransactionItem } from "../types/TransactionItem";

export function useBankData(): [AccountItem[], Map<string, TransactionItem[]>] {
  const [accounts, setAccounts] = useState(BankData.getAccounts());
  const [transactions, setTransactions] = useState(BankData.getTransactions());

  useEffect(() => {
    const unsubscribe = BankData.subscribe(() => {
      setAccounts(BankData.getAccounts());
      setTransactions(BankData.getTransactions());
    });
    return unsubscribe;
  }, []);

  return [accounts!, transactions];
}
