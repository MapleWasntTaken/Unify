package com.financialapp.financialapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.financialapp.financialapp.model.AccountItem;
import com.financialapp.financialapp.model.ApplicationUser;
import com.financialapp.financialapp.model.TransactionItem;

@Repository
public interface TransactionItemRepository extends JpaRepository<TransactionItem, Long> {

    @Query("SELECT t FROM TransactionItem t WHERE t.accountItem.plaidItem.user = :user")
    List<TransactionItem> findAllByUser(ApplicationUser user);

    List<TransactionItem> findByAccountItem(AccountItem accountItem);

    Optional<TransactionItem> findByAccountItemAndTransactionId(AccountItem accountItem,String transactionID);
    
}