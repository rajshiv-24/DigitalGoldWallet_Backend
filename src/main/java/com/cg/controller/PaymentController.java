package com.cg.controller;

import com.cg.dto.PaymentResponseDTO;
import com.cg.entity.Payment;
import com.cg.enums.PaymentMethod;
import com.cg.enums.PaymentStatus;
import com.cg.enums.TransactionType;
import com.cg.exception.ResourceNotFoundException;
import com.cg.repo.PaymentRepository;
import com.cg.service.PaymentService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;
    private final PaymentRepository paymentRepository;

    public PaymentController(PaymentService paymentService, PaymentRepository paymentRepository) {
        this.paymentService = paymentService;
        this.paymentRepository = paymentRepository;
    }

    // ── ADMIN — aggregate views ───────────────────────────────

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<PaymentResponseDTO> getAllPayments() {
        return paymentService.getAllPayments();
    }

    @GetMapping("/{paymentId}")
    @PreAuthorize("hasRole('ADMIN')")
    public PaymentResponseDTO getPaymentById(@PathVariable Integer paymentId) {
        return paymentService.getPaymentById(paymentId);
    }

    @GetMapping("/by-status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    public List<PaymentResponseDTO> getPaymentsByStatus(@PathVariable PaymentStatus status) {
        return paymentRepository.findByPaymentStatus(status).stream().map(this::toDTO).toList();
    }

    @GetMapping("/by-method/{method}")
    @PreAuthorize("hasRole('ADMIN')")
    public List<PaymentResponseDTO> getPaymentsByMethod(@PathVariable PaymentMethod method) {
        return paymentRepository.findAll().stream()
                .filter(payment -> payment.getPaymentMethod() == method)
                .map(this::toDTO).toList();
    }

    @GetMapping("/by-type/{type}")
    @PreAuthorize("hasRole('ADMIN')")
    public List<PaymentResponseDTO> getPaymentsByType(@PathVariable TransactionType type) {
        return paymentRepository.findAll().stream()
                .filter(payment -> payment.getTransactionType() == type)
                .map(this::toDTO).toList();
    }

    @GetMapping("/count")
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, Long> countPayments() {
        return Map.of("count", paymentRepository.count());
    }

    @GetMapping("/{paymentId}/exists")
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, Boolean> paymentExists(@PathVariable Integer paymentId) {
        return Map.of("exists", paymentRepository.existsById(paymentId));
    }

    @GetMapping("/successful")
    @PreAuthorize("hasRole('ADMIN')")
    public List<PaymentResponseDTO> getSuccessfulPayments() {
        return getPaymentsByStatus(PaymentStatus.SUCCESS);
    }

    @GetMapping("/failed")
    @PreAuthorize("hasRole('ADMIN')")
    public List<PaymentResponseDTO> getFailedPayments() {
        return getPaymentsByStatus(PaymentStatus.FAILED);
    }

    // ── USER — own payment data ───────────────────────────────

    @GetMapping("/by-user/{userId}")
    @PreAuthorize("@userAccessService.canAccessUser(authentication, #userId)")
    public List<PaymentResponseDTO> getPaymentsByUser(@PathVariable Integer userId) {
        return paymentService.getPaymentsByUser(userId);
    }

    @GetMapping("/by-user/{userId}/total")
    @PreAuthorize("@userAccessService.canAccessUser(authentication, #userId)")
    public Map<String, Object> getPaymentTotalByUser(@PathVariable Integer userId) {
        BigDecimal total = paymentRepository.findByUserUserId(userId).stream()
                .map(Payment::getAmount).filter(a -> a != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return Map.of("userId", userId, "total", total);
    }

    private PaymentResponseDTO toDTO(Payment payment) {
        if (payment == null) throw new ResourceNotFoundException("Payment not found");
        PaymentResponseDTO dto = new PaymentResponseDTO();
        dto.setPaymentId(payment.getPaymentId());
        dto.setAmount(payment.getAmount());
        dto.setPaymentMethod(payment.getPaymentMethod());
        dto.setTransactionType(payment.getTransactionType());
        dto.setPaymentStatus(payment.getPaymentStatus());
        dto.setCreatedAt(payment.getCreatedAt());
        if (payment.getUser() != null) {
            dto.setUserId(payment.getUser().getUserId());
            dto.setUserName(payment.getUser().getName());
        }
        return dto;
    }
}
