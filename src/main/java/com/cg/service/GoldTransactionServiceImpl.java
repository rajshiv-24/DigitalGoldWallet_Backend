package com.cg.service;

import com.cg.dto.*;
import com.cg.entity.*;
import com.cg.enums.*;
import com.cg.exception.*;
import com.cg.repo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class GoldTransactionServiceImpl implements GoldTransactionService {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private VendorBranchRepository branchRepo;

    @Autowired
    private VirtualGoldHoldingRepository holdingRepo;

    @Autowired
    private PhysicalGoldTransactionRepository physicalRepo;

    @Autowired
    private TransactionHistoryRepository historyRepo;

    @Autowired
    private PaymentRepository paymentRepo;

    @Autowired
    private AddressRepository addressRepo;

    // ──────────────────────────────────────────────────
    // BUY GOLD
    // 1. Validate user, branch exist
    // 2. Calculate cost = quantity × branch.vendor.currentGoldPrice
    // 3. Check user.balance >= cost (InsufficientBalanceException)
    // 4. Check branch.quantity >= requested quantity (InsufficientGoldException)
    // 5. Deduct cost from user.balance
    // 6. Deduct quantity from branch.quantity (vendor branch stock)
    // 7. Add/update VirtualGoldHolding for this user+branch
    // 8. Record in transaction_history (BUY, COMPLETED)
    // 9. Record in payments (DEBIT, SUCCESS)
    // ──────────────────────────────────────────────────
    @Override
    @Transactional
    public TransactionHistoryResponseDTO buyGold(BuyGoldRequestDTO request) {
        User user = userRepo.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found with id: " + request.getUserId()));

        VendorBranch branch = branchRepo.findById(request.getBranchId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Branch not found with id: " + request.getBranchId()));

        BigDecimal quantity = request.getQuantity();
        BigDecimal pricePerGram = BigDecimal.valueOf(branch.getVendor().getCurrentGoldPrice());
        BigDecimal totalCost = quantity.multiply(pricePerGram);

        // Check wallet balance
        if (user.getBalance().compareTo(totalCost) < 0) {
            throw new InsufficientBalanceException(
                    "Insufficient wallet balance. Required: ₹" + totalCost
                    + ", Available: ₹" + user.getBalance());
        }

        // Check branch stock
        if (branch.getQuantity().compareTo(quantity) < 0) {
            throw new InsufficientGoldException(
                    "Branch does not have enough gold. Requested: " + quantity
                    + "g, Available: " + branch.getQuantity() + "g");
        }

        // Deduct cost from user wallet
        user.setBalance(user.getBalance().subtract(totalCost));
        userRepo.save(user);

        // Reduce branch stock
        branch.setQuantity(branch.getQuantity().subtract(quantity));
        branchRepo.save(branch);

        // Update or create virtual gold holding for this user+branch
        Optional<VirtualGoldHolding> existingHolding =
                holdingRepo.findByUserUserId(user.getUserId())
                        .stream()
                        .filter(h -> h.getBranch().getBranchId().equals(branch.getBranchId()))
                        .findFirst();

        if (existingHolding.isPresent()) {
            VirtualGoldHolding holding = existingHolding.get();
            holding.setQuantity(holding.getQuantity().add(quantity));
            holdingRepo.save(holding);
        } else {
            VirtualGoldHolding newHolding = new VirtualGoldHolding();
            newHolding.setUser(user);
            newHolding.setBranch(branch);
            newHolding.setQuantity(quantity);
            newHolding.setCreatedAt(LocalDateTime.now());
            holdingRepo.save(newHolding);
        }

        // Record in transaction_history
        TransactionHistory history = new TransactionHistory();
        history.setUser(user);
        history.setBranch(branch);
        history.setTransactionType(TransactionType2.BUY);
        history.setTransactionStatus(TransactionStatus.SUCCESS);
        history.setQuantity(quantity);
        history.setAmount(totalCost);
        history.setCreatedAt(LocalDateTime.now());
        TransactionHistory savedHistory = historyRepo.save(history);

        // Record in payments (DEBIT — money left wallet)
        Payment payment = new Payment();
        payment.setUser(user);
        payment.setAmount(totalCost);
        payment.setPaymentMethod(request.getPaymentMethod());
        payment.setTransactionType(TransactionType.DEBITED_FROM_WALLET);
        payment.setPaymentStatus(PaymentStatus.SUCCESS);
        payment.setCreatedAt(LocalDateTime.now());
        paymentRepo.save(payment);

        return toHistoryDTO(savedHistory);
    }

    // ──────────────────────────────────────────────────
    // SELL GOLD
    // 1. Validate user, branch exist
    // 2. Find user's holding at this branch
    // 3. Check holding.quantity >= requested quantity (InsufficientGoldException)
    // 4. Calculate refund = quantity × currentGoldPrice
    // 5. Deduct from holding (or remove if zero)
    // 6. Add stock back to branch
    // 7. Credit INR to user.balance
    // 8. Record in transaction_history (SELL, COMPLETED)
    // 9. Record in payments (CREDIT, SUCCESS)
    // ──────────────────────────────────────────────────
    @Override
    @Transactional
    public TransactionHistoryResponseDTO sellGold(SellGoldRequestDTO request) {
        User user = userRepo.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found with id: " + request.getUserId()));

        VendorBranch branch = branchRepo.findById(request.getBranchId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Branch not found with id: " + request.getBranchId()));

        BigDecimal quantity = request.getQuantity();

        // Find user's holding at this branch
        VirtualGoldHolding holding = holdingRepo.findByUserUserId(user.getUserId())
                .stream()
                .filter(h -> h.getBranch().getBranchId().equals(branch.getBranchId()))
                .findFirst()
                .orElseThrow(() -> new InsufficientGoldException(
                        "No virtual gold holding found for this user at branch id: " + request.getBranchId()));

        if (holding.getQuantity().compareTo(quantity) < 0) {
            throw new InsufficientGoldException(
                    "Insufficient gold holding. Requested: " + quantity
                    + "g, Available: " + holding.getQuantity() + "g");
        }

        BigDecimal pricePerGram = BigDecimal.valueOf(branch.getVendor().getCurrentGoldPrice());
        BigDecimal refundAmount = quantity.multiply(pricePerGram);

        // Reduce holding
        holding.setQuantity(holding.getQuantity().subtract(quantity));
        holdingRepo.save(holding);

        // Add stock back to branch
        branch.setQuantity(branch.getQuantity().add(quantity));
        branchRepo.save(branch);

        // Credit INR to wallet
        user.setBalance(user.getBalance().add(refundAmount));
        userRepo.save(user);

        // Record in transaction_history
        TransactionHistory history = new TransactionHistory();
        history.setUser(user);
        history.setBranch(branch);
        history.setTransactionType(TransactionType2.SELL);
        history.setTransactionStatus(TransactionStatus.SUCCESS);
        history.setQuantity(quantity);
        history.setAmount(refundAmount);
        history.setCreatedAt(LocalDateTime.now());
        TransactionHistory savedHistory = historyRepo.save(history);

        // Record in payments (CREDIT — money returned to wallet)
        Payment payment = new Payment();
        payment.setUser(user);
        payment.setAmount(refundAmount);
        payment.setPaymentMethod(PaymentMethod.BANK_TRANSFER); // default for sell
        payment.setTransactionType(TransactionType.CREDITED_TO_WALLET);
        payment.setPaymentStatus(PaymentStatus.SUCCESS);
        payment.setCreatedAt(LocalDateTime.now());
        paymentRepo.save(payment);

        return toHistoryDTO(savedHistory);
    }

    // ──────────────────────────────────────────────────
    // CONVERT TO PHYSICAL DELIVERY
    // 1. Validate user, branch, deliveryAddress exist
    // 2. Find user's holding at this branch
    // 3. Check holding.quantity >= requested quantity (InsufficientGoldException)
    // 4. Deduct from virtual_gold_holdings
    // 5. Insert into physical_gold_transactions
    // 6. Record in transaction_history (PHYSICAL_DELIVERY, COMPLETED)
    // NOTE: No payment record here — gold is already paid for when buying
    // ──────────────────────────────────────────────────
    @Override
    @Transactional
    public PhysicalGoldTransactionResponseDTO convertToPhysical(ConvertToPhysicalRequestDTO request) {
        User user = userRepo.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User not found with id: " + request.getUserId()));

        VendorBranch branch = branchRepo.findById(request.getBranchId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Branch not found with id: " + request.getBranchId()));

        Address deliveryAddress = addressRepo.findById(request.getDeliveryAddressId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Delivery address not found with id: " + request.getDeliveryAddressId()));

        BigDecimal quantity = request.getQuantity();

        // Find holding
        VirtualGoldHolding holding = holdingRepo.findByUserUserId(user.getUserId())
                .stream()
                .filter(h -> h.getBranch().getBranchId().equals(branch.getBranchId()))
                .findFirst()
                .orElseThrow(() -> new InsufficientGoldException(
                        "No virtual gold holding found for this user at branch id: " + request.getBranchId()));

        if (holding.getQuantity().compareTo(quantity) < 0) {
            throw new InsufficientGoldException(
                    "Insufficient gold holding. Requested: " + quantity
                    + "g, Available: " + holding.getQuantity() + "g");
        }

        // Deduct from virtual holding
        holding.setQuantity(holding.getQuantity().subtract(quantity));
        holdingRepo.save(holding);

        // Insert physical transaction record
        PhysicalGoldTransaction physical = new PhysicalGoldTransaction();
        physical.setUser(user);
        physical.setBranch(branch);
        physical.setQuantity(quantity);
        physical.setDeliveryAddress(deliveryAddress);
        physical.setCreatedAt(LocalDateTime.now());
        PhysicalGoldTransaction savedPhysical = physicalRepo.save(physical);

        // Record in transaction_history
        BigDecimal pricePerGram = BigDecimal.valueOf(branch.getVendor().getCurrentGoldPrice());
        TransactionHistory history = new TransactionHistory();
        history.setUser(user);
        history.setBranch(branch);
        history.setTransactionType(TransactionType2.CONVERT_TO_PHYSICAL);
        history.setTransactionStatus(TransactionStatus.SUCCESS);
        history.setQuantity(quantity);
        history.setAmount(quantity.multiply(pricePerGram));
        history.setCreatedAt(LocalDateTime.now());
        historyRepo.save(history);

        return toPhysicalDTO(savedPhysical);
    }

    // ──────────────────────────────────────────────────
    // READ METHODS
    // ──────────────────────────────────────────────────
    @Override
    public List<VirtualGoldHoldingResponseDTO> getHoldingsByUser(Integer userId) {
        return holdingRepo.findByUserUserId(userId)
                .stream()
                .map(this::toHoldingDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<TransactionHistoryResponseDTO> getTransactionHistoryByUser(Integer userId) {
        return historyRepo.findByUserUserId(userId)
                .stream()
                .map(this::toHistoryDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<PhysicalGoldTransactionResponseDTO> getPhysicalTransactionsByUser(Integer userId) {
        return physicalRepo.findByUserUserId(userId)
                .stream()
                .map(this::toPhysicalDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<TransactionHistoryResponseDTO> getAllTransactionHistory() {
        return historyRepo.findAll()
                .stream()
                .map(this::toHistoryDTO)
                .collect(Collectors.toList());
    }

    // ──────────────────────────────────────────────────
    // MAPPERS
    // ──────────────────────────────────────────────────

    private TransactionHistoryResponseDTO toHistoryDTO(TransactionHistory th) {
        TransactionHistoryResponseDTO dto = new TransactionHistoryResponseDTO();
        dto.setTransactionId(th.getTransactionId());
        dto.setUserId(th.getUser().getUserId());
        dto.setUserName(th.getUser().getName());
        dto.setBranchId(th.getBranch().getBranchId());
        if (th.getBranch().getAddress() != null) {
            dto.setBranchCity(th.getBranch().getAddress().getCity());
        }
        if (th.getBranch().getVendor() != null) {
            dto.setVendorName(th.getBranch().getVendor().getVendorName());
        }
        dto.setTransactionType(th.getTransactionType());
        dto.setTransactionStatus(th.getTransactionStatus());
        dto.setQuantity(th.getQuantity());
        dto.setAmount(th.getAmount());
        dto.setCreatedAt(th.getCreatedAt());
        return dto;
    }

    private VirtualGoldHoldingResponseDTO toHoldingDTO(VirtualGoldHolding h) {
        VirtualGoldHoldingResponseDTO dto = new VirtualGoldHoldingResponseDTO();
        dto.setHoldingId(h.getHoldingId());
        dto.setUserId(h.getUser().getUserId());
        dto.setUserName(h.getUser().getName());
        dto.setBranchId(h.getBranch().getBranchId());
        if (h.getBranch().getVendor() != null) {
            dto.setVendorName(h.getBranch().getVendor().getVendorName());
        }
        if (h.getBranch().getAddress() != null) {
            dto.setBranchCity(h.getBranch().getAddress().getCity());
        }
        dto.setQuantity(h.getQuantity());
        dto.setCreatedAt(h.getCreatedAt());
        return dto;
    }

    private PhysicalGoldTransactionResponseDTO toPhysicalDTO(PhysicalGoldTransaction p) {
        PhysicalGoldTransactionResponseDTO dto = new PhysicalGoldTransactionResponseDTO();
        dto.setTransactionId(p.getTransactionId());
        dto.setUserId(p.getUser().getUserId());
        dto.setUserName(p.getUser().getName());
        dto.setBranchId(p.getBranch().getBranchId());
        if (p.getBranch().getVendor() != null) {
            dto.setVendorName(p.getBranch().getVendor().getVendorName());
        }
        dto.setQuantity(p.getQuantity());
        if (p.getDeliveryAddress() != null) {
            dto.setDeliveryStreet(p.getDeliveryAddress().getStreet());
            dto.setDeliveryCity(p.getDeliveryAddress().getCity());
            dto.setDeliveryState(p.getDeliveryAddress().getState());
            dto.setDeliveryPostalCode(p.getDeliveryAddress().getPostalCode());
            dto.setDeliveryCountry(p.getDeliveryAddress().getCountry());
        }
        dto.setCreatedAt(p.getCreatedAt());
        return dto;
    }
}
