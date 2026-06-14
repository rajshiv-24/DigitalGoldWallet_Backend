package com.cg.repo;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.cg.entity.PhysicalGoldTransaction;

public interface PhysicalGoldTransactionRepository extends JpaRepository<PhysicalGoldTransaction, Integer> {

    List<PhysicalGoldTransaction> findByUserUserId(Integer userId);

    @Modifying
    @Query("delete from PhysicalGoldTransaction p where p.user.userId = :userId")
    void deleteByUserId(@Param("userId") Integer userId);
}
