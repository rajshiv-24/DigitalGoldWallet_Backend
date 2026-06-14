package com.cg.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.cg.enums.TransactionStatus;
import com.cg.enums.TransactionType2;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "transaction_history")
public class TransactionHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer transactionId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "branch_id")
    private VendorBranch branch;

    @Enumerated(EnumType.STRING)
    private TransactionType2 transactionType;

    @Enumerated(EnumType.STRING)
    private TransactionStatus transactionStatus;

    private BigDecimal quantity;
    private BigDecimal amount;

    private LocalDateTime createdAt;

	public Integer getTransactionId() {
		return transactionId;
	}

	public User getUser() {
		return user;
	}

	public VendorBranch getBranch() {
		return branch;
	}
	
	public TransactionType2 getTransactionType() {
		return transactionType;
	}
	
	public TransactionStatus getTransactionStatus() {
		return transactionStatus;
	}

	public BigDecimal getQuantity() {
		return quantity;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setTransactionId(Integer transactionId) {
		this.transactionId = transactionId;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public void setBranch(VendorBranch branch) {
		this.branch = branch;
	}

	public void setTransactionType(TransactionType2 transactionType) {
		this.transactionType = transactionType;
	}

	public void setTransactionStatus(TransactionStatus transactionStatus) {
		this.transactionStatus = transactionStatus;
	}

	public void setQuantity(BigDecimal quantity) {
		this.quantity = quantity;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
    
    
}

