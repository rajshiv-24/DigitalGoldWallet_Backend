package com.cg.service;

import com.cg.dto.VendorRequestDTO;
import com.cg.entity.Vendors;

import java.util.List;

public interface VendorService {

    // Insert a new vendor into vendors table
    Vendors createVendor(VendorRequestDTO request);

    // Get vendor by vendors.vendor_id — throws ResourceNotFoundException if not found
    Vendors getVendorById(Integer vendorId);

    // Get all vendors
    List<Vendors> getAllVendors();

    // Update vendor details by ID
    Vendors updateVendor(Integer vendorId, VendorRequestDTO request);

    // Delete vendor by ID
    void deleteVendor(Integer vendorId);
}
