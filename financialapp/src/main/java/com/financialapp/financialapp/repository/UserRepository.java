package com.financialapp.financialapp.repository;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.financialapp.financialapp.model.ApplicationUser;

@Repository
public interface UserRepository extends JpaRepository<ApplicationUser,Integer>{
    
    Optional<ApplicationUser> findByUserId(Integer user_id);

    Optional<ApplicationUser> findByEmail(String email);

    
    @Query("SELECT u FROM ApplicationUser u LEFT JOIN FETCH u.plaidItems WHERE u.id = :id")
    ApplicationUser getUserWithPlaidItems(@Param("id") Long id);

}
