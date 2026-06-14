package com.cg.dto;

import java.math.BigDecimal;


public class VendorBranchRequestDTO {

    private Integer vendorId;     
    private Integer addressId;    
    private BigDecimal quantity;  

    public Integer getVendorId() { return vendorId; }
    public void setVendorId(Integer vendorId) { this.vendorId = vendorId; }

    public Integer getAddressId() { return addressId; }
    public void setAddressId(Integer addressId) { this.addressId = addressId; }

    public BigDecimal getQuantity() { return quantity; }
    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }
	public VendorBranchRequestDTO(Integer vendorId, Integer addressId, BigDecimal quantity) {
		super();
		this.vendorId = vendorId;
		this.addressId = addressId;
		this.quantity = quantity;
	}
	public VendorBranchRequestDTO() {
		super();
		// TODO Auto-generated constructor stub
	}
}