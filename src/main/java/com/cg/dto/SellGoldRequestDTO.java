package com.cg.dto;

import java.math.BigDecimal;



public class SellGoldRequestDTO {

    private Integer userId;      
    private Integer branchId;    
    private BigDecimal quantity;  

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public Integer getBranchId() { return branchId; }
    public void setBranchId(Integer branchId) { this.branchId = branchId; }

    public BigDecimal getQuantity() { return quantity; }
    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }
	public SellGoldRequestDTO(Integer userId, Integer branchId, BigDecimal quantity) {
		super();
		this.userId = userId;
		this.branchId = branchId;
		this.quantity = quantity;
	}
	public SellGoldRequestDTO() {
		super();
		// TODO Auto-generated constructor stub
	}
}