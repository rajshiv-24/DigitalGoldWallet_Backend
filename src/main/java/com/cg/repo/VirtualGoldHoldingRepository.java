package com.cg.repo;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.cg.entity.VirtualGoldHolding;

public interface VirtualGoldHoldingRepository extends JpaRepository<VirtualGoldHolding, Integer> {

    List<VirtualGoldHolding> findByUserUserId(Integer userId);

    @Modifying
    @Query("delete from VirtualGoldHolding v where v.user.userId = :userId")
    void deleteByUserId(@Param("userId") Integer userId);
}
