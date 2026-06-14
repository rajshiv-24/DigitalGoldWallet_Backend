package com.cg.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;


@Entity
public class Vendors {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long vendorId;

    private String vendorName;
    private String description;
    private String contactPersonName;

    public Long getVendorId() {
        return vendorId;
    }

    public void setVendorId(Long vendorId) {
        this.vendorId = vendorId;
    }

    public String getVendorName() {
        return vendorName;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getContactPersonName() {
        return contactPersonName;
    }

    public void setContactPersonName(String contactPersonName) {
        this.contactPersonName = contactPersonName;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getWebsiteUrl() {
        return websiteUrl;
    }

    public void setWebsiteUrl(String websiteUrl) {
        this.websiteUrl = websiteUrl;
    }

    public Double getTotalGoldQuantity() {
        return totalGoldQuantity;
    }

    public void setTotalGoldQuantity(Double totalGoldQuantity) {
        this.totalGoldQuantity = totalGoldQuantity;
    }

    public Double getCurrentGoldPrice() {
        return currentGoldPrice;
    }

    public void setCurrentGoldPrice(Double currentGoldPrice) {
        this.currentGoldPrice = currentGoldPrice;
    }

    private String contactEmail;
    private String contactPhone;
    private String websiteUrl;

    private Double totalGoldQuantity;
    private Double currentGoldPrice;

    @JsonIgnore
    @OneToMany(mappedBy = "vendor")
    private List<VendorBranch> branches;
}
