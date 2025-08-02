export class TransactionItem {
  transactionId: string;
  accountId: string;
  name: string;
  merchantName: string | null;
  category: string;
  amount: number;
  date: string;
  pending: boolean;
  paymentChannel: string;

  constructor(
    transactionId: string,
    accountId: string,
    name: string,
    merchantName: string | null,
    category: string,
    amount: number,
    date: string,
    pending: boolean,
    paymentChannel: string
  ) {
    this.transactionId = transactionId;
    this.accountId = accountId;
    this.name = name;
    this.merchantName = merchantName;
    this.category = category;
    this.amount = amount;
    this.date = date;
    this.pending = pending;
    this.paymentChannel = paymentChannel;
  }
}
