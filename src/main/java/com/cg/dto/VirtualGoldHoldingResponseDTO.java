package com.cg.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;



public class VirtualGoldHoldingResponseDTO {

    private Integer holdingId;
    private Integer userId;
    private String userName;
    private Integer branchId;
    private String vendorName;     
    private String branchCity;     
    private BigDecimal quantity;   
    private LocalDateTime createdAt;

    public Integer getHoldingId() { return holdingId; }
    public void setHoldingId(Integer holdingId) { this.holdingId = holdingId; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public Integer getBranchId() { return branchId; }
    public void setBranchId(Integer branchId) { this.branchId = branchId; }

    public String getVendorName() { return vendorName; }
    public void setVendorName(String vendorName) { this.vendorName = vendorName; }

    public String getBranchCity() { return branchCity; }
    public void setBranchCity(String branchCity) { this.branchCity = branchCity; }

    public BigDecimal getQuantity() { return quantity; }
    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
	public VirtualGoldHoldingResponseDTO(Integer holdingId, Integer userId, String userName, Integer branchId,
			String vendorName, String branchCity, BigDecimal quantity, LocalDateTime createdAt) {
		super();
		this.holdingId = holdingId;
		this.userId = userId;
		this.userName = userName;
		this.branchId = branchId;
		this.vendorName = vendorName;
		this.branchCity = branchCity;
		this.quantity = quantity;
		this.createdAt = createdAt;
	}
	public VirtualGoldHoldingResponseDTO() {
		super();
		// TODO Auto-generated constructor stub
	}
}