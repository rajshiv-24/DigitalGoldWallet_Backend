package com.cg.dto;


public class VendorRequestDTO {

    private String vendorName;
    private String description;
    private String contactPersonName;
    private String contactEmail;
    private String contactPhone;
    private String websiteUrl;
    private Double totalGoldQuantity;    
    private Double currentGoldPrice;     

    public String getVendorName() { return vendorName; }
    public void setVendorName(String vendorName) { this.vendorName = vendorName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getContactPersonName() { return contactPersonName; }
    public void setContactPersonName(String contactPersonName) { this.contactPersonName = contactPersonName; }

    public String getContactEmail() { return contactEmail; }
    public void setContactEmail(String contactEmail) { this.contactEmail = contactEmail; }

    public String getContactPhone() { return contactPhone; }
    public void setContactPhone(String contactPhone) { this.contactPhone = contactPhone; }

    public String getWebsiteUrl() { return websiteUrl; }
    public void setWebsiteUrl(String websiteUrl) { this.websiteUrl = websiteUrl; }

    public Double getTotalGoldQuantity() { return totalGoldQuantity; }
    public void setTotalGoldQuantity(Double totalGoldQuantity) { this.totalGoldQuantity = totalGoldQuantity; }

    public Double getCurrentGoldPrice() { return currentGoldPrice; }
    public void setCurrentGoldPrice(Double currentGoldPrice) { this.currentGoldPrice = currentGoldPrice; }
	public VendorRequestDTO(String vendorName, String description, String contactPersonName, String contactEmail,
			String contactPhone, String websiteUrl, Double totalGoldQuantity, Double currentGoldPrice) {
		super();
		this.vendorName = vendorName;
		this.description = description;
		this.contactPersonName = contactPersonName;
		this.contactEmail = contactEmail;
		this.contactPhone = contactPhone;
		this.websiteUrl = websiteUrl;
		this.totalGoldQuantity = totalGoldQuantity;
		this.currentGoldPrice = currentGoldPrice;
	}
	public VendorRequestDTO() {
		super();
		// TODO Auto-generated constructor stub
	}
}