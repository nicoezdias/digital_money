package com.digital.money.msvc.api.account.repository;

import com.digital.money.msvc.api.account.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

    @Repository
    public interface IAccountRepository extends JpaRepository<Account, Long> {
        Optional<Account> findByCvu(String cvu);
        Optional<Account> findById(Long id);
        Optional<Account> findByAlias(String alias);
        @Query("SELECT a FROM Account a WHERE a.alias = ?1 AND a.accountId != ?2")
        Optional<Account> aliasUnique(String alias, long id);


    }
