import { useState } from "react";
import { SignIn } from "./signin";
import { SignUp } from "./signup";

export function AuthModalToggle() {
  const [modal, setModal] = useState<"none" | "signin" | "signup">("none");

  return (
    <>
      <div className="login-btn" onClick={() => setModal("signin")}>
        Log In &gt;
      </div>
      <div className="login-btn" onClick={() => setModal("signup")}>
        Sign Up &gt;
      </div>

      {modal === "signin" && (
        <SignIn onClose={() => setModal("none")} onSwap={() => setModal("signup")} />
      )}
      {modal === "signup" && (
        <SignUp onClose={() => setModal("none")} onSwap={() => setModal("signin")} />
      )}
    </>
  );
}
