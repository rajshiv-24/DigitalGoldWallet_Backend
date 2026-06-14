package com.cg.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;


public class PhysicalGoldTransactionResponseDTO {

    private Integer transactionId;
    private Integer userId;
    private String userName;
    private Integer branchId;
    public PhysicalGoldTransactionResponseDTO() {
		super();
		// TODO Auto-generated constructor stub
	}
	public PhysicalGoldTransactionResponseDTO(Integer transactionId, Integer userId, String userName, Integer branchId,
			String vendorName, BigDecimal quantity, String deliveryStreet, String deliveryCity, String deliveryState,
			String deliveryPostalCode, String deliveryCountry, LocalDateTime createdAt) {
		super();
		this.transactionId = transactionId;
		this.userId = userId;
		this.userName = userName;
		this.branchId = branchId;
		this.vendorName = vendorName;
		this.quantity = quantity;
		this.deliveryStreet = deliveryStreet;
		this.deliveryCity = deliveryCity;
		this.deliveryState = deliveryState;
		this.deliveryPostalCode = deliveryPostalCode;
		this.deliveryCountry = deliveryCountry;
		this.createdAt = createdAt;
	}
	private String vendorName;         
    private BigDecimal quantity;         
    private String deliveryStreet;       
    private String deliveryCity;
    private String deliveryState;
    private String deliveryPostalCode;
    private String deliveryCountry;
    private LocalDateTime createdAt;

    public Integer getTransactionId() { return transactionId; }
    public void setTransactionId(Integer transactionId) { this.transactionId = transactionId; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public Integer getBranchId() { return branchId; }
    public void setBranchId(Integer branchId) { this.branchId = branchId; }

    public String getVendorName() { return vendorName; }
    public void setVendorName(String vendorName) { this.vendorName = vendorName; }

    public BigDecimal getQuantity() { return quantity; }
    public void setQuantity(BigDecimal quantity) { this.quantity = quantity; }

    public String getDeliveryStreet() { return deliveryStreet; }
    public void setDeliveryStreet(String deliveryStreet) { this.deliveryStreet = deliveryStreet; }

    public String getDeliveryCity() { return deliveryCity; }
    public void setDeliveryCity(String deliveryCity) { this.deliveryCity = deliveryCity; }

    public String getDeliveryState() { return deliveryState; }
    public void setDeliveryState(String deliveryState) { this.deliveryState = deliveryState; }

    public String getDeliveryPostalCode() { return deliveryPostalCode; }
    public void setDeliveryPostalCode(String deliveryPostalCode) { this.deliveryPostalCode = deliveryPostalCode; }

    public String getDeliveryCountry() { return deliveryCountry; }
    public void setDeliveryCountry(String deliveryCountry) { this.deliveryCountry = deliveryCountry; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}