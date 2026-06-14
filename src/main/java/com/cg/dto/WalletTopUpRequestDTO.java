package com.cg.dto;

import java.math.BigDecimal;
import com.cg.enums.PaymentMethod;


public class WalletTopUpRequestDTO {

    private Integer userId;               
    private BigDecimal amount;             
    private PaymentMethod paymentMethod;  

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }
	public WalletTopUpRequestDTO(Integer userId, BigDecimal amount, PaymentMethod paymentMethod) {
		super();
		this.userId = userId;
		this.amount = amount;
		this.paymentMethod = paymentMethod;
	}
	public WalletTopUpRequestDTO() {
		super();
		// TODO Auto-generated constructor stub
	}
}