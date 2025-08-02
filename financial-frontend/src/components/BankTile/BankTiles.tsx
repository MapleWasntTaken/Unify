import { useEffect, useState } from "react";
import { GetBankDetails } from "../../utils/GetBankData"; // renamed as per prior message
import { BankData } from "../../types/BankData";

export function BankTiles() {
  const [data, setData] = useState<BankData | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const load = async () => {
      try {
        const result = await GetBankDetails();
        setData(result);
      } catch (err) {
        console.error("Couldn't load bank data:", err);
      } finally {
        setLoading(false);
      }
    };
    load();
  }, []);

  if (loading) return <p>Loading...</p>;
  if (!data) return <p>Error loading bank data</p>;

  return (
    <div>
      {data.accounts.map(acc => (
        <div key={acc.accountId}>
          <h3>{acc.name}</h3>
          <p>Balance: ${acc.currentBalance}</p>
        </div>
      ))}
    </div>
  );
}
