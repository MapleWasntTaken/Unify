package com.financialapp.financialapp.config;

public enum PlaidEnvironment {
    SANDBOX("https://sandbox.plaid.com"),
    PROD("https://production.plaid.com");

    private final String baseUrl;

    PlaidEnvironment(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String baseUrl() {
        return baseUrl;
    }
}
