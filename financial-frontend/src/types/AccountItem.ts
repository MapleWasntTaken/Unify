export class AccountItem {
  accountId: string;
  name: string;
  officialName: string;
  subtype: string;
  currentBalance: number;
  availableBalance: number | null;
  creditLimit: number | null;

  constructor(
    accountId: string,
    name: string,
    officialName: string,
    subtype: string,
    currentBalance: number,
    availableBalance: number | null,
    creditLimit: number | null
  ) {
    this.accountId = accountId;
    this.name = name;
    this.officialName = officialName;
    this.subtype = subtype;
    this.currentBalance = currentBalance;
    this.availableBalance = availableBalance;
    this.creditLimit = creditLimit;
  }
}
