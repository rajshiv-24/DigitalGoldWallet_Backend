package com.cg.repo;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.cg.entity.Payment;
import com.cg.enums.PaymentStatus;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {

    List<Payment> findByUserUserId(Integer userId);

    @Modifying
    @Query("delete from Payment p where p.user.userId = :userId")
    void deleteByUserId(@Param("userId") Integer userId);

    List<Payment> findByPaymentStatus(PaymentStatus status);
}
