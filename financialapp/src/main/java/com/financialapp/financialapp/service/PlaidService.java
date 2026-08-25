package com.financialapp.financialapp.service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.financialapp.financialapp.model.AccountItem;
import com.financialapp.financialapp.model.ApplicationUser;
import com.financialapp.financialapp.model.PlaidItem;
import com.financialapp.financialapp.model.TransactionItem;
import com.financialapp.financialapp.repository.AccountItemRepository;
import com.financialapp.financialapp.repository.PlaidItemRepository;
import com.financialapp.financialapp.repository.TransactionItemRepository;

@Service
public class PlaidService {
    private static final Logger log = LoggerFactory.getLogger(PlaidService.class);

    private final TransactionItemRepository transactionItemRepository;
    private final PlaidItemRepository plaidItemRepository;
    private final AccountItemRepository accountItemRepository;
    private final CurrentUserService currentUserService;
    private final PlaidApiClient plaidApiClient;

    public PlaidService(
        TransactionItemRepository transactionItemRepository,
        PlaidItemRepository plaidItemRepository,
        AccountItemRepository accountItemRepository,
        CurrentUserService currentUserService,
        PlaidApiClient plaidApiClient
    ) {
        this.transactionItemRepository = transactionItemRepository;
        this.plaidItemRepository = plaidItemRepository;
        this.accountItemRepository = accountItemRepository;
        this.currentUserService = currentUserService;
        this.plaidApiClient = plaidApiClient;
    }

    @Transactional
    public void refreshPlaidItem(PlaidItem plaidItem) {
        log.info("Plaid synchronization started plaidItemId={}", plaidItem.getPlaidItemId());
        refreshPlaidItemAccounts(plaidItem);
        refreshPlaidItemTransactions(plaidItem);
        plaidItem.setFilled(true);
        plaidItem.setUpdate(false);
        plaidItemRepository.save(plaidItem);
        log.info("Plaid synchronization succeeded plaidItemId={} accountCount={}",
            plaidItem.getPlaidItemId(), plaidItem.getAccountItems().size());
    }

    public void refreshPlaidItemAccounts(PlaidItem plaidItem) {
        String accessToken = plaidItem.getAccessToken();
        plaidApiClient.validateTokenEnvironment("accounts-get", accessToken);
        Map<String, Object> body = Map.of("access_token", accessToken);

        JsonNode accountsRoot = plaidApiClient.post("accounts-get", "/accounts/get", body);
        JsonNode liabilitiesRoot = plaidApiClient.post("liabilities-get", "/liabilities/get", body);
        JsonNode accounts = requiredArray(accountsRoot, "accounts", "accounts-get");
        JsonNode creditLiabilities = liabilitiesRoot.path("liabilities").path("credit");

        Map<String, JsonNode> liabilitiesByAccount = new HashMap<>();
        if (creditLiabilities.isArray()) {
            for (JsonNode liability : creditLiabilities) {
                liabilitiesByAccount.put(requiredText(liability, "account_id"), liability);
            }
        }

        ApplicationUser currentUser = currentUserService.getCurrentUser();
        for (JsonNode accountNode : accounts) {
            String accountId = requiredText(accountNode, "account_id");
            AccountItem account = accountItemRepository
                .findByPlaidItem_UserAndAccountId(currentUser, accountId)
                .orElseGet(AccountItem::new);

            account.setAccountId(accountId);
            account.setPlaidItem(plaidItem);
            account.setName(textOrNull(accountNode, "name"));
            account.setOfficialName(textOrNull(accountNode, "official_name"));
            account.setSubtype(textOrNull(accountNode, "subtype"));
            account.setMask(textOrNull(accountNode, "mask"));

            JsonNode balances = accountNode.path("balances");
            account.setCurrentBalance(doubleOrNull(balances, "current"));
            account.setAvailableBalance(doubleOrNull(balances, "available"));
            account.setCreditLimit(doubleOrNull(balances, "limit"));

            JsonNode liability = liabilitiesByAccount.get(accountId);
            if (liability != null) {
                account.setStatementBalance(doubleOrNull(liability, "last_statement_balance"));
                account.setLastStatementDate(dateOrNull(liability, "last_statement_issue_date"));
                account.setNextPaymentDueDate(dateOrNull(liability, "next_payment_due_date"));
                account.setMinimumPaymentAmount(doubleOrNull(liability, "minimum_payment_amount"));
            }

            accountItemRepository.save(account);
            plaidItem.addAccountItem(account);
        }
        plaidItemRepository.save(plaidItem);
    }

    public void refreshPlaidItemTransactions(PlaidItem plaidItem) {
        String accessToken = plaidItem.getAccessToken();
        plaidApiClient.validateTokenEnvironment("transactions-get", accessToken);
        JsonNode root = plaidApiClient.post("transactions-get", "/transactions/get", Map.of(
            "access_token", accessToken,
            "start_date", LocalDate.now().minusDays(365).toString(),
            "end_date", LocalDate.now().toString()
        ));
        JsonNode transactions = requiredArray(root, "transactions", "transactions-get");

        Map<String, AccountItem> accountsById = new HashMap<>();
        for (AccountItem account : plaidItem.getAccountItems()) {
            accountsById.put(account.getAccountId(), account);
        }

        for (JsonNode transactionNode : transactions) {
            String accountId = requiredText(transactionNode, "account_id");
            AccountItem account = accountsById.get(accountId);
            if (account == null) {
                log.warn("Plaid transaction referenced an unknown account plaidItemId={}",
                    plaidItem.getPlaidItemId());
                continue;
            }

            String transactionId = requiredText(transactionNode, "transaction_id");
            TransactionItem transaction = transactionItemRepository
                .findByAccountItemAndTransactionId(account, transactionId)
                .orElse(null);
            boolean newTransaction = transaction == null;
            if (newTransaction) {
                transaction = new TransactionItem();
            }
            transaction.setAccountId(accountId);
            transaction.setTransactionId(transactionId);
            transaction.setName(textOrNull(transactionNode, "name"));
            transaction.setAmount(doubleOrNull(transactionNode, "amount"));
            transaction.setDate(LocalDate.parse(requiredText(transactionNode, "date")));
            transaction.setCategory(firstCategory(transactionNode));
            transaction.setIsoCurrencyCode(textOrEmpty(transactionNode, "iso_currency_code"));
            if (newTransaction) {
                account.addTransaction(transaction);
            }
            transactionItemRepository.save(transaction);
        }
    }

    public void removeItem(String accessToken) {
        plaidApiClient.validateTokenEnvironment("item-remove", accessToken);
        plaidApiClient.post("item-remove", "/item/remove", Map.of("access_token", accessToken));
    }

    private static JsonNode requiredArray(JsonNode root, String field, String operation) {
        JsonNode value = root.get(field);
        if (value == null || !value.isArray()) {
            throw new IllegalStateException("Plaid response for " + operation + " omitted " + field);
        }
        return value;
    }

    private static String requiredText(JsonNode node, String field) {
        String value = textOrNull(node, field);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Plaid response omitted " + field);
        }
        return value;
    }

    private static String textOrNull(JsonNode node, String field) {
        JsonNode value = node.get(field);
        return value == null || value.isNull() ? null : value.asText();
    }

    private static String textOrEmpty(JsonNode node, String field) {
        String value = textOrNull(node, field);
        return value == null ? "" : value;
    }

    private static Double doubleOrNull(JsonNode node, String field) {
        JsonNode value = node.get(field);
        return value == null || value.isNull() ? null : value.asDouble();
    }

    private static LocalDate dateOrNull(JsonNode node, String field) {
        String value = textOrNull(node, field);
        return value == null || value.isBlank() ? null : LocalDate.parse(value);
    }

    private static String firstCategory(JsonNode transaction) {
        JsonNode category = transaction.get("category");
        return category != null && category.isArray() && !category.isEmpty()
            ? category.get(0).asText()
            : "Uncategorized";
    }
}
