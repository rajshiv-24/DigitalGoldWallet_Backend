package com.cg.test.controller;

import com.cg.controller.GoldTransactionController;
import com.cg.dto.*;
import com.cg.enums.PaymentMethod;
import com.cg.enums.TransactionStatus;
import com.cg.enums.TransactionType2;
import com.cg.repo.PhysicalGoldTransactionRepository;
import com.cg.repo.TransactionHistoryRepository;
import com.cg.repo.VirtualGoldHoldingRepository;
import com.cg.service.GoldTransactionService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@ExtendWith(MockitoExtension.class)
public class GoldTransactionControllerTest {

    @Mock
    private GoldTransactionService goldTransactionService;

    @Mock
    private TransactionHistoryRepository historyRepository;

    @Mock
    private VirtualGoldHoldingRepository holdingRepository;

    @Mock
    private PhysicalGoldTransactionRepository physicalRepository;

    private GoldTransactionController goldTransactionController;

    private TransactionHistoryResponseDTO historyResponse;
    private VirtualGoldHoldingResponseDTO holdingResponse;
    private PhysicalGoldTransactionResponseDTO physicalResponse;

    @BeforeEach
    public void beforeEach() {
        goldTransactionController = new GoldTransactionController(
                goldTransactionService,
                historyRepository,
                holdingRepository,
                physicalRepository
        );

        historyResponse = new TransactionHistoryResponseDTO();
        historyResponse.setTransactionId(300);
        historyResponse.setUserId(1);
        historyResponse.setUserName("Neha Gupta");
        historyResponse.setBranchId(10);
        historyResponse.setTransactionType(TransactionType2.BUY);
        historyResponse.setTransactionStatus(TransactionStatus.SUCCESS);
        historyResponse.setQuantity(new BigDecimal("5.00"));
        historyResponse.setAmount(new BigDecimal("31000.00"));
        historyResponse.setCreatedAt(LocalDateTime.now());

        holdingResponse = new VirtualGoldHoldingResponseDTO();
        holdingResponse.setHoldingId(400);
        holdingResponse.setUserId(1);
        holdingResponse.setUserName("Neha Gupta");
        holdingResponse.setBranchId(10);
        holdingResponse.setQuantity(new BigDecimal("5.00"));
        holdingResponse.setCreatedAt(LocalDateTime.now());

        physicalResponse = new PhysicalGoldTransactionResponseDTO();
        physicalResponse.setTransactionId(500);
        physicalResponse.setUserId(1);
        physicalResponse.setUserName("Neha Gupta");
        physicalResponse.setBranchId(10);
        physicalResponse.setQuantity(new BigDecimal("2.00"));
        physicalResponse.setCreatedAt(LocalDateTime.now());
    }

    // ── buyGold ────────────────────────────────────────────────

    @Test
    public void testBuyGold_Returns201() {
        BuyGoldRequestDTO request = new BuyGoldRequestDTO();
        request.setUserId(1);
        request.setBranchId(10);
        request.setQuantity(new BigDecimal("5.00"));
        request.setPaymentMethod(PaymentMethod.CREDIT_CARD);

        Mockito.when(goldTransactionService.buyGold(Mockito.any(BuyGoldRequestDTO.class)))
                .thenReturn(historyResponse);

        ResponseEntity<TransactionHistoryResponseDTO> response = goldTransactionController.buyGold(request);

        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(300, response.getBody().getTransactionId());
        Mockito.verify(goldTransactionService).buyGold(Mockito.any(BuyGoldRequestDTO.class));
    }

    // ── sellGold ───────────────────────────────────────────────

    @Test
    public void testSellGold_ReturnsResponse() {
        SellGoldRequestDTO request = new SellGoldRequestDTO();
        request.setUserId(1);
        request.setBranchId(10);
        request.setQuantity(new BigDecimal("3.00"));

        historyResponse.setTransactionType(TransactionType2.SELL);
        Mockito.when(goldTransactionService.sellGold(Mockito.any(SellGoldRequestDTO.class)))
                .thenReturn(historyResponse);

        TransactionHistoryResponseDTO result = goldTransactionController.sellGold(request);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(TransactionType2.SELL, result.getTransactionType());
        Mockito.verify(goldTransactionService).sellGold(Mockito.any(SellGoldRequestDTO.class));
    }

    // ── convertToPhysical ──────────────────────────────────────

    @Test
    public void testConvertToPhysical_Returns201() {
        ConvertToPhysicalRequestDTO request = new ConvertToPhysicalRequestDTO();
        request.setUserId(1);
        request.setBranchId(10);
        request.setQuantity(new BigDecimal("2.00"));
        request.setDeliveryAddressId(20);

        Mockito.when(goldTransactionService.convertToPhysical(Mockito.any(ConvertToPhysicalRequestDTO.class)))
                .thenReturn(physicalResponse);

        ResponseEntity<PhysicalGoldTransactionResponseDTO> response =
                goldTransactionController.convertToPhysical(request);

        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(500, response.getBody().getTransactionId());
        Mockito.verify(goldTransactionService).convertToPhysical(Mockito.any(ConvertToPhysicalRequestDTO.class));
    }

    // ── getHoldingsByUser ──────────────────────────────────────

    @Test
    public void testGetHoldingsByUser_ReturnsList() {
        Mockito.when(goldTransactionService.getHoldingsByUser(1)).thenReturn(List.of(holdingResponse));

        List<VirtualGoldHoldingResponseDTO> result = goldTransactionController.getHoldingsByUser(1);

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(400, result.get(0).getHoldingId());
        Mockito.verify(goldTransactionService).getHoldingsByUser(1);
    }

    @Test
    public void testGetHoldingsByUser_EmptyList() {
        Mockito.when(goldTransactionService.getHoldingsByUser(99)).thenReturn(List.of());

        List<VirtualGoldHoldingResponseDTO> result = goldTransactionController.getHoldingsByUser(99);

        Assertions.assertTrue(result.isEmpty());
    }

    // ── getTransactionHistoryByUser ────────────────────────────

    @Test
    public void testGetTransactionHistoryByUser_ReturnsList() {
        Mockito.when(goldTransactionService.getTransactionHistoryByUser(1)).thenReturn(List.of(historyResponse));

        List<TransactionHistoryResponseDTO> result = goldTransactionController.getTransactionHistoryByUser(1);

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(300, result.get(0).getTransactionId());
        Mockito.verify(goldTransactionService).getTransactionHistoryByUser(1);
    }

    // ── getPhysicalTransactionsByUser ──────────────────────────

    @Test
    public void testGetPhysicalTransactionsByUser_ReturnsList() {
        Mockito.when(goldTransactionService.getPhysicalTransactionsByUser(1))
                .thenReturn(List.of(physicalResponse));

        List<PhysicalGoldTransactionResponseDTO> result =
                goldTransactionController.getPhysicalTransactionsByUser(1);

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(500, result.get(0).getTransactionId());
    }

    // ── getAllTransactionHistory ────────────────────────────────

    @Test
    public void testGetAllTransactionHistory_ReturnsList() {
        Mockito.when(goldTransactionService.getAllTransactionHistory()).thenReturn(List.of(historyResponse));

        List<TransactionHistoryResponseDTO> result = goldTransactionController.getAllTransactionHistory();

        Assertions.assertEquals(1, result.size());
        Mockito.verify(goldTransactionService).getAllTransactionHistory();
    }

    // ── countTransactionHistory ────────────────────────────────

    @Test
    public void testCountTransactionHistory() {
        Mockito.when(historyRepository.count()).thenReturn(50L);

        var result = goldTransactionController.countTransactionHistory();

        Assertions.assertEquals(50L, result.get("count"));
        Mockito.verify(historyRepository).count();
    }

    // ── countHoldings ─────────────────────────────────────────

    @Test
    public void testCountHoldings() {
        Mockito.when(holdingRepository.count()).thenReturn(30L);

        var result = goldTransactionController.countHoldings();

        Assertions.assertEquals(30L, result.get("count"));
        Mockito.verify(holdingRepository).count();
    }

    // ── countPhysicalTransactions ──────────────────────────────

    @Test
    public void testCountPhysicalTransactions() {
        Mockito.when(physicalRepository.count()).thenReturn(12L);

        var result = goldTransactionController.countPhysicalTransactions();

        Assertions.assertEquals(12L, result.get("count"));
        Mockito.verify(physicalRepository).count();
    }

    // ── getGoldSummaryByUser ───────────────────────────────────

    @Test
    public void testGetGoldSummaryByUser_ReturnsMap() {
        Mockito.when(goldTransactionService.getHoldingsByUser(1)).thenReturn(List.of(holdingResponse));
        Mockito.when(goldTransactionService.getTransactionHistoryByUser(1)).thenReturn(List.of(historyResponse));
        Mockito.when(goldTransactionService.getPhysicalTransactionsByUser(1)).thenReturn(List.of(physicalResponse));

        var result = goldTransactionController.getGoldSummaryByUser(1);

        Assertions.assertEquals(1, result.get("userId"));
        Assertions.assertNotNull(result.get("holdings"));
        Assertions.assertNotNull(result.get("history"));
        Assertions.assertNotNull(result.get("physicalTransactions"));
    }
}
