// hooks/usePlaidUpdate.ts
import { useEffect, useState } from "react";
import { BankData } from "../types/BankData";
import { PlaidUpdateData } from "../types/PlaidUpdate";
import type { PlaidUpdateState } from "../types/PlaidUpdate";
import type { AccountItem } from "../types/AccountItem";
import { GetPlaidUpdate } from "../utils/GetPlaidUpdate";

export function usePlaidUpdate(): PlaidUpdateState {
  const [state, setState] = useState<PlaidUpdateState>(PlaidUpdateData.getState());

  // Subscribe to the PlaidUpdateData store
  useEffect(() => {
    const unsub = PlaidUpdateData.subscribe(() => setState(PlaidUpdateData.getState()));
    return () => { unsub(); };
  }, []);

  // React to BankData changes
  useEffect(() => {
    const unsubBank = BankData.subscribe(async () => {
      const accounts: AccountItem[] = BankData.getAccounts() ?? [];
      const needing = accounts.filter(a => a.status);

      PlaidUpdateData.setState({ needsUpdate: needing.length > 0 });

      if (needing.length === 0) {
        PlaidUpdateData.setState({ linkTokens: {} });
        return;
      }

      // Find the next account that needs update but has no token yet
      const currentTokens = PlaidUpdateData.getState().linkTokens;
      const next = needing.find(a => !currentTokens[a.accountId]);

      if (!next) return; // all needing accounts already have tokens

      // Fetch ONE token for that accountId (guard to avoid races)
      await PlaidUpdateData.guarded(async () => {
        const token = await GetPlaidUpdate(next.accountId);
        PlaidUpdateData.setState({
          linkTokens: { ...PlaidUpdateData.getState().linkTokens, [next.accountId]: token }
        });
      });
    });

    // Kick once on mount to seed state
    (async () => {
      const accounts: AccountItem[] = BankData.getAccounts() ?? [];
      const needing = accounts.filter(a => a.status);
      PlaidUpdateData.setState({ needsUpdate: needing.length > 0 });

      if (needing.length > 0) {
        const currentTokens = PlaidUpdateData.getState().linkTokens;
        const next = needing.find(a => !currentTokens[a.accountId]);
        if (next) {
          await PlaidUpdateData.guarded(async () => {
            const token = await GetPlaidUpdate(next.accountId);
            PlaidUpdateData.setState({
              linkTokens: { ...PlaidUpdateData.getState().linkTokens, [next.accountId]: token }
            });
          });
        }
      } else {
        PlaidUpdateData.setState({ linkTokens: {} });
      }
    })();

    return () => { unsubBank(); };
  }, []);

  return state;
}
