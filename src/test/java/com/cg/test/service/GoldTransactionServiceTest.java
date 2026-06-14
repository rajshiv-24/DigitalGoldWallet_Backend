package com.cg.test.service;

import com.cg.dto.BuyGoldRequestDTO;
import com.cg.dto.SellGoldRequestDTO;
import com.cg.dto.TransactionHistoryResponseDTO;
import com.cg.dto.VirtualGoldHoldingResponseDTO;
import com.cg.entity.*;
import com.cg.enums.PaymentMethod;
import com.cg.enums.TransactionStatus;
import com.cg.enums.TransactionType2;
import com.cg.exception.InsufficientBalanceException;
import com.cg.exception.InsufficientGoldException;
import com.cg.exception.ResourceNotFoundException;
import com.cg.repo.*;
import com.cg.service.GoldTransactionService;
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
public class GoldTransactionServiceTest {

    @MockitoBean
    private UserRepository userRepo;

    @MockitoBean
    private VendorBranchRepository branchRepo;

    @MockitoBean
    private VirtualGoldHoldingRepository holdingRepo;

    @MockitoBean
    private PhysicalGoldTransactionRepository physicalRepo;

    @MockitoBean
    private TransactionHistoryRepository historyRepo;

    @MockitoBean
    private PaymentRepository paymentRepo;

    @MockitoBean
    private AddressRepository addressRepo;

    @Autowired
    private GoldTransactionService goldTransactionService;

    private User user;
    private Vendors vendor;
    private VendorBranch branch;
    private VirtualGoldHolding holding;
    private TransactionHistory transactionHistory;

    @BeforeEach
    public void beforeEach() {
        vendor = new Vendors();
        vendor.setVendorId(1L);
        vendor.setVendorName("Gold Palace");
        vendor.setCurrentGoldPrice(6000.0);

        Address branchAddress = new Address();
        branchAddress.setCity("Delhi");

        branch = new VendorBranch();
        branch.setBranchId(10);
        branch.setVendor(vendor);
        branch.setAddress(branchAddress);
        branch.setQuantity(new BigDecimal("100.00")); // 100g in stock

        user = new User();
        user.setUserId(1);
        user.setName("Priya Mehta");
        user.setEmail("priya@test.com");
        user.setBalance(new BigDecimal("100000.00")); // Rs 1 lakh

        holding = new VirtualGoldHolding();
        holding.setHoldingId(50);
        holding.setUser(user);
        holding.setBranch(branch);
        holding.setQuantity(new BigDecimal("10.00")); // 10g held
        holding.setCreatedAt(LocalDateTime.now());

        transactionHistory = new TransactionHistory();
        transactionHistory.setTransactionId(200);
        transactionHistory.setUser(user);
        transactionHistory.setBranch(branch);
        transactionHistory.setTransactionType(TransactionType2.BUY);
        transactionHistory.setTransactionStatus(TransactionStatus.SUCCESS);
        transactionHistory.setQuantity(new BigDecimal("5.00"));
        transactionHistory.setAmount(new BigDecimal("30000.00"));
        transactionHistory.setCreatedAt(LocalDateTime.now());
    }

    // ── buyGold ────────────────────────────────────────────────

    @Test
    public void testBuyGold_Success_NewHolding() {
        BuyGoldRequestDTO request = new BuyGoldRequestDTO();
        request.setUserId(1);
        request.setBranchId(10);
        request.setQuantity(new BigDecimal("5.00"));
        request.setPaymentMethod(PaymentMethod.CREDIT_CARD);

        Mockito.when(userRepo.findById(1)).thenReturn(Optional.of(user));
        Mockito.when(branchRepo.findById(10)).thenReturn(Optional.of(branch));
        Mockito.when(holdingRepo.findByUserUserId(1)).thenReturn(List.of()); // no existing holding
        Mockito.when(holdingRepo.save(Mockito.any(VirtualGoldHolding.class))).thenReturn(holding);
        Mockito.when(userRepo.save(Mockito.any(User.class))).thenReturn(user);
        Mockito.when(branchRepo.save(Mockito.any(VendorBranch.class))).thenReturn(branch);
        Mockito.when(historyRepo.save(Mockito.any(TransactionHistory.class))).thenReturn(transactionHistory);
        Mockito.when(paymentRepo.save(Mockito.any(Payment.class))).thenReturn(new Payment());

        TransactionHistoryResponseDTO result = goldTransactionService.buyGold(request);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(200, result.getTransactionId());
        Mockito.verify(userRepo).findById(1);
        Mockito.verify(branchRepo).findById(10);
        Mockito.verify(userRepo).save(Mockito.any(User.class));
        Mockito.verify(historyRepo).save(Mockito.any(TransactionHistory.class));
        Mockito.verify(paymentRepo).save(Mockito.any(Payment.class));
    }

    @Test
    public void testBuyGold_Success_ExistingHolding() {
        BuyGoldRequestDTO request = new BuyGoldRequestDTO();
        request.setUserId(1);
        request.setBranchId(10);
        request.setQuantity(new BigDecimal("5.00"));
        request.setPaymentMethod(PaymentMethod.PAYTM);

        Mockito.when(userRepo.findById(1)).thenReturn(Optional.of(user));
        Mockito.when(branchRepo.findById(10)).thenReturn(Optional.of(branch));
        Mockito.when(holdingRepo.findByUserUserId(1)).thenReturn(List.of(holding)); // existing holding for same branch
        Mockito.when(holdingRepo.save(Mockito.any(VirtualGoldHolding.class))).thenReturn(holding);
        Mockito.when(userRepo.save(Mockito.any(User.class))).thenReturn(user);
        Mockito.when(branchRepo.save(Mockito.any(VendorBranch.class))).thenReturn(branch);
        Mockito.when(historyRepo.save(Mockito.any(TransactionHistory.class))).thenReturn(transactionHistory);
        Mockito.when(paymentRepo.save(Mockito.any(Payment.class))).thenReturn(new Payment());

        TransactionHistoryResponseDTO result = goldTransactionService.buyGold(request);

        Assertions.assertNotNull(result);
        Mockito.verify(holdingRepo).save(Mockito.any(VirtualGoldHolding.class));
    }

    @Test
    public void testBuyGold_UserNotFound() {
        BuyGoldRequestDTO request = new BuyGoldRequestDTO();
        request.setUserId(99);
        request.setBranchId(10);
        request.setQuantity(new BigDecimal("5.00"));
        request.setPaymentMethod(PaymentMethod.CREDIT_CARD);

        Mockito.when(userRepo.findById(99)).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class,
                () -> goldTransactionService.buyGold(request));

        Mockito.verify(userRepo).findById(99);
        Mockito.verify(branchRepo, Mockito.never()).findById(Mockito.any());
    }

    @Test
    public void testBuyGold_BranchNotFound() {
        BuyGoldRequestDTO request = new BuyGoldRequestDTO();
        request.setUserId(1);
        request.setBranchId(99);
        request.setQuantity(new BigDecimal("5.00"));
        request.setPaymentMethod(PaymentMethod.CREDIT_CARD);

        Mockito.when(userRepo.findById(1)).thenReturn(Optional.of(user));
        Mockito.when(branchRepo.findById(99)).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class,
                () -> goldTransactionService.buyGold(request));

        Mockito.verify(branchRepo).findById(99);
    }

    @Test
    public void testBuyGold_InsufficientBalance() {
        user.setBalance(new BigDecimal("100.00")); // very low balance
        BuyGoldRequestDTO request = new BuyGoldRequestDTO();
        request.setUserId(1);
        request.setBranchId(10);
        request.setQuantity(new BigDecimal("10.00")); // 10g * 6000 = 60000 > 100
        request.setPaymentMethod(PaymentMethod.PHONEPE);

        Mockito.when(userRepo.findById(1)).thenReturn(Optional.of(user));
        Mockito.when(branchRepo.findById(10)).thenReturn(Optional.of(branch));

        Assertions.assertThrows(InsufficientBalanceException.class,
                () -> goldTransactionService.buyGold(request));
    }

    @Test
    public void testBuyGold_InsufficientBranchStock() {
        branch.setQuantity(new BigDecimal("1.00")); // only 1g in stock
        BuyGoldRequestDTO request = new BuyGoldRequestDTO();
        request.setUserId(1);
        request.setBranchId(10);
        request.setQuantity(new BigDecimal("5.00")); // want 5g
        request.setPaymentMethod(PaymentMethod.AMAZON_PAY);

        Mockito.when(userRepo.findById(1)).thenReturn(Optional.of(user));
        Mockito.when(branchRepo.findById(10)).thenReturn(Optional.of(branch));

        Assertions.assertThrows(InsufficientGoldException.class,
                () -> goldTransactionService.buyGold(request));
    }

    // ── sellGold ───────────────────────────────────────────────

    @Test
    public void testSellGold_Success() {
        SellGoldRequestDTO request = new SellGoldRequestDTO();
        request.setUserId(1);
        request.setBranchId(10);
        request.setQuantity(new BigDecimal("5.00"));

        TransactionHistory sellHistory = new TransactionHistory();
        sellHistory.setTransactionId(201);
        sellHistory.setUser(user);
        sellHistory.setBranch(branch);
        sellHistory.setTransactionType(TransactionType2.SELL);
        sellHistory.setTransactionStatus(TransactionStatus.SUCCESS);
        sellHistory.setQuantity(new BigDecimal("5.00"));
        sellHistory.setAmount(new BigDecimal("30000.00"));
        sellHistory.setCreatedAt(LocalDateTime.now());

        Mockito.when(userRepo.findById(1)).thenReturn(Optional.of(user));
        Mockito.when(branchRepo.findById(10)).thenReturn(Optional.of(branch));
        Mockito.when(holdingRepo.findByUserUserId(1)).thenReturn(List.of(holding)); // holding has 10g
        Mockito.when(holdingRepo.save(Mockito.any(VirtualGoldHolding.class))).thenReturn(holding);
        Mockito.when(branchRepo.save(Mockito.any(VendorBranch.class))).thenReturn(branch);
        Mockito.when(userRepo.save(Mockito.any(User.class))).thenReturn(user);
        Mockito.when(historyRepo.save(Mockito.any(TransactionHistory.class))).thenReturn(sellHistory);
        Mockito.when(paymentRepo.save(Mockito.any(Payment.class))).thenReturn(new Payment());

        TransactionHistoryResponseDTO result = goldTransactionService.sellGold(request);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(201, result.getTransactionId());
        Mockito.verify(holdingRepo).save(Mockito.any(VirtualGoldHolding.class));
        Mockito.verify(userRepo).save(Mockito.any(User.class));
    }

    @Test
    public void testSellGold_NoHoldingAtBranch() {
        SellGoldRequestDTO request = new SellGoldRequestDTO();
        request.setUserId(1);
        request.setBranchId(10);
        request.setQuantity(new BigDecimal("5.00"));

        Mockito.when(userRepo.findById(1)).thenReturn(Optional.of(user));
        Mockito.when(branchRepo.findById(10)).thenReturn(Optional.of(branch));
        Mockito.when(holdingRepo.findByUserUserId(1)).thenReturn(List.of()); // no holdings

        Assertions.assertThrows(InsufficientGoldException.class,
                () -> goldTransactionService.sellGold(request));
    }

    @Test
    public void testSellGold_InsufficientHolding() {
        holding.setQuantity(new BigDecimal("2.00")); // only 2g, want 5g
        SellGoldRequestDTO request = new SellGoldRequestDTO();
        request.setUserId(1);
        request.setBranchId(10);
        request.setQuantity(new BigDecimal("5.00"));

        Mockito.when(userRepo.findById(1)).thenReturn(Optional.of(user));
        Mockito.when(branchRepo.findById(10)).thenReturn(Optional.of(branch));
        Mockito.when(holdingRepo.findByUserUserId(1)).thenReturn(List.of(holding));

        Assertions.assertThrows(InsufficientGoldException.class,
                () -> goldTransactionService.sellGold(request));
    }

    // ── getHoldingsByUser ──────────────────────────────────────

    @Test
    public void testGetHoldingsByUser_ReturnsList() {
        Mockito.when(holdingRepo.findByUserUserId(1)).thenReturn(List.of(holding));

        List<VirtualGoldHoldingResponseDTO> result = goldTransactionService.getHoldingsByUser(1);

        Assertions.assertEquals(1, result.size());
        Mockito.verify(holdingRepo).findByUserUserId(1);
    }

    @Test
    public void testGetHoldingsByUser_EmptyList() {
        Mockito.when(holdingRepo.findByUserUserId(99)).thenReturn(List.of());

        List<VirtualGoldHoldingResponseDTO> result = goldTransactionService.getHoldingsByUser(99);

        Assertions.assertTrue(result.isEmpty());
    }

    // ── getTransactionHistoryByUser ────────────────────────────

    @Test
    public void testGetTransactionHistoryByUser_ReturnsList() {
        Mockito.when(historyRepo.findByUserUserId(1)).thenReturn(List.of(transactionHistory));

        List<TransactionHistoryResponseDTO> result = goldTransactionService.getTransactionHistoryByUser(1);

        Assertions.assertEquals(1, result.size());
        Mockito.verify(historyRepo).findByUserUserId(1);
    }

    // ── getAllTransactionHistory ────────────────────────────────

    @Test
    public void testGetAllTransactionHistory_ReturnsList() {
        Mockito.when(historyRepo.findAll()).thenReturn(List.of(transactionHistory));

        List<TransactionHistoryResponseDTO> result = goldTransactionService.getAllTransactionHistory();

        Assertions.assertFalse(result.isEmpty());
        Mockito.verify(historyRepo).findAll();
    }
}