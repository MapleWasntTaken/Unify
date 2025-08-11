let isLoggedIn = false;
const subscribers: (() => void)[] = [];

export const authState = {
  get value() {
    return isLoggedIn;
  },
  set(value: boolean) {
    isLoggedIn = value;
    subscribers.forEach(fn => fn());
  },
  subscribe(fn: () => void) {
    subscribers.push(fn);
    return () => {
      const i = subscribers.indexOf(fn);
      if (i > -1) subscribers.splice(i, 1);
    };
  }
};