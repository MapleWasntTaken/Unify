package com.financialapp.financialapp.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

@Entity
public class AccountItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String accountId;
    private String name;
    private String officialName;
    private String subtype;
    private Double currentBalance;
    private Double availableBalance;
    private Double creditLimit;
    private String mask;
    private LocalDate lastStatementDate;
    private LocalDate nextPaymentDueDate;
    private Double statementBalance;
    private Double minimumPaymentAmount;

    @ManyToOne
    @JoinColumn(name = "plaid_item_id")
    @JsonBackReference
    private PlaidItem plaidItem;

    @OneToMany(mappedBy = "accountItem", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private final List<TransactionItem> transactions = new ArrayList<>();

    

    public LocalDate getLastStatementDate() {
        return lastStatementDate;
    }

    public void setLastStatementDate(LocalDate lastStatementDate) {
        this.lastStatementDate = lastStatementDate;
    }

    public LocalDate getNextPaymentDueDate() {
        return nextPaymentDueDate;
    }

    public void setNextPaymentDueDate(LocalDate nextPaymentDueDate) {
        this.nextPaymentDueDate = nextPaymentDueDate;
    }

    public Double getStatementBalance() {
        return statementBalance;
    }

    public void setStatementBalance(Double statementBalance) {
        this.statementBalance = statementBalance;
    }

    public Double getMinimumPaymentAmount() {
        return minimumPaymentAmount;
    }

    public void setMinimumPaymentAmount(Double minimumPaymentAmount) {
        this.minimumPaymentAmount = minimumPaymentAmount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOfficialName() {
        return officialName;
    }

    public void setOfficialName(String officialName) {
        this.officialName = officialName;
    }


    public String getSubtype() {
        return subtype;
    }

    public void setSubtype(String subtype) {
        this.subtype = subtype;
    }

    public Double getCurrentBalance() {
        return currentBalance;
    }

    public void setCurrentBalance(Double currentBalance) {
        this.currentBalance = currentBalance;
    }

    public Double getAvailableBalance() {
        return availableBalance;
    }

    public void setAvailableBalance(Double availableBalance) {
        this.availableBalance = availableBalance;
    }

    public PlaidItem getPlaidItem() {
        return plaidItem;
    }

    public void setPlaidItem(PlaidItem plaidItem) {
        this.plaidItem = plaidItem;
    }


    
    public List<TransactionItem> getTransactions() {
        return this.transactions;
    }

    public void addTransaction(TransactionItem transactionItem){
        this.transactions.add(transactionItem);
        transactionItem.setAccountItem(this);
    }
    public void removeTransaction(TransactionItem transactionItem){
        this.transactions.remove(transactionItem);
        transactionItem.setAccountItem(null);
    }

    public Double getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(Double creditLimit) {
        this.creditLimit = creditLimit;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AccountItem that = (AccountItem) o;
        return Objects.equals(accountId, that.accountId) &&
            Objects.equals(plaidItem.getId(), that.plaidItem.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(accountId, plaidItem.getId());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("AccountItem{");
        sb.append("id=").append(id);
        sb.append(", accountId=").append(accountId);
        sb.append(", name=").append(name);
        sb.append(", officialName=").append(officialName);
        sb.append(", subtype=").append(subtype);
        sb.append(", currentBalance=").append(currentBalance);
        sb.append(", availableBalance=").append(availableBalance);
        sb.append(", creditLimit=").append(creditLimit);
        sb.append('}');
        return sb.toString();
    }

    public String getMask() {
        return mask;
    }

    public void setMask(String mask) {
        this.mask = mask;
    }
}
