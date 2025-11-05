package com.financialapp.financialapp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.financialapp.financialapp.model.ApplicationUser;
import com.financialapp.financialapp.model.PlaidItem;


@Repository
public interface PlaidItemRepository extends JpaRepository<PlaidItem, Long> {
    List<PlaidItem> findByUser(ApplicationUser user);

    Optional<PlaidItem> findByUserAndAccessToken(ApplicationUser applicationUser, String accessToken);

    Optional<PlaidItem> findByPlaidItemId(String itemId);

    void deleteByPlaidItemId(String plaidItemId);

    @Query("""
        select p
        from PlaidItem p
        join p.accountItems a
        where a.accountId = :accountId
    """)
    Optional<PlaidItem> findByAccountItemId(@Param("accountId") String accountId);
}