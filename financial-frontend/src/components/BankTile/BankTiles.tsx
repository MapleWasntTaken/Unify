import { useEffect, useState } from "react";
import { BankTile } from "./BankTile";
import { useBankData } from "../../hooks/UseBankData";
import { useDarkMode } from "../../hooks/UseDarkMode";
import {useAuthState} from "../../hooks/UseAuthState"
import { Building2 } from "lucide-react";
import "../../css/BankTiles.css";
import { AddPlaidButton } from "./AddPlaidButton";
import { LoginButton } from "./LoginButton";

export function BankTiles() {
  const [isDark] = useDarkMode();
  const [accounts] = useBankData();
  const [isLoading, setIsLoading] = useState(true);
  const [isLoggedIn] = useAuthState();

  useEffect(() => {
    if (accounts !== undefined && accounts !== null) {
      setIsLoading(false);
    }
  }, [accounts]);


  // 2️⃣ Logged out state
  if (!isLoggedIn) {
    return (
      <div className={isDark ? "bank-tiles-container-container-dark" : "bank-tiles-container-container-light"}>
        <div className={isDark ? "bank-tiles-container-dark" : "bank-tiles-container-light"}>
          <div className={isDark ? "bank-tiles-header-div-dark" : "bank-tiles-header-div-light"}>
            <h1 className={isDark ? "bank-tiles-header-header-dark" : "bank-tiles-header-header-light"}>Your Accounts</h1>
            <h2 className={isDark ? "bank-tiles-header-subheader-dark" : "bank-tiles-header-subheader-light"}>
              Sign in to view your linked accounts and transactions
            </h2>
          </div>
          <div className={isDark ? "add-account-container-dark" : "add-account-container-light"}>
            <h1 className={isDark ? "login-header-dark" : "login-header-light"}>
              Sign in to view your accounts
            </h1>
            <h1 className={isDark ? "login-subheader-dark" : "login-subheader-light"}>
              Access your linked bank accounts, view balances, and track all your transactions in one secure dashboard.
            </h1>
            <LoginButton />
          </div>
        </div>
      </div>
    );
  }
  
  // 1️⃣ Loading state
  if (isLoading) {
    return (
      <div className={isDark ? "bank-tiles-container-container-dark" : "bank-tiles-container-container-light"}>
        <div className={isDark ? "bank-tiles-container-dark" : "bank-tiles-container-light"}>
        </div>
      </div>
    );
  }
  if (!accounts || accounts.length === 0) {
    return (
      <div className={isDark ? "bank-tiles-container-container-dark" : "bank-tiles-container-container-light"}>
        <div className={isDark ? "bank-tiles-container-dark" : "bank-tiles-container-light"}>
          <div className={isDark ? "add-account-container-dark" : "add-account-container-light"}>
            <div className={isDark ? "icon-circle-dark" : "icon-circle-light"}>
              <Building2 className={isDark ? "add-account-icon-dark" : "add-account-icon-light"} size={55} strokeWidth={1} />
            </div>
            <h1 className={isDark ? "add-account-header-dark" : "add-account-header-light"}>No accounts connected</h1>
            <h1 className={isDark ? "add-account-subheader-dark" : "add-account-subheader-light"}>
              Link your bank accounts to view balances, track spending, and manage your finances all in one place.
            </h1>
            <AddPlaidButton />
          </div>
        </div>
      </div>
    );
  }



  // 4️⃣ Logged in + Has accounts
  return (
    <div className={isDark ? "bank-tiles-container-container-dark" : "bank-tiles-container-container-light"}>
      <div className={isDark ? "bank-tiles-container-dark" : "bank-tiles-container-light"}>
        <div className={isDark ? "bank-tiles-header-div-dark" : "bank-tiles-header-div-light"}>
            <h1 className={isDark ? "bank-tiles-header-header-dark" : "bank-tiles-header-header-light"}>Your Accounts</h1>
            <h2 className={isDark ? "bank-tiles-header-subheader-dark" : "bank-tiles-header-subheader-light"}>
              Sign in to view your linked accounts and transactions
            </h2>
          </div>
        <div className="bank-div">
          {accounts.map(account => (
          <BankTile key={account.accountId} {...account} />
          ))}
        </div>
      </div>
    </div>
  );
}
