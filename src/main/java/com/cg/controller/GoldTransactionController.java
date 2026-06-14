package com.cg.controller;

import com.cg.dto.*;
import com.cg.entity.PhysicalGoldTransaction;
import com.cg.entity.TransactionHistory;
import com.cg.entity.VirtualGoldHolding;
import com.cg.enums.TransactionStatus;
import com.cg.exception.ResourceNotFoundException;
import com.cg.repo.PhysicalGoldTransactionRepository;
import com.cg.repo.TransactionHistoryRepository;
import com.cg.repo.VirtualGoldHoldingRepository;
import com.cg.service.GoldTransactionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/gold")
public class GoldTransactionController {

    private final GoldTransactionService goldTransactionService;
    private final TransactionHistoryRepository historyRepository;
    private final VirtualGoldHoldingRepository holdingRepository;
    private final PhysicalGoldTransactionRepository physicalRepository;

    public GoldTransactionController(GoldTransactionService goldTransactionService,
                                     TransactionHistoryRepository historyRepository,
                                     VirtualGoldHoldingRepository holdingRepository,
                                     PhysicalGoldTransactionRepository physicalRepository) {
        this.goldTransactionService = goldTransactionService;
        this.historyRepository = historyRepository;
        this.holdingRepository = holdingRepository;
        this.physicalRepository = physicalRepository;
    }

    // ── USER ACTIONS ─────────────────────────────────────────

    @PostMapping("/buy")
    @PreAuthorize("@userAccessService.canAccessOwnUser(authentication, #request.userId)")
    public ResponseEntity<TransactionHistoryResponseDTO> buyGold(@RequestBody BuyGoldRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(goldTransactionService.buyGold(request));
    }

    @PostMapping("/sell")
    @PreAuthorize("@userAccessService.canAccessOwnUser(authentication, #request.userId)")
    public TransactionHistoryResponseDTO sellGold(@RequestBody SellGoldRequestDTO request) {
        return goldTransactionService.sellGold(request);
    }

    @PostMapping("/convert-to-physical")
    @PreAuthorize("@userAccessService.canAccessOwnUser(authentication, #request.userId)")
    public ResponseEntity<PhysicalGoldTransactionResponseDTO> convertToPhysical(
            @RequestBody ConvertToPhysicalRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(goldTransactionService.convertToPhysical(request));
    }

    // ── USER — OWN DATA ──────────────────────────────────────

    @GetMapping("/holdings/by-user/{userId}")
    @PreAuthorize("@userAccessService.canAccessUser(authentication, #userId)")
    public List<VirtualGoldHoldingResponseDTO> getHoldingsByUser(@PathVariable Integer userId) {
        return goldTransactionService.getHoldingsByUser(userId);
    }

    @GetMapping("/holdings/by-user/{userId}/total")
    @PreAuthorize("@userAccessService.canAccessUser(authentication, #userId)")
    public Map<String, Object> getTotalHoldingByUser(@PathVariable Integer userId) {
        BigDecimal total = holdingRepository.findByUserUserId(userId).stream()
                .map(VirtualGoldHolding::getQuantity)
                .filter(q -> q != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return Map.of("userId", userId, "totalQuantity", total);
    }

    @GetMapping("/history/by-user/{userId}")
    @PreAuthorize("@userAccessService.canAccessUser(authentication, #userId)")
    public List<TransactionHistoryResponseDTO> getTransactionHistoryByUser(@PathVariable Integer userId) {
        return goldTransactionService.getTransactionHistoryByUser(userId);
    }

    @GetMapping("/physical/by-user/{userId}")
    @PreAuthorize("@userAccessService.canAccessUser(authentication, #userId)")
    public List<PhysicalGoldTransactionResponseDTO> getPhysicalTransactionsByUser(@PathVariable Integer userId) {
        return goldTransactionService.getPhysicalTransactionsByUser(userId);
    }

    @GetMapping("/summary/by-user/{userId}")
    @PreAuthorize("@userAccessService.canAccessUser(authentication, #userId)")
    public Map<String, Object> getGoldSummaryByUser(@PathVariable Integer userId) {
        return Map.of(
                "userId", userId,
                "holdings", goldTransactionService.getHoldingsByUser(userId),
                "history", goldTransactionService.getTransactionHistoryByUser(userId),
                "physicalTransactions", goldTransactionService.getPhysicalTransactionsByUser(userId)
        );
    }

    // ── ADMIN — AGGREGATE VIEWS ──────────────────────────────

    @GetMapping("/history")
    @PreAuthorize("hasRole('ADMIN')")
    public List<TransactionHistoryResponseDTO> getAllTransactionHistory() {
        return goldTransactionService.getAllTransactionHistory();
    }

    @GetMapping("/history/{transactionId}")
    @PreAuthorize("hasRole('ADMIN')")
    public TransactionHistoryResponseDTO getTransactionHistoryById(@PathVariable Integer transactionId) {
        TransactionHistory history = historyRepository.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found with id: " + transactionId));
        return toHistoryDTO(history);
    }

    @GetMapping("/history/by-status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    public List<TransactionHistoryResponseDTO> getTransactionHistoryByStatus(@PathVariable TransactionStatus status) {
        return historyRepository.findByTransactionStatus(status).stream().map(this::toHistoryDTO).toList();
    }

    @GetMapping("/history/count")
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, Long> countTransactionHistory() {
        return Map.of("count", historyRepository.count());
    }

    @GetMapping("/holdings")
    @PreAuthorize("hasRole('ADMIN')")
    public List<VirtualGoldHoldingResponseDTO> getAllHoldings() {
        return holdingRepository.findAll().stream().map(this::toHoldingDTO).toList();
    }

    @GetMapping("/holdings/{holdingId}")
    @PreAuthorize("hasRole('ADMIN')")
    public VirtualGoldHoldingResponseDTO getHoldingById(@PathVariable Integer holdingId) {
        VirtualGoldHolding holding = holdingRepository.findById(holdingId)
                .orElseThrow(() -> new ResourceNotFoundException("Holding not found with id: " + holdingId));
        return toHoldingDTO(holding);
    }

    @GetMapping("/holdings/count")
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, Long> countHoldings() {
        return Map.of("count", holdingRepository.count());
    }

    @GetMapping("/physical")
    @PreAuthorize("hasRole('ADMIN')")
    public List<PhysicalGoldTransactionResponseDTO> getAllPhysicalTransactions() {
        return physicalRepository.findAll().stream().map(this::toPhysicalDTO).toList();
    }

    @GetMapping("/physical/{transactionId}")
    @PreAuthorize("hasRole('ADMIN')")
    public PhysicalGoldTransactionResponseDTO getPhysicalTransactionById(@PathVariable Integer transactionId) {
        PhysicalGoldTransaction transaction = physicalRepository.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Physical transaction not found with id: " + transactionId));
        return toPhysicalDTO(transaction);
    }

    @GetMapping("/physical/count")
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, Long> countPhysicalTransactions() {
        return Map.of("count", physicalRepository.count());
    }

    // ── MAPPERS ──────────────────────────────────────────────

    private TransactionHistoryResponseDTO toHistoryDTO(TransactionHistory history) {
        TransactionHistoryResponseDTO dto = new TransactionHistoryResponseDTO();
        dto.setTransactionId(history.getTransactionId());
        dto.setTransactionType(history.getTransactionType());
        dto.setTransactionStatus(history.getTransactionStatus());
        dto.setQuantity(history.getQuantity());
        dto.setAmount(history.getAmount());
        dto.setCreatedAt(history.getCreatedAt());
        if (history.getUser() != null) {
            dto.setUserId(history.getUser().getUserId());
            dto.setUserName(history.getUser().getName());
        }
        if (history.getBranch() != null) {
            dto.setBranchId(history.getBranch().getBranchId());
            if (history.getBranch().getAddress() != null) dto.setBranchCity(history.getBranch().getAddress().getCity());
            if (history.getBranch().getVendor() != null) dto.setVendorName(history.getBranch().getVendor().getVendorName());
        }
        return dto;
    }

    private VirtualGoldHoldingResponseDTO toHoldingDTO(VirtualGoldHolding holding) {
        VirtualGoldHoldingResponseDTO dto = new VirtualGoldHoldingResponseDTO();
        dto.setHoldingId(holding.getHoldingId());
        dto.setQuantity(holding.getQuantity());
        dto.setCreatedAt(holding.getCreatedAt());
        if (holding.getUser() != null) { dto.setUserId(holding.getUser().getUserId()); dto.setUserName(holding.getUser().getName()); }
        if (holding.getBranch() != null) {
            dto.setBranchId(holding.getBranch().getBranchId());
            if (holding.getBranch().getVendor() != null) dto.setVendorName(holding.getBranch().getVendor().getVendorName());
            if (holding.getBranch().getAddress() != null) dto.setBranchCity(holding.getBranch().getAddress().getCity());
        }
        return dto;
    }

    private PhysicalGoldTransactionResponseDTO toPhysicalDTO(PhysicalGoldTransaction transaction) {
        PhysicalGoldTransactionResponseDTO dto = new PhysicalGoldTransactionResponseDTO();
        dto.setTransactionId(transaction.getTransactionId());
        dto.setQuantity(transaction.getQuantity());
        dto.setCreatedAt(transaction.getCreatedAt());
        if (transaction.getUser() != null) { dto.setUserId(transaction.getUser().getUserId()); dto.setUserName(transaction.getUser().getName()); }
        if (transaction.getBranch() != null) {
            dto.setBranchId(transaction.getBranch().getBranchId());
            if (transaction.getBranch().getVendor() != null) dto.setVendorName(transaction.getBranch().getVendor().getVendorName());
        }
        if (transaction.getDeliveryAddress() != null) {
            dto.setDeliveryStreet(transaction.getDeliveryAddress().getStreet());
            dto.setDeliveryCity(transaction.getDeliveryAddress().getCity());
            dto.setDeliveryState(transaction.getDeliveryAddress().getState());
            dto.setDeliveryPostalCode(transaction.getDeliveryAddress().getPostalCode());
            dto.setDeliveryCountry(transaction.getDeliveryAddress().getCountry());
        }
        return dto;
    }
}
