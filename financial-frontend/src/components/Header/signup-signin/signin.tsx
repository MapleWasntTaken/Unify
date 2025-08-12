import { useEffect, useState } from "react";
import { GetCsrf, WipeCSRF } from "../../../utils/GetCSRF";
import { authState } from "../../../utils/authState";
import {ResetBankDataCache } from "../../../utils/GetBankData";
import "../../../css/SignIn.css";

export function SignIn({ onClose, onSwap}: { onClose: () => void; onSwap: () => void }) {

  const [csrf, setCsrf] = useState<Record<string, string> | null>(null);

  useEffect(() => {
    GetCsrf().then(setCsrf);
  }, []);

  if (!csrf) return null;

  return (
    <div className="sign-in-overlay" onClick={onClose}>
      <form
        className="sign-in-form"
        onClick={(e) => e.stopPropagation()}
        onSubmit={async (e) => {
          e.preventDefault();
          const formdata = new FormData(e.currentTarget);
          const res = await fetch("/api/login", {
            credentials: "include",
            method: "POST",
            body: new URLSearchParams(
              [...formdata.entries()] as [string, string][]
            ),
            headers: {
              "Content-Type": "application/x-www-form-urlencoded",
              ...csrf,
            },
          });

          if (res.ok) {
            WipeCSRF();
            ResetBankDataCache();
            authState.set(true);
            onClose();
          } else {
            console.log("Login failed");
          }
        }}
      >
        <div className="form-header-box">
          <h1 className="form-header">Welcome Back</h1>
          <h2 className="form-subheader">Sign in to access your accounts and institutions</h2>
        </div>

        <label htmlFor="username" className="form-title">Email</label>
        <input type="email" id="username" name="username" required className="form-input" placeholder="Enter your email" autoComplete="email"/>
        <label htmlFor="password" className="form-title">Password</label>
        <input type="password" id="password" name="password" required className="form-input" placeholder="Enter your password" autoComplete="password"/>

        <div className="form-stay-signed-in-box">
          <input type="checkbox" name="remember-me" className="form-checkbox"/>Stay Signed In?
        </div>

        <button type="submit" className="form-submit">Sign In</button>
        <div className="divider"></div>

        <p className="form-text-a">Don't have an account?&nbsp;
          <button className="signup-btn" onClick={(e) => {e.preventDefault();onSwap();}}>
            <em>Sign up</em>
          </button>
        </p>

        <button className="forgot-password">Forgot your password?</button>
      </form>
    </div>
  );
}
