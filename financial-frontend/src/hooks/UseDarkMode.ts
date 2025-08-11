// src/hooks/useDarkMode.ts
import { useEffect, useState } from "react";
import { darkMode } from "../utils/DarkMode";

export function useDarkMode(): [boolean, () => void] {
  const [value, setValue] = useState(darkMode.value);

  useEffect(() => {
    const unsubscribe = darkMode.subscribe(() => {
      setValue(darkMode.value);
    });
    return unsubscribe;
  }, []);

  return [value, darkMode.toggle];
}
