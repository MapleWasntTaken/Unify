// src/utils/darkMode.ts

let isDark = localStorage.getItem('dark') === 'true';
const subscribers: (() => void)[] = [];

export const darkMode = {
  get value() {
    return isDark;
  },
  toggle() {
    isDark = !isDark;
    localStorage.setItem('dark', isDark.toString());
    document.body.classList.toggle('dark', isDark);
    subscribers.forEach(fn => fn()); // notify
  },
  set(value: boolean) {
    isDark = value;
    localStorage.setItem('dark', isDark.toString());
    document.body.classList.toggle('dark', isDark);
    subscribers.forEach(fn => fn()); // notify
  },
  subscribe(fn: () => void) {
    subscribers.push(fn);
    return () => {
      const i = subscribers.indexOf(fn);
      if (i > -1) subscribers.splice(i, 1);
    };
  }
};
