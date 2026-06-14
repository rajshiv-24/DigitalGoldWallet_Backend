package com.cg.service;

import com.cg.dto.VendorBranchRequestDTO;
import com.cg.entity.Address;
import com.cg.entity.VendorBranch;
import com.cg.entity.Vendors;
import com.cg.exception.ResourceNotFoundException;
import com.cg.repo.AddressRepository;
import com.cg.repo.VendorBranchRepository;
import com.cg.repo.VendorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class VendorBranchServiceImpl implements VendorBranchService {

    @Autowired
    private VendorBranchRepository branchRepo;

    @Autowired
    private VendorRepository vendorRepo;

    @Autowired
    private AddressRepository addressRepo;

    @Override
    public VendorBranch createBranch(VendorBranchRequestDTO request) {
        Vendors vendor = vendorRepo.findById(request.getVendorId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Vendor not found with id: " + request.getVendorId()));

        Address address = addressRepo.findById(request.getAddressId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Address not found with id: " + request.getAddressId()));

        VendorBranch branch = new VendorBranch();
        branch.setVendor(vendor);
        branch.setAddress(address);
        branch.setQuantity(request.getQuantity());
        branch.setCreatedAt(LocalDateTime.now());
        return branchRepo.save(branch);
    }

    @Override
    public VendorBranch getBranchById(Integer branchId) {
        return branchRepo.findById(branchId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Branch not found with id: " + branchId));
    }

    @Override
    public List<VendorBranch> getAllBranches() {
        return branchRepo.findAll();
    }

    @Override
    public void deleteBranch(Integer branchId) {
        getBranchById(branchId);
        branchRepo.deleteById(branchId);
    }
}
