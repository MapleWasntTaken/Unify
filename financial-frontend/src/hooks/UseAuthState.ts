import { useEffect, useState } from "react";
import { authState } from "../utils/authState";

export function useAuthState(): [boolean, (v: boolean) => void] {
  const [value, setValue] = useState(authState.value);

  useEffect(() => {
    const unsubscribe = authState.subscribe(() => {
      setValue(authState.value);
    });
    return unsubscribe;
  }, []);

  return [value, authState.set];
}