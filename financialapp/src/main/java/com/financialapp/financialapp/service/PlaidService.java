package com.financialapp.financialapp.service;


import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.financialapp.financialapp.config.Env;
import com.financialapp.financialapp.model.AccountItem;
import com.financialapp.financialapp.model.ApplicationUser;
import com.financialapp.financialapp.model.PlaidItem;
import com.financialapp.financialapp.model.TransactionItem;
import com.financialapp.financialapp.repository.AccountItemRepository;
import com.financialapp.financialapp.repository.PlaidItemRepository;
import com.financialapp.financialapp.repository.TransactionItemRepository;
import com.financialapp.financialapp.repository.UserRepository;

@Service
public class PlaidService {
    


    private final TransactionItemRepository transactionItemRepository;

    private final PlaidItemRepository plaidItemRepository;

    private final AccountItemRepository accountItemRepository;

    @Autowired
    private final CurrentUserService currentUserService;

    private final UserRepository userRepository;

    @Autowired
    private final RestTemplate restTemplate;

    public PlaidService(
        TransactionItemRepository transactionItemRepository,
        RestTemplate restTemplate,
        PlaidItemRepository plaidItemRepository,
        AccountItemRepository accountItemRepository,
        UserRepository userRepository,
        CurrentUserService currentUserService
        ){
        this.transactionItemRepository = transactionItemRepository;
        this.restTemplate = restTemplate;
        this.plaidItemRepository = plaidItemRepository;
        this.accountItemRepository = accountItemRepository;
        this.userRepository = userRepository;
        this.currentUserService = currentUserService;
    }

    @Transactional
    public void refreshPlaidItem(PlaidItem plaidItem){
        try {
            refreshPlaidItemAccounts(plaidItem);
            refreshPlaidItemTransactions(plaidItem);
        } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("Failed to sync Plaid item", e);
        }

        
        
    }
    @SuppressWarnings("UnnecessaryBoxing")
    public void refreshPlaidItemAccounts(PlaidItem plaidItem) {
        String accessToken = plaidItem.getAccessToken();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = Map.of(
            "client_id", Env.PLAID_CLIENT_ID,
            "secret", Env.PLAID_SECRET,
            "access_token", accessToken
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        ResponseEntity<String> accountGet = restTemplate.postForEntity(
            "https://production.plaid.com/accounts/get",
            request,
            String.class
        );

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(accountGet.getBody());
            ApplicationUser currentUser = currentUserService.getCurrentUser();
            JsonNode Accounts = root.get("accounts");

            // Optional: prepare liabilities map in advance
            Map<String, JsonNode> liabilitiesMap = new HashMap<>();
            ResponseEntity<String> liabilitiesGet = restTemplate.postForEntity(
                "https://production.plaid.com/liabilities/get",
                request,
                String.class
            );
            JsonNode liabilitiesRoot = mapper.readTree(liabilitiesGet.getBody());
            JsonNode creditLiabilities = liabilitiesRoot.path("liabilities").path("credit");

            for (JsonNode liab : creditLiabilities) {
                liabilitiesMap.put(liab.get("account_id").asText(), liab);
            }

            for (JsonNode ac : Accounts) {
                String plaidAccountId = ac.get("account_id").asText();
                Optional<AccountItem> existing = accountItemRepository
                    .findByPlaidItem_UserAndAccountId(currentUser, plaidAccountId);

                AccountItem ai = existing.orElse(new AccountItem());
                ai.setAccountId(plaidAccountId);
                ai.setPlaidItem(plaidItem);
                ai.setName(ac.get("name").asText());
                ai.setOfficialName(ac.get("official_name").asText());
                ai.setSubtype(ac.get("subtype").asText());
                ai.setMask(ac.get("mask").asText());

                JsonNode balances = ac.get("balances");
                ai.setCurrentBalance(balances.get("current").asDouble());

                if (ai.getSubtype().equals("credit card")) {
                    ai.setCreditLimit(balances.get("limit").asDouble());
                    ai.setAvailableBalance(0D);

                    
                    JsonNode liab = liabilitiesMap.get(plaidAccountId);
                    if (liab != null) {
                        ai.setStatementBalance(liab.path("last_statement_balance").asDouble());

                        String lastStatementStr = liab.path("last_statement_issue_date").asText(null);
                        if (lastStatementStr != null && !lastStatementStr.isBlank()) {
                            ai.setLastStatementDate(LocalDate.parse(lastStatementStr));
                        } else {
                            ai.setLastStatementDate(null);
                        }

                        String nextPaymentStr = liab.path("next_payment_due_date").asText(null);
                        if (nextPaymentStr != null && !nextPaymentStr.isBlank()) {
                            ai.setNextPaymentDueDate(LocalDate.parse(nextPaymentStr));
                        } else {
                            ai.setNextPaymentDueDate(null);
                        }

                        ai.setMinimumPaymentAmount(liab.path("minimum_payment_amount").asDouble());
                    }


                } else if (ai.getSubtype().equals("checking")) {
                    ai.setAvailableBalance(balances.get("available").asDouble());
                    ai.setCreditLimit(0D);
                }

                accountItemRepository.save(ai);
                plaidItem.addAccountItem(ai);
                plaidItemRepository.save(plaidItem);
            }

        } catch (Exception e) {
            System.err.println("Transaction sync failed: " + e.getMessage());
            e.printStackTrace();
        }
}

    public void refreshPlaidItemTransactions(PlaidItem plaidItem){

        String accessToken = plaidItem.getAccessToken();

        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> body = Map.of(
            "client_id", Env.PLAID_CLIENT_ID,
            "secret", Env.PLAID_SECRET,
            "access_token", accessToken,
            "start_date", LocalDate.now().minusDays(365).toString(),
            "end_date", LocalDate.now().toString()
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        ResponseEntity<String> transget = restTemplate.postForEntity(
            "https://production.plaid.com/transactions/get",
            request,
            String.class
        );
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(transget.getBody());
            JsonNode transactions = root.get("transactions");
            Map<String, AccountItem> accountItemMap = new HashMap<>();
            for (AccountItem account : plaidItem.getAccountItems()) {
                accountItemMap.put(account.getAccountId(), account);
                
            }

            for (JsonNode tx : transactions) {
                Optional<AccountItem> optionalAccountItem = accountItemRepository.findByPlaidItem_UserAndAccountId(plaidItem.getUser(),tx.get("account_id").asText());
                TransactionItem t = null;
                if (optionalAccountItem.isPresent()) {
                    AccountItem accountItem = optionalAccountItem.get();
                    t = transactionItemRepository.findByAccountItemAndTransactionId(accountItem,tx.get("transaction_id").asText()).orElse(new TransactionItem());
                    t.setAccountId(accountItem.getAccountId());
                
                    t.setName(tx.get("name").asText());

                    t.setAmount(tx.get("amount").asDouble());
                    t.setDate(LocalDate.parse(tx.get("date").asText()));
                    t.setCategory(tx.get("category") != null && tx.get("category").isArray()
                        ? tx.get("category").get(0).asText()
                        : "Uncategorized");
                    t.setTransactionId(tx.get("transaction_id").asText());
                    if (tx.has("iso_currency_code") && !tx.get("iso_currency_code").isNull()) {
                        t.setIsoCurrencyCode(tx.get("iso_currency_code").asText());
                    } else {
                        t.setIsoCurrencyCode("");
                    }

                    String txAccountId = tx.get("account_id").asText();
                    AccountItem txAcc = accountItemMap.get(txAccountId);
                    if(txAcc!=null){
                        t.setAccountItem(txAcc);
                        txAcc.addTransaction(t);
                        transactionItemRepository.save(t);
                        accountItemRepository.save(txAcc);
                    }
                }
            }
            

        } catch (Exception e) {
            System.err.println("Transaction sync failed: " + e.getMessage());
        }
    }

    public void removeItem(String s){
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        Map<String, Object> body = Map.of(
            "client_id", Env.PLAID_CLIENT_ID,
            "secret", Env.PLAID_SECRET,
            "access_token", s
        );
                HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        ResponseEntity<String> res = restTemplate.postForEntity(
            "https://production.plaid.com/item/remove",
            request,
            String.class
        );
        System.out.println(res.getBody()+"    res status:" + res.getStatusCode() );
    }



}
