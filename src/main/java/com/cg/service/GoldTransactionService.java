package com.cg.service;

import com.cg.dto.*;

import java.util.List;

public interface GoldTransactionService {

    // BUY gold: deducts amount from user.balance, adds to virtual_gold_holdings,
    // records in transaction_history (TransactionType2.BUY) and payments (TransactionType.DEBIT)
    // Throws InsufficientBalanceException if user.balance < cost
    // Throws InsufficientGoldException if branch.quantity < requested quantity
    TransactionHistoryResponseDTO buyGold(BuyGoldRequestDTO request);

    // SELL gold: deducts from virtual_gold_holdings, credits INR back to user.balance,
    // records in transaction_history (TransactionType2.SELL) and payments (TransactionType.CREDIT)
    // Throws InsufficientGoldException if user holding < requested quantity
    TransactionHistoryResponseDTO sellGold(SellGoldRequestDTO request);

    // CONVERT TO PHYSICAL: deducts from virtual_gold_holdings, inserts into physical_gold_transactions,
    // records in transaction_history (TransactionType2.PHYSICAL_DELIVERY)
    // Throws InsufficientGoldException if user holding < requested quantity
    PhysicalGoldTransactionResponseDTO convertToPhysical(ConvertToPhysicalRequestDTO request);

    // Get all virtual gold holdings for a user (from virtual_gold_holdings where user_id = userId)
    List<VirtualGoldHoldingResponseDTO> getHoldingsByUser(Integer userId);

    // Get full transaction history for a user (from transaction_history where user_id = userId)
    List<TransactionHistoryResponseDTO> getTransactionHistoryByUser(Integer userId);

    // Get all physical gold delivery transactions for a user
    List<PhysicalGoldTransactionResponseDTO> getPhysicalTransactionsByUser(Integer userId);

    // Get all transaction history (for admin)
    List<TransactionHistoryResponseDTO> getAllTransactionHistory();
}
