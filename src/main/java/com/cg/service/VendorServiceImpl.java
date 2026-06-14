package com.cg.service;

import com.cg.dto.VendorRequestDTO;
import com.cg.entity.Vendors;
import com.cg.exception.ResourceNotFoundException;
import com.cg.repo.VendorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VendorServiceImpl implements VendorService {

    private static final double DEFAULT_TOTAL_GOLD_QUANTITY = 0.0;
    private static final double DEFAULT_CURRENT_GOLD_PRICE = 5700.0;

    @Autowired
    private VendorRepository vendorRepo;

    @Override
    public Vendors createVendor(VendorRequestDTO request) {
        validateVendorRequest(request);

        Vendors vendor = new Vendors();
        vendor.setVendorName(request.getVendorName());
        vendor.setDescription(request.getDescription());
        vendor.setContactPersonName(request.getContactPersonName());
        vendor.setContactEmail(request.getContactEmail());
        vendor.setContactPhone(request.getContactPhone());
        vendor.setWebsiteUrl(request.getWebsiteUrl());
        vendor.setTotalGoldQuantity(defaultIfNull(request.getTotalGoldQuantity(), DEFAULT_TOTAL_GOLD_QUANTITY));
        vendor.setCurrentGoldPrice(defaultIfNull(request.getCurrentGoldPrice(), DEFAULT_CURRENT_GOLD_PRICE));
        return vendorRepo.save(vendor);
    }

    @Override
    public Vendors getVendorById(Integer vendorId) {
        return vendorRepo.findById(vendorId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Vendor not found with id: " + vendorId));
    }

    @Override
    public List<Vendors> getAllVendors() {
        return vendorRepo.findAll();
    }

    @Override
    public Vendors updateVendor(Integer vendorId, VendorRequestDTO request) {
        validateVendorRequest(request);

        Vendors vendor = getVendorById(vendorId);
        vendor.setVendorName(request.getVendorName());
        vendor.setDescription(request.getDescription());
        vendor.setContactPersonName(request.getContactPersonName());
        vendor.setContactEmail(request.getContactEmail());
        vendor.setContactPhone(request.getContactPhone());
        vendor.setWebsiteUrl(request.getWebsiteUrl());
        vendor.setTotalGoldQuantity(defaultIfNull(request.getTotalGoldQuantity(), DEFAULT_TOTAL_GOLD_QUANTITY));
        vendor.setCurrentGoldPrice(defaultIfNull(request.getCurrentGoldPrice(), DEFAULT_CURRENT_GOLD_PRICE));
        return vendorRepo.save(vendor);
    }

    @Override
    public void deleteVendor(Integer vendorId) {
        getVendorById(vendorId);
        vendorRepo.deleteById(vendorId);
    }

    private void validateVendorRequest(VendorRequestDTO request) {
        if (request.getVendorName() == null || request.getVendorName().isBlank()) {
            throw new IllegalArgumentException("Vendor name is required");
        }
    }

    private Double defaultIfNull(Double value, double defaultValue) {
        return value != null ? value : defaultValue;
    }
}
