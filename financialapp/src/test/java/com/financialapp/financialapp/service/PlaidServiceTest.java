package com.financialapp.financialapp.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.financialapp.financialapp.model.AccountItem;
import com.financialapp.financialapp.model.PlaidItem;
import com.financialapp.financialapp.model.TransactionItem;
import com.financialapp.financialapp.repository.AccountItemRepository;
import com.financialapp.financialapp.repository.PlaidItemRepository;
import com.financialapp.financialapp.repository.TransactionItemRepository;

class PlaidServiceTest {
    private final TransactionItemRepository transactionRepository = mock(TransactionItemRepository.class);
    private final PlaidItemRepository plaidItemRepository = mock(PlaidItemRepository.class);
    private final AccountItemRepository accountRepository = mock(AccountItemRepository.class);
    private final CurrentUserService currentUserService = mock(CurrentUserService.class);
    private final PlaidApiClient plaidApiClient = mock(PlaidApiClient.class);
    private final PlaidService service = new PlaidService(
        transactionRepository,
        plaidItemRepository,
        accountRepository,
        currentUserService,
        plaidApiClient
    );

    @Test
    void updatingAnExistingTransactionDoesNotAppendItAgain() throws Exception {
        PlaidItem plaidItem = plaidItemWithAccount();
        AccountItem account = plaidItem.getAccountItems().getFirst();
        TransactionItem existing = new TransactionItem();
        existing.setTransactionId("transaction-1");
        account.addTransaction(existing);

        when(plaidApiClient.post(any(), any(), any())).thenReturn(new ObjectMapper().readTree("""
            {"transactions":[{
              "account_id":"account-1",
              "transaction_id":"transaction-1",
              "name":"Updated merchant",
              "amount":12.34,
              "date":"2026-08-06",
              "category":["Food"],
              "iso_currency_code":"CAD"
            }]}
            """));
        when(transactionRepository.findByAccountItemAndTransactionId(account, "transaction-1"))
            .thenReturn(Optional.of(existing));

        service.refreshPlaidItemTransactions(plaidItem);

        assertThat(account.getTransactions()).containsExactly(existing);
        assertThat(existing.getName()).isEqualTo("Updated merchant");
        verify(transactionRepository).save(existing);
    }

    @Test
    void aNewTransactionIsAddedToItsAccount() throws Exception {
        PlaidItem plaidItem = plaidItemWithAccount();
        AccountItem account = plaidItem.getAccountItems().getFirst();
        when(plaidApiClient.post(any(), any(), any())).thenReturn(new ObjectMapper().readTree("""
            {"transactions":[{
              "account_id":"account-1",
              "transaction_id":"transaction-1",
              "name":"Merchant",
              "amount":12.34,
              "date":"2026-08-06",
              "category":[],
              "iso_currency_code":"CAD"
            }]}
            """));
        when(transactionRepository.findByAccountItemAndTransactionId(account, "transaction-1"))
            .thenReturn(Optional.empty());

        service.refreshPlaidItemTransactions(plaidItem);

        assertThat(account.getTransactions()).hasSize(1);
        assertThat(account.getTransactions().getFirst().getAccountItem()).isSameAs(account);
        assertThat(account.getTransactions().getFirst().getCategory()).isEqualTo("Uncategorized");
    }

    private static PlaidItem plaidItemWithAccount() {
        PlaidItem plaidItem = new PlaidItem();
        plaidItem.setAccessToken("access-sandbox-redacted");
        AccountItem account = new AccountItem();
        account.setAccountId("account-1");
        account.setPlaidItem(plaidItem);
        plaidItem.addAccountItem(account);
        return plaidItem;
    }
}
