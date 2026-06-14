package com.cg.service;

import com.cg.dto.VendorBranchRequestDTO;
import com.cg.entity.VendorBranch;

import java.util.List;

public interface VendorBranchService {

    // Create a new branch linked to a vendor and an address
    VendorBranch createBranch(VendorBranchRequestDTO request);

    // Get branch by vendor_branches.branch_id — throws ResourceNotFoundException if not found
    VendorBranch getBranchById(Integer branchId);

    // Get all branches (all vendor locations)
    List<VendorBranch> getAllBranches();

    // Delete branch by ID
    void deleteBranch(Integer branchId);
}
