export class AccountItem {
  accountId: string;
  name: string;
  officialName: string;
  subtype: string;
  currentBalance: number;
  availableBalance: number | null;
  creditLimit: number | null;
  mask: string | null;

  statementBalance: number | null;
  lastStatementDate: string | null;
  nextPaymentDueDate: string | null;
  minimumPaymentAmount: number | null;

  constructor(
    accountId: string,
    name: string,
    officialName: string,
    subtype: string,
    currentBalance: number,
    availableBalance: number | null,
    creditLimit: number | null,
    mask: string | null,
    statementBalance: number | null,
    lastStatementDate: string | null,
    nextPaymentDueDate: string | null,
    minimumPaymentAmount: number | null
  ) {
    this.accountId = accountId;
    this.name = name;
    this.officialName = officialName;
    this.subtype = subtype;
    this.currentBalance = currentBalance;
    this.availableBalance = availableBalance;
    this.creditLimit = creditLimit;
    this.mask = mask;

    this.statementBalance = statementBalance;
    this.lastStatementDate = lastStatementDate;
    this.nextPaymentDueDate = nextPaymentDueDate;
    this.minimumPaymentAmount = minimumPaymentAmount;
  }
}

