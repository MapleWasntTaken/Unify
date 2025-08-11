import {
  LineChart,
  Line,
  XAxis,
  YAxis,
  Tooltip,
  ResponsiveContainer,
  Legend,
} from "recharts";
import { useEffect, useState } from "react";
import { useBankData } from "../hooks/UseBankData";
import { GetBankDetails } from "../utils/GetBankData";
import { useDarkMode } from "../hooks/UseDarkMode";
import "../css/Graph.css";

type GraphRow = {
  date: string;
  [accountName: string]: string | number;
};

export function Graph() {
  const [isDark] = useDarkMode();
  const [accounts, transactionsMap] = useBankData();
  const [graphData, setGraphData] = useState<GraphRow[]>([]);
  const [accountIds, setAccountIds] = useState<string[]>([]);
  const [range, setRange] = useState(30);

  useEffect(() => {
    async function processData() {
      await GetBankDetails();

      const today = new Date();
      const allDays: string[] = [];

      for (let i = range - 1; i >= 0; i--) {
        const d = new Date(today);
        d.setDate(d.getDate() - i);
        allDays.push(d.toISOString().split("T")[0]); // YYYY-MM-DD
      }

      const perAccountData: { [accountId: string]: number[] } = {};

      for (const acc of accounts) {
        const txs = (transactionsMap.get(acc.accountId) ?? []).slice().sort((a, b) =>
          new Date(b.date).getTime() - new Date(a.date).getTime()
        );

        let balance = acc.currentBalance;
        const balances: number[] = [];
        let txIndex = 0;

        for (let i = allDays.length - 1; i >= 0; i--) {
          const currentDate = allDays[i];
          while (txIndex < txs.length && txs[txIndex].date > currentDate) {
            balance -= txs[txIndex].amount;
            txIndex++;
          }
          balances.unshift(parseFloat(balance.toFixed(2)));
        }

        perAccountData[acc.accountId] = balances;
      }

      const rows: GraphRow[] = [];
      for (let i = 0; i < allDays.length; i++) {
        const row: GraphRow = { date: allDays[i] };
        for (const acc of accounts) {
          const accName = acc.officialName ?? acc.name;
          row[accName] = perAccountData[acc.accountId]?.[i] ?? 0;
        }
        rows.push(row);
      }

      setAccountIds(accounts.map((a) => a.officialName ?? a.name));
      setGraphData(rows);
    }

    processData();
  }, [range, transactionsMap, accounts]);

  return (
    <div className={isDark ? "graph-container-container-container-dark" : "graph-container-container-container-light"}>
      <div className={isDark ? "graph-container-container-dark" : "graph-container-container-light"}>
        <div className={isDark ? "graph-header-dark" : "graph-header-light"}>
          <h1 className={isDark ? "header-txt-dark" : "header-txt-light"}>Your Financial Movement Timeline</h1>
          <select
            className={isDark ? "graph-range-dropdown-dark" : "graph-range-dropdown-light"}
            value={range}
            onChange={(e) => setRange(parseInt(e.target.value))}
          >
            <option value={7}>Last 7 Days</option>
            <option value={30}>Last 30 Days</option>
            <option value={90}>Last 90 Days</option>
            <option value={365}>Last 1 Year</option>
            <option value={730}>Last 2 Years</option>
          </select>
        </div>
        <ResponsiveContainer width="96%" height={300} z-index={999}>
          <LineChart data={graphData}>
            <XAxis dataKey="date" />
            <YAxis  width={72}tickMargin={8} tickFormatter={(v) => `$${v.toFixed(2)}`} />
            <Tooltip
              formatter={(v: number) => [`$${v.toFixed(2)}`, "Balance"]}
              labelFormatter={(label) => `Date: ${label}`}
            />
            <Legend />
            {accountIds.map((name, i) => (
              <Line
                key={name}
                type="monotone"
                dataKey={name}
                stroke={generateColor(i)}
                strokeWidth={2}
                dot={false}
              />
            ))}
          </LineChart>
        </ResponsiveContainer>
      </div>
    </div>
  );
}

function generateColor(index: number): string {
  const colors = [
    "#8884d8", "#82ca9d", "#ffc658", "#ff7f0e",
    "#d62728", "#2ca02c", "#9467bd", "#1f77b4",
  ];
  return colors[index % colors.length];
}
