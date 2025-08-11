import { useEffect, useRef, useState } from "react";
import { GetCsrf } from "../../../utils/GetCSRF";
import "../../../css/SignIn.css";

export function SignUp({ onClose, onSwap }: { onClose: () => void; onSwap: () => void }) {
  const [csrf, setCsrf] = useState<Record<string, string> | null>(null);
  const [emailTaken, setEmailTaken] = useState(false);
  const [password, setPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [showPasswordRules, setShowPasswordRules] = useState(false);

  const emailRef = useRef<HTMLInputElement | null>(null);
  const passwordRef = useRef<HTMLInputElement | null>(null);

  const passwordRequirements = {
    minLength: /.{8,}/,
    uppercase: /[A-Z]/,
    lowercase: /[a-z]/,
    number: /[0-9]/,
    special: /[^A-Za-z0-9]/,
  };

  const validatePassword = (pw: string): boolean =>
    Object.values(passwordRequirements).every((regex) => regex.test(pw));

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
          const formData = new FormData(e.currentTarget);
          const email = String(formData.get("username"));

          if (password !== confirmPassword) {
            passwordRef.current?.setCustomValidity("Passwords do not match.");
            passwordRef.current?.reportValidity();
            return;
          }

          if (!validatePassword(password)) {
            passwordRef.current?.setCustomValidity("Password must meet all requirements.");
            passwordRef.current?.reportValidity();
            return;
          } else {
            passwordRef.current?.setCustomValidity("");
          }

          const res = await fetch("/api/signup", {
            method: "POST",
            body: JSON.stringify({ email, password }),
            headers: {
              "Content-Type": "application/json",
              ...csrf,
            },
            credentials: "include",
          });

          if (res.status === 409) {
            setEmailTaken(true);
            emailRef.current?.setCustomValidity("Email already in use.");
            emailRef.current?.reportValidity();
          } else if (res.ok) {
            setEmailTaken(false);
            emailRef.current?.setCustomValidity("");
            onSwap();
          } else {
            console.log("Signup failed.");
          }
        }}
      >
        <div className="form-header-box">
          <h1 className="form-header">Create Account</h1>
          <h2 className="form-subheader">Sign up to start managing your finances</h2>
        </div>

        <label htmlFor="username" className="form-title">Email</label>
        <input
          type="email"
          id="username"
          name="username"
          required
          className={`form-input-a ${emailTaken ? "input-error" : ""}`}
          ref={emailRef}
          placeholder="Enter your email"
        />

        <label htmlFor="password" className="form-title">Password</label>
        <input
          type="password"
          id="password"
          name="password"
          required
          className="form-input"
          placeholder="Enter your password"
          ref={passwordRef}
          value={password}
          onFocus={() => setShowPasswordRules(true)}
          onBlur={() => setShowPasswordRules(false)}
          onChange={(e) => setPassword(e.target.value)}
        />

        {showPasswordRules && (
          <ul className="password-checklist">
            <li className={passwordRequirements.minLength.test(password) ? "valid" : ""}>• At least 8 characters</li>
            <li className={passwordRequirements.uppercase.test(password) ? "valid" : ""}>• One uppercase letter</li>
            <li className={passwordRequirements.lowercase.test(password) ? "valid" : ""}>• One lowercase letter</li>
            <li className={passwordRequirements.number.test(password) ? "valid" : ""}>• One number</li>
            <li className={passwordRequirements.special.test(password) ? "valid" : ""}>• One special character</li>
          </ul>
        )}

        <label htmlFor="confpassword" className="form-title">Confirm Password</label>
        <input
          type="password"
          id="confpassword"
          name="confpassword"
          required
          className="form-input"
          placeholder="Confirm your password"
          value={confirmPassword}
          onChange={(e) => setConfirmPassword(e.target.value)}
        />

        {password && confirmPassword && password !== confirmPassword && (
          <p className="form-error-text">Passwords do not match</p>
        )}

        <button type="submit" className="form-submit">Sign Up</button>
        <div className="divider"></div>

        <p className="form-text-a">
          Already have an account?&nbsp;
          <button
            className="signup-btn"
            onClick={(e) => {
              e.preventDefault();
              onSwap();
            }}
          >
            <em>Sign in</em>
          </button>
        </p>
      </form>
    </div>
  );
}
