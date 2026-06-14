package com.cg.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.cg.enums.PaymentMethod;
import com.cg.enums.PaymentStatus;
import com.cg.enums.TransactionType;


public class PaymentResponseDTO {

    private Integer paymentId;
    private Integer userId;
    private String userName;
    private BigDecimal amount;               
    private PaymentMethod paymentMethod;    
    private TransactionType transactionType; 
    private PaymentStatus paymentStatus;    
    private LocalDateTime createdAt;

    public Integer getPaymentId() { return paymentId; }
    public void setPaymentId(Integer paymentId) { this.paymentId = paymentId; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }

    public TransactionType getTransactionType() { return transactionType; }
    public void setTransactionType(TransactionType transactionType) { this.transactionType = transactionType; }

    public PaymentStatus getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(PaymentStatus paymentStatus) { this.paymentStatus = paymentStatus; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
	public PaymentResponseDTO(Integer paymentId, Integer userId, String userName, BigDecimal amount,
			PaymentMethod paymentMethod, TransactionType transactionType, PaymentStatus paymentStatus,
			LocalDateTime createdAt) {
		super();
		this.paymentId = paymentId;
		this.userId = userId;
		this.userName = userName;
		this.amount = amount;
		this.paymentMethod = paymentMethod;
		this.transactionType = transactionType;
		this.paymentStatus = paymentStatus;
		this.createdAt = createdAt;
	}
	public PaymentResponseDTO() {
		super();
		// TODO Auto-generated constructor stub
	}
}