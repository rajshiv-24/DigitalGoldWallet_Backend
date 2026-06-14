package com.cg.dto;

import java.math.BigDecimal;
import com.cg.enums.PaymentMethod;

public class BuyGoldRequestDTO {

    private Integer userId;           
    private Integer branchId;         
    private BigDecimal quantity;      
    private PaymentMethod paymentMethod; 

    public BuyGoldRequestDTO() {
		super();
		// TODO Auto-generated constructor stub
	}
	public BuyGoldRequestDTO(Integer userId, Integer branchId, BigDecimal quantity, PaymentMethod paymentMethod) {
		super();
		this.userId = userId;
		this.branchId = branchId;
		this.quantity = quantity;
		this.paymentMethod = paymentMethod;
	}
	public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public Integer getBranchId() { return branchId; }
    public void setBranchId(Integer branchId) { this.branchId = branchId; }

    public BigDecimal getQuantity() { return quantity; }
    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }

    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }
}