package org.example.repository;

import org.example.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByAccountId(Long accountId);
    List<Transaction> findByAccountIdAndUpdateTimeGreaterThan(Long accountId, Timestamp updateTimeEnd);

    @Query(value = "SELECT t.id FROM TRANSACTIONS t WHERE t.account_id = :accountId ORDER BY t.update_time desc LIMIT 1",
            nativeQuery = true)
    Optional<Long> findLastId(@Param("accountId") Long accountId);
}
