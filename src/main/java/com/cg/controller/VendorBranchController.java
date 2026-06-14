package com.cg.controller;

import com.cg.dto.VendorBranchRequestDTO;
import com.cg.entity.Address;
import com.cg.entity.VendorBranch;
import com.cg.entity.Vendors;
import com.cg.exception.ResourceNotFoundException;
import com.cg.repo.AddressRepository;
import com.cg.repo.VendorBranchRepository;
import com.cg.repo.VendorRepository;
import com.cg.service.VendorBranchService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/branches")
public class VendorBranchController {

    private final VendorBranchService branchService;
    private final VendorBranchRepository branchRepository;
    private final VendorRepository vendorRepository;
    private final AddressRepository addressRepository;

    public VendorBranchController(VendorBranchService branchService, VendorBranchRepository branchRepository,
                                  VendorRepository vendorRepository, AddressRepository addressRepository) {
        this.branchService = branchService;
        this.branchRepository = branchRepository;
        this.vendorRepository = vendorRepository;
        this.addressRepository = addressRepository;
    }

    @PostMapping
    public ResponseEntity<VendorBranch> createBranch(@RequestBody VendorBranchRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(branchService.createBranch(request));
    }

    @GetMapping
    public List<VendorBranch> getAllBranches() {
        return branchService.getAllBranches();
    }

    @GetMapping("/{branchId}")
    public VendorBranch getBranchById(@PathVariable Integer branchId) {
        return branchService.getBranchById(branchId);
    }

    @PutMapping("/{branchId}")
    public VendorBranch updateBranch(@PathVariable Integer branchId, @RequestBody VendorBranchRequestDTO request) {
        VendorBranch branch = branchService.getBranchById(branchId);
        Vendors vendor = vendorRepository.findById(request.getVendorId())
                .orElseThrow(() -> new ResourceNotFoundException("Vendor not found with id: " + request.getVendorId()));
        Address address = addressRepository.findById(request.getAddressId())
                .orElseThrow(() -> new ResourceNotFoundException("Address not found with id: " + request.getAddressId()));
        branch.setVendor(vendor);
        branch.setAddress(address);
        branch.setQuantity(request.getQuantity());
        return branchRepository.save(branch);
    }

    @DeleteMapping("/{branchId}")
    public ResponseEntity<Void> deleteBranch(@PathVariable Integer branchId) {
        branchService.deleteBranch(branchId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/count")
    public Map<String, Long> countBranches() {
        return Map.of("count", branchRepository.count());
    }

    @GetMapping("/{branchId}/exists")
    public Map<String, Boolean> branchExists(@PathVariable Integer branchId) {
        return Map.of("exists", branchRepository.existsById(branchId));
    }

    @GetMapping("/by-vendor/{vendorId}")
    public List<VendorBranch> getBranchesByVendor(@PathVariable Integer vendorId) {
        return branchRepository.findAll().stream()
                .filter(branch -> branch.getVendor() != null
                        && branch.getVendor().getVendorId().intValue() == vendorId)
                .toList();
    }

    @GetMapping("/by-city")
    public List<VendorBranch> getBranchesByCity(@RequestParam String city) {
        return branchRepository.findAll().stream()
                .filter(branch -> branch.getAddress() != null
                        && branch.getAddress().getCity() != null
                        && branch.getAddress().getCity().equalsIgnoreCase(city))
                .toList();
    }

    @GetMapping("/with-min-quantity")
    public List<VendorBranch> getBranchesWithMinimumQuantity(@RequestParam BigDecimal quantity) {
        return branchRepository.findAll().stream()
                .filter(branch -> branch.getQuantity() != null && branch.getQuantity().compareTo(quantity) >= 0)
                .toList();
    }

    @GetMapping("/{branchId}/stock")
    public Map<String, Object> getBranchStock(@PathVariable Integer branchId) {
        VendorBranch branch = branchService.getBranchById(branchId);
        return Map.of("branchId", branch.getBranchId(), "quantity", branch.getQuantity());
    }

}

