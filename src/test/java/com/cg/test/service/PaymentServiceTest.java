package com.cg.test.service;

import com.cg.dto.PaymentResponseDTO;
import com.cg.entity.Payment;
import com.cg.entity.User;
import com.cg.enums.PaymentMethod;
import com.cg.enums.PaymentStatus;
import com.cg.enums.TransactionType;
import com.cg.exception.ResourceNotFoundException;
import com.cg.repo.PaymentRepository;
import com.cg.service.PaymentService;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@SpringBootTest
public class PaymentServiceTest {

    @MockitoBean
    private PaymentRepository paymentRepo;

    @Autowired
    private PaymentService paymentService;

    private Payment payment;
    private User user;

    @BeforeEach
    public void beforeEach() {
        user = new User();
        user.setUserId(1);
        user.setName("Rahul Sharma");
        user.setEmail("rahul@test.com");

        payment = new Payment();
        payment.setPaymentId(101);
        payment.setUser(user);
        payment.setAmount(new BigDecimal("5000.00"));
        payment.setPaymentMethod(PaymentMethod.CREDIT_CARD);
        payment.setTransactionType(TransactionType.CREDITED_TO_WALLET);
        payment.setPaymentStatus(PaymentStatus.SUCCESS);
        payment.setCreatedAt(LocalDateTime.now());
    }

    // ── getPaymentsByUser ──────────────────────────────────────

    @Test
    public void testGetPaymentsByUser_ReturnsPayments() {
        Mockito.when(paymentRepo.findByUserUserId(1)).thenReturn(List.of(payment));

        List<PaymentResponseDTO> result = paymentService.getPaymentsByUser(1);

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(101, result.get(0).getPaymentId());
        Mockito.verify(paymentRepo).findByUserUserId(1);
    }

    @Test
    public void testGetPaymentsByUser_EmptyList() {
        Mockito.when(paymentRepo.findByUserUserId(99)).thenReturn(List.of());

        List<PaymentResponseDTO> result = paymentService.getPaymentsByUser(99);

        Assertions.assertTrue(result.isEmpty());
        Mockito.verify(paymentRepo).findByUserUserId(99);
    }

    // ── getAllPayments ─────────────────────────────────────────

    @Test
    public void testGetAllPayments_ReturnsList() {
        Mockito.when(paymentRepo.findAll()).thenReturn(List.of(payment));

        List<PaymentResponseDTO> result = paymentService.getAllPayments();

        Assertions.assertEquals(1, result.size());
        Mockito.verify(paymentRepo).findAll();
    }

    @Test
    public void testGetAllPayments_EmptyList() {
        Mockito.when(paymentRepo.findAll()).thenReturn(List.of());

        List<PaymentResponseDTO> result = paymentService.getAllPayments();

        Assertions.assertTrue(result.isEmpty());
        Mockito.verify(paymentRepo).findAll();
    }

    // ── getPaymentById ─────────────────────────────────────────

    @Test
    public void testGetPaymentById_Found() {
        Mockito.when(paymentRepo.findById(101)).thenReturn(Optional.of(payment));

        PaymentResponseDTO result = paymentService.getPaymentById(101);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(101, result.getPaymentId());
        Assertions.assertEquals(new BigDecimal("5000.00"), result.getAmount());
        Mockito.verify(paymentRepo).findById(101);
    }

    @Test
    public void testGetPaymentById_NotFound() {
        Mockito.when(paymentRepo.findById(999)).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class,
                () -> paymentService.getPaymentById(999));

        Mockito.verify(paymentRepo).findById(999);
    }
}  