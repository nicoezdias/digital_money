package com.digital.money.msvc.api.account.repository;

import com.digital.money.msvc.api.account.model.Transaction;
import com.digital.money.msvc.api.account.model.TransactionType;
import com.digital.money.msvc.api.account.model.projections.GetLastCVUs;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ITransactionRepository extends JpaRepository<Transaction, Long> {

    @Query("SELECT t FROM Transaction t WHERE t.account.accountId = :id ORDER BY t.realizationDate DESC LIMIT 5")
    Optional<List<Transaction>> getLastFive(@Param("id") Long id);
    Optional<Transaction> findByAccount_AccountIdAndTransactionId(Long accountId, Long transactionId);
    @Query("SELECT t FROM Transaction t WHERE t.account.accountId = :id ORDER BY t.realizationDate DESC")
    Optional<List<Transaction>> findAllSorted(@Param("id") Long id);

    @Query("select t from Transaction t where t.amount between ?1 and ?2 and t.account.accountId = ?3")
    List<Transaction> findByAmountBetweenAndAccount_AccountId(Double amountStart, Double amountEnd, Long accountId);

    @Query("select t from Transaction t where t.amount >= ?1 and t.account.accountId = ?2")
    List<Transaction> findByAmountGreaterThanEqualAndAccount_AccountId(Double amount, Long accountId);

    List<Transaction> findByAccount_AccountIdAndTypeOrderByRealizationDateDesc(Long accountId, TransactionType type, Pageable pageable);



    @Query(value = "SELECT to_cvu,realization_date,account_id,type FROM transactions GROUP by to_cvu HAVING account_id = ?1 AND type = 'OUTGOING' order by MAX(realization_date) DESC", nativeQuery = true)
    List<GetLastCVUs> findLastFiveReceivers(Long accountId, Pageable pageable);

}
