package com.financialapp.financialapp.dto;

import com.financialapp.financialapp.model.TransactionItem;

public class frontEndTransactionItem {
    private String transactionId;
    private String name;
    private String category;
    private String accountId;
    private String isoCurrencyCode;
    private Double amount;
    private String date;

    public frontEndTransactionItem(TransactionItem ti) {
        this.transactionId = ti.getTransactionId();
        this.name = ti.getName();
        this.category = ti.getCategory();
        this.accountId = ti.getAccountId();
        this.isoCurrencyCode = ti.getIsoCurrencyCode();
        this.amount = ti.getAmount();
        this.date = ti.getDate() != null ? ti.getDate().toString() : null;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public String getIsoCurrencyCode() {
        return isoCurrencyCode;
    }

    public void setIsoCurrencyCode(String isoCurrencyCode) {
        this.isoCurrencyCode = isoCurrencyCode;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
