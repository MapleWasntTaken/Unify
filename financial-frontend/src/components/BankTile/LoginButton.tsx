import { useState } from "react";
import { SignIn } from "../Header//signup-signin/signin";
import { SignUp } from "../Header//signup-signin/signup";
import { LogIn } from "lucide-react";
import "../../css/BankTiles.css"

export function LoginButton() {
  const [view, setView] = useState<"none" | "signin" | "signup">("none");

  return (
    <>
      <div className="login-btn-banktile" onClick={() => setView("signin")}>
         <LogIn color="white" size={25}/> <h1 className="login-btn-text">Sign In to Your Dashboard</h1>
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
