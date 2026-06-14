package com.cg.repo;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.cg.entity.TransactionHistory;
import com.cg.enums.TransactionStatus;

public interface TransactionHistoryRepository extends JpaRepository<TransactionHistory, Integer> {

    List<TransactionHistory> findByUserUserId(Integer userId);

    @Modifying
    @Query("delete from TransactionHistory t where t.user.userId = :userId")
    void deleteByUserId(@Param("userId") Integer userId);

    List<TransactionHistory> findByTransactionStatus(TransactionStatus status);
}
