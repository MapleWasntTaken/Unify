import { useState } from "react";
import { SignIn } from "./signin";
import { SignUp } from "./signup";

export function LoginButton() {
  const [view, setView] = useState<"none" | "signin" | "signup">("none");

  return (
    <>
      <div className="login-btn" onClick={() => setView("signin")}>
        Log In &gt;
      </div>

      {view === "signin" && (
        <SignIn
          onClose={() => setView("none")}
          onSwap={() => setView("signup")}
        />
      )}

      {view === "signup" && (
        <SignUp
          onClose={() => setView("none")}
          onSwap={() => setView("signin")}
        />
      )}
    </>
  );
}
