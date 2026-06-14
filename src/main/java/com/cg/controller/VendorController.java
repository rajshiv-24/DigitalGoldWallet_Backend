package com.cg.controller;

import com.cg.dto.VendorRequestDTO;
import com.cg.entity.VendorBranch;
import com.cg.entity.Vendors;
import com.cg.repo.VendorBranchRepository;
import com.cg.repo.VendorRepository;
import com.cg.service.VendorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/vendors")
public class VendorController {

    private final VendorService vendorService;
    private final VendorRepository vendorRepository;
    private final VendorBranchRepository branchRepository;

    public VendorController(VendorService vendorService, VendorRepository vendorRepository,
                            VendorBranchRepository branchRepository) {
        this.vendorService = vendorService;
        this.vendorRepository = vendorRepository;
        this.branchRepository = branchRepository;
    }

    @PostMapping
    public ResponseEntity<Vendors> createVendor(@RequestBody VendorRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(vendorService.createVendor(request));
    }

    @GetMapping
    public List<Vendors> getAllVendors() {
        return vendorService.getAllVendors();
    }

    @GetMapping("/{vendorId}")
    public Vendors getVendorById(@PathVariable Integer vendorId) {
        return vendorService.getVendorById(vendorId);
    }

    @PutMapping("/{vendorId}")
    public Vendors updateVendor(@PathVariable Integer vendorId, @RequestBody VendorRequestDTO request) {
        return vendorService.updateVendor(vendorId, request);
    }

    @DeleteMapping("/{vendorId}")
    public ResponseEntity<Void> deleteVendor(@PathVariable Integer vendorId) {
        vendorService.deleteVendor(vendorId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/count")
    public Map<String, Long> countVendors() {
        return Map.of("count", vendorRepository.count());
    }

    @GetMapping("/{vendorId}/exists")
    public Map<String, Boolean> vendorExists(@PathVariable Integer vendorId) {
        return Map.of("exists", vendorRepository.existsById(vendorId));
    }

    @GetMapping("/by-name")
    public List<Vendors> getVendorsByName(@RequestParam String name) {
        String query = name.toLowerCase();
        return vendorRepository.findAll().stream()
                .filter(vendor -> vendor.getVendorName() != null
                        && vendor.getVendorName().toLowerCase().contains(query))
                .toList();
    }

    @GetMapping("/by-email")
    public List<Vendors> getVendorsByEmail(@RequestParam String email) {
        return vendorRepository.findAll().stream()
                .filter(vendor -> vendor.getContactEmail() != null
                        && vendor.getContactEmail().equalsIgnoreCase(email))
                .toList();
    }

    @GetMapping("/{vendorId}/price")
    public Map<String, Object> getVendorGoldPrice(@PathVariable Integer vendorId) {
        Vendors vendor = vendorService.getVendorById(vendorId);
        return Map.of("vendorId", vendor.getVendorId(), "currentGoldPrice", vendor.getCurrentGoldPrice());
    }

    @GetMapping("/{vendorId}/branches")
    public List<VendorBranch> getVendorBranches(@PathVariable Integer vendorId) {
        return branchRepository.findAll().stream()
                .filter(branch -> branch.getVendor() != null
                        && branch.getVendor().getVendorId().intValue() == vendorId)
                .toList();
    }
}

