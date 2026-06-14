package com.cg.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.cg.enums.TransactionStatus;
import com.cg.enums.TransactionType2;


public class TransactionHistoryResponseDTO {

    private Integer transactionId;
    private Integer userId;
    private String userName;
    private Integer branchId;
    private String branchCity;               
    private String vendorName;               
    private TransactionType2 transactionType;  
    private TransactionStatus transactionStatus; 
    private BigDecimal quantity;             
    private BigDecimal amount;               
    private LocalDateTime createdAt;

    public Integer getTransactionId() { return transactionId; }
    public void setTransactionId(Integer transactionId) { this.transactionId = transactionId; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public Integer getBranchId() { return branchId; }
    public void setBranchId(Integer branchId) { this.branchId = branchId; }

    public String getBranchCity() { return branchCity; }
    public void setBranchCity(String branchCity) { this.branchCity = branchCity; }

    public String getVendorName() { return vendorName; }
    public void setVendorName(String vendorName) { this.vendorName = vendorName; }

    public TransactionType2 getTransactionType() { return transactionType; }
    public void setTransactionType(TransactionType2 transactionType) { this.transactionType = transactionType; }

    public TransactionStatus getTransactionStatus() { return transactionStatus; }
    public void setTransactionStatus(TransactionStatus transactionStatus) { this.transactionStatus = transactionStatus; }

    public BigDecimal getQuantity() { return quantity; }
    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
	public TransactionHistoryResponseDTO(Integer transactionId, Integer userId, String userName, Integer branchId,
			String branchCity, String vendorName, TransactionType2 transactionType, TransactionStatus transactionStatus,
			BigDecimal quantity, BigDecimal amount, LocalDateTime createdAt) {
		super();
		this.transactionId = transactionId;
		this.userId = userId;
		this.userName = userName;
		this.branchId = branchId;
		this.branchCity = branchCity;
		this.vendorName = vendorName;
		this.transactionType = transactionType;
		this.transactionStatus = transactionStatus;
		this.quantity = quantity;
		this.amount = amount;
		this.createdAt = createdAt;
	}
	public TransactionHistoryResponseDTO() {
		super();
		// TODO Auto-generated constructor stub
	}
}