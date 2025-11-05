
export type PlaidUpdateState = {
  needsUpdate: boolean;
  linkTokens: Record<string, string>;
};

type Listener = () => void;

class PlaidUpdateStore {
  private state: PlaidUpdateState = { needsUpdate: false, linkTokens: {} };
  private listeners: Set<Listener> = new Set();
  private inFlight = false;

  getState() {
    return this.state;
  }

  subscribe(fn: Listener): () => void {
    this.listeners.add(fn);
    return () => {
        this.listeners.delete(fn);
    };
}


  private emit() {
    for (const fn of this.listeners) fn();
  }

  setState(next: Partial<PlaidUpdateState>) {
    this.state = { ...this.state, ...next };
    this.emit();
  }

  /** Prevent duplicate refreshes if something is already running */
  async guarded<T>(task: () => Promise<T>) {
    if (this.inFlight) return;
    this.inFlight = true;
    try {
      await task();
    } finally {
      this.inFlight = false;
    }
  }
}

export const PlaidUpdateData = new PlaidUpdateStore();
