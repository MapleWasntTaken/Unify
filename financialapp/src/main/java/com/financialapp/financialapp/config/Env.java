package com.financialapp.financialapp.config;

import io.github.cdimascio.dotenv.Dotenv;

public class Env {
    private static final Dotenv dotenv = Dotenv.configure().ignoreIfMalformed().ignoreIfMissing().load();

    public static final String PLAID_CLIENT_ID = dotenv.get("PLAID_CLIENT_ID");
    public static final String PLAID_SECRET = dotenv.get("PLAID_SECRET");
    public static final String PLAID_ENV = dotenv.get("PLAID_ENV");
    public static final String ENCRYPTION_KEY = dotenv.get("ENCRYPTION_KEY");
}