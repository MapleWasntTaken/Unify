import { useEffect } from "react";
import { authState } from "../utils/authState";

export function useAuthBootstrap() {
  useEffect(() => {
    async function checkAuth() {
      try {
        const res = await fetch("/api/whoami", {
          method: "GET",
          credentials: "include",
        });
        if (!res.ok) throw new Error("Not authenticated");
        const text = await res.text();

        if (text === "Logged in") {
          authState.set(true);
        } else {
          
          console.log("here");
          authState.set(false);
        }
      } catch {
        authState.set(false);
      }
    }

    checkAuth();
  }, []);
}
