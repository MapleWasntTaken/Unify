package com.financialapp.financialapp.dto;

import java.time.LocalDate;

import com.financialapp.financialapp.model.AccountItem;

public class frontEndAccountItem {
    private String accountId;
    private String name;
    private String officialName;
    private String subtype;
    private Double currentBalance;
    private Double availableBalance;
    private Double creditLimit;

    private Double statementBalance;
    private LocalDate lastStatementDate;
    private LocalDate nextPaymentDueDate;
    private Double minimumPaymentAmount;

    public frontEndAccountItem(AccountItem ai) {
        this.accountId = ai.getAccountId();
        this.name = ai.getName();
        this.officialName = ai.getOfficialName();
        this.subtype = ai.getSubtype();
        this.currentBalance = ai.getCurrentBalance();
        this.availableBalance = ai.getAvailableBalance();
        this.creditLimit = ai.getCreditLimit();

        this.statementBalance = ai.getStatementBalance();
        this.lastStatementDate = ai.getLastStatementDate();
        this.nextPaymentDueDate = ai.getNextPaymentDueDate();
        this.minimumPaymentAmount = ai.getMinimumPaymentAmount();
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

    public Double getCreditLimit() {
        return creditLimit;
    }

    public void setCreditLimit(Double creditLimit) {
        this.creditLimit = creditLimit;
    }

    public Double getStatementBalance() {
        return statementBalance;
    }

    public void setStatementBalance(Double statementBalance) {
        this.statementBalance = statementBalance;
    }

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

    public Double getMinimumPaymentAmount() {
        return minimumPaymentAmount;
    }

    public void setMinimumPaymentAmount(Double minimumPaymentAmount) {
        this.minimumPaymentAmount = minimumPaymentAmount;
    }
}
