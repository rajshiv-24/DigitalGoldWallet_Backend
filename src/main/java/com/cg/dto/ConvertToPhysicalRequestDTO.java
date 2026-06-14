package com.cg.dto;

import java.math.BigDecimal;


public class ConvertToPhysicalRequestDTO {

    private Integer userId;              
    private Integer branchId;            
    private BigDecimal quantity;          
    private Integer deliveryAddressId;   

    public ConvertToPhysicalRequestDTO() {
		super();
		// TODO Auto-generated constructor stub
	}
	public ConvertToPhysicalRequestDTO(Integer userId, Integer branchId, BigDecimal quantity,
			Integer deliveryAddressId) {
		super();
		this.userId = userId;
		this.branchId = branchId;
		this.quantity = quantity;
		this.deliveryAddressId = deliveryAddressId;
	}
	public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public Integer getBranchId() { return branchId; }
    public void setBranchId(Integer branchId) { this.branchId = branchId; }

    public BigDecimal getQuantity() { return quantity; }
    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }

    public Integer getDeliveryAddressId() { return deliveryAddressId; }
    public void setDeliveryAddressId(Integer deliveryAddressId) { this.deliveryAddressId = deliveryAddressId; }
}