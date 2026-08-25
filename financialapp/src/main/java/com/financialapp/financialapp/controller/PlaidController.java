package com.financialapp.financialapp.controller;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.financialapp.financialapp.model.AccountItem;
import com.financialapp.financialapp.model.ApplicationUser;
import com.financialapp.financialapp.model.PlaidItem;
import com.financialapp.financialapp.repository.AccountItemRepository;
import com.financialapp.financialapp.repository.PlaidItemRepository;
import com.financialapp.financialapp.service.CurrentUserService;
import com.financialapp.financialapp.service.PlaidApiClient;
import com.financialapp.financialapp.service.PlaidService;

@RestController
@RequestMapping("/api/plaid")
public class PlaidController {
    private static final Logger log = LoggerFactory.getLogger(PlaidController.class);

    private final CurrentUserService currentUserService;
    private final PlaidItemRepository plaidItemRepository;
    private final AccountItemRepository accountItemRepository;
    private final PlaidService plaidService;
    private final PlaidApiClient plaidApiClient;

    public PlaidController(
        CurrentUserService currentUserService,
        PlaidItemRepository plaidItemRepository,
        AccountItemRepository accountItemRepository,
        PlaidService plaidService,
        PlaidApiClient plaidApiClient
    ) {
        this.currentUserService = currentUserService;
        this.plaidItemRepository = plaidItemRepository;
        this.accountItemRepository = accountItemRepository;
        this.plaidService = plaidService;
        this.plaidApiClient = plaidApiClient;
    }

    @GetMapping("/create-link-token")
    public ResponseEntity<JsonNode> createLinkToken() {
        Integer userId = currentUserService.getCurrentUserId();
        log.info("Creating Plaid Link token userId={} environment={}",
            userId, plaidApiClient.properties().environment());

        JsonNode response = plaidApiClient.post("link-token-create", "/link/token/create", Map.of(
            "user", Map.of("client_user_id", "user-" + userId),
            "client_name", plaidApiClient.properties().clientName(),
            "products", List.of("transactions", "liabilities", "assets"),
            "country_codes", List.of("US", "CA"),
            "language", "en",
            "webhook", plaidApiClient.properties().webhookUrl().toString()
        ));
        return ResponseEntity.ok(response);
    }

    @PostMapping("/create-update-token")
    public ResponseEntity<JsonNode> createUpdateToken(@RequestBody Map<String, String> payload) {
        String accountId = required(payload, "accountId");
        ApplicationUser currentUser = currentUserService.getCurrentUser();
        PlaidItem plaidItem = plaidItemRepository.findByAccountItemId(accountId)
            .filter(item -> item.getUser().getUserId().equals(currentUser.getUserId()))
            .orElseThrow(() -> new IllegalArgumentException("Account was not found"));

        plaidApiClient.validateTokenEnvironment("update-link-token-create", plaidItem.getAccessToken());
        log.info("Creating Plaid update token userId={} plaidItemId={} environment={}",
            currentUser.getUserId(), plaidItem.getPlaidItemId(), plaidApiClient.properties().environment());

        JsonNode response = plaidApiClient.post("update-link-token-create", "/link/token/create", Map.of(
            "user", Map.of("client_user_id", "user-" + currentUser.getUserId()),
            "update", Map.of("account_selection_enabled", true),
            "access_token", plaidItem.getAccessToken(),
            "client_name", plaidApiClient.properties().clientName(),
            "country_codes", List.of("US", "CA"),
            "language", "en",
            "webhook", plaidApiClient.properties().webhookUrl().toString()
        ));
        return ResponseEntity.ok(response);
    }

    @PostMapping("/exchange-public-token")
    public ResponseEntity<Map<String, String>> exchangePublicToken(@RequestBody Map<String, String> payload) {
        String publicToken = required(payload, "public_token");
        plaidApiClient.validateTokenEnvironment("public-token-exchange", publicToken);

        ApplicationUser currentUser = currentUserService.getCurrentUser();
        log.info("Exchanging Plaid public token userId={} environment={}",
            currentUser.getUserId(), plaidApiClient.properties().environment());
        JsonNode response = plaidApiClient.post("public-token-exchange", "/item/public_token/exchange", Map.of(
            "public_token", publicToken
        ));

        JsonNode accessTokenNode = response.get("access_token");
        JsonNode itemIdNode = response.get("item_id");
        if (accessTokenNode == null || itemIdNode == null) {
            throw new IllegalStateException("Plaid exchange response omitted required fields");
        }

        String accessToken = accessTokenNode.asText();
        String plaidItemId = itemIdNode.asText();
        PlaidItem plaidItem = plaidItemRepository
            .findByUserAndAccessToken(currentUser, accessToken)
            .orElseGet(PlaidItem::new);
        plaidItem.setUser(currentUser);
        plaidItem.setPlaidItemId(plaidItemId);
        plaidItem.setAccessToken(accessToken);
        plaidItem.setInstitutionId(payload.get("institution_id"));
        plaidItem.setInstitutionName(payload.get("institution_name"));
        plaidItem.setFilled(false);
        plaidItem.setUpdate(false);
        plaidItemRepository.save(plaidItem);

        log.info("Plaid item connected userId={} plaidItemId={}", currentUser.getUserId(), plaidItemId);
        return ResponseEntity.ok(Map.of("status", "connected", "item_id", plaidItemId));
    }

    @Transactional
    @PostMapping("/remove-item")
    public ResponseEntity<Map<String, String>> removeItem(@RequestBody String accountId) {
        if (accountId == null || accountId.isBlank()) {
            throw new IllegalArgumentException("accountId is required");
        }
        ApplicationUser currentUser = currentUserService.getCurrentUser();
        AccountItem account = accountItemRepository
            .findByPlaidItem_UserAndAccountId(currentUser, accountId)
            .orElseThrow(() -> new IllegalArgumentException("Account was not found"));

        PlaidItem plaidItem = account.getPlaidItem();
        if (plaidItem.getAccountItems().size() == 1) {
            plaidService.removeItem(plaidItem.getAccessToken());
            plaidItemRepository.deleteByPlaidItemId(plaidItem.getPlaidItemId());
            plaidItem.getUser().removePlaidItem(plaidItem);
            log.info("Plaid item removed userId={} plaidItemId={}",
                currentUser.getUserId(), plaidItem.getPlaidItemId());
            return ResponseEntity.ok(Map.of("status", "item_removed"));
        }

        accountItemRepository.deleteByaccountId(account.getAccountId());
        plaidItem.removeAccountItem(account);
        log.info("Plaid account removed userId={} plaidItemId={}",
            currentUser.getUserId(), plaidItem.getPlaidItemId());
        return ResponseEntity.ok(Map.of("status", "account_removed"));
    }

    private static String required(Map<String, String> payload, String field) {
        String value = payload.get(field);
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " is required");
        }
        return value;
    }
}
