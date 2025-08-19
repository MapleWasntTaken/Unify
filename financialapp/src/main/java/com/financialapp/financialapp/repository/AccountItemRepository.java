package com.financialapp.financialapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.financialapp.financialapp.model.AccountItem;
import com.financialapp.financialapp.model.ApplicationUser;
import com.financialapp.financialapp.model.PlaidItem;

@Repository
public interface AccountItemRepository extends JpaRepository<AccountItem, Long> {

    // 🔍 Find all accounts for a given PlaidItem
    List<AccountItem> findByPlaidItem(PlaidItem plaidItem);

    // 🔍 Find one account by its Plaid account ID
    Optional<AccountItem> findByAccountId(String accountId);

    Optional<AccountItem> findByPlaidItem_UserAndAccountId(ApplicationUser user, String accountId);

    // 🔍 Find all accounts for a given User (through the PlaidItem relationship)
    @Query("SELECT a FROM AccountItem a WHERE a.plaidItem.user = :user")
    List<AccountItem> findAllByUser(@Param("user") ApplicationUser user);

    // ❌ Delete all accounts under a specific PlaidItem (e.g. when unlinking)
    void deleteByPlaidItem(PlaidItem plaidItem);

    //@Query("SELECT a FROM AccountItem WHERE a.account_id = :accountId AND p CONTAINING  ")
    void deleteByaccountId(String accountId);

    // ✅ Check if an account already exists (for sync deduping)
    boolean existsByAccountId(String accountId);
}

