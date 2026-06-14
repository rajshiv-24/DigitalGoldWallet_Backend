package com.cg.test.service;



import com.cg.dto.VendorBranchRequestDTO;
import com.cg.entity.Address;
import com.cg.entity.VendorBranch;
import com.cg.entity.Vendors;
import com.cg.exception.ResourceNotFoundException;
import com.cg.repo.AddressRepository;
import com.cg.repo.VendorBranchRepository;
import com.cg.repo.VendorRepository;
import com.cg.service.VendorBranchService;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@SpringBootTest
public class VendorBranchServiceTest {

    @MockitoBean
    private VendorBranchRepository branchRepo;

    @MockitoBean
    private VendorRepository vendorRepo;

    @MockitoBean
    private AddressRepository addressRepo;

    @Autowired
    private VendorBranchService vendorBranchService;

    private VendorBranchRequestDTO requestDTO;
    private VendorBranch branch;
    private Vendors vendor;
    private Address address;

    @BeforeEach
    public void beforeEach() {
        vendor = new Vendors();
        vendor.setVendorId(1L);
        vendor.setVendorName("Gold Point India");
        vendor.setCurrentGoldPrice(6200.0);

        address = new Address();
        address.setAddressId(10);
        address.setCity("Mumbai");
        address.setState("Maharashtra");

        branch = new VendorBranch();
        branch.setBranchId(100);
        branch.setVendor(vendor);
        branch.setAddress(address);
        branch.setQuantity(new BigDecimal("500.00"));
        branch.setCreatedAt(LocalDateTime.now());

        requestDTO = new VendorBranchRequestDTO();
        requestDTO.setVendorId(1);
        requestDTO.setAddressId(10);
        requestDTO.setQuantity(new BigDecimal("500.00"));
    }

    // ── createBranch ───────────────────────────────────────────

    @Test
    public void testCreateBranch_Success() {
        Mockito.when(vendorRepo.findById(1)).thenReturn(Optional.of(vendor));
        Mockito.when(addressRepo.findById(10)).thenReturn(Optional.of(address));
        Mockito.when(branchRepo.save(Mockito.any(VendorBranch.class))).thenReturn(branch);

        VendorBranch result = vendorBranchService.createBranch(requestDTO);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(100, result.getBranchId());
        Mockito.verify(vendorRepo).findById(1);
        Mockito.verify(addressRepo).findById(10);
        Mockito.verify(branchRepo).save(Mockito.any(VendorBranch.class));
    }

    @Test
    public void testCreateBranch_VendorNotFound() {
        Mockito.when(vendorRepo.findById(99)).thenReturn(Optional.empty());
        requestDTO.setVendorId(99);

        Assertions.assertThrows(ResourceNotFoundException.class,
                () -> vendorBranchService.createBranch(requestDTO));

        Mockito.verify(vendorRepo).findById(99);
        Mockito.verify(branchRepo, Mockito.never()).save(Mockito.any());
    }

    @Test
    public void testCreateBranch_AddressNotFound() {
        Mockito.when(vendorRepo.findById(1)).thenReturn(Optional.of(vendor));
        Mockito.when(addressRepo.findById(99)).thenReturn(Optional.empty());
        requestDTO.setAddressId(99);

        Assertions.assertThrows(ResourceNotFoundException.class,
                () -> vendorBranchService.createBranch(requestDTO));

        Mockito.verify(vendorRepo).findById(1);
        Mockito.verify(addressRepo).findById(99);
        Mockito.verify(branchRepo, Mockito.never()).save(Mockito.any());
    }

    // ── getBranchById ──────────────────────────────────────────

    @Test
    public void testGetBranchById_Found() {
        Mockito.when(branchRepo.findById(100)).thenReturn(Optional.of(branch));

        VendorBranch result = vendorBranchService.getBranchById(100);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(100, result.getBranchId());
        Mockito.verify(branchRepo).findById(100);
    }

    @Test
    public void testGetBranchById_NotFound() {
        Mockito.when(branchRepo.findById(999)).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class,
                () -> vendorBranchService.getBranchById(999));

        Mockito.verify(branchRepo).findById(999);
    }

    // ── getAllBranches ─────────────────────────────────────────

    @Test
    public void testGetAllBranches_ReturnsList() {
        Mockito.when(branchRepo.findAll()).thenReturn(List.of(branch));

        List<VendorBranch> result = vendorBranchService.getAllBranches();

        Assertions.assertEquals(1, result.size());
        Mockito.verify(branchRepo).findAll();
    }

    @Test
    public void testGetAllBranches_EmptyList() {
        Mockito.when(branchRepo.findAll()).thenReturn(List.of());

        List<VendorBranch> result = vendorBranchService.getAllBranches();

        Assertions.assertTrue(result.isEmpty());
    }

    // ── deleteBranch ───────────────────────────────────────────

    @Test
    public void testDeleteBranch_Success() {
        Mockito.when(branchRepo.findById(100)).thenReturn(Optional.of(branch));
        Mockito.doNothing().when(branchRepo).deleteById(100);

        Assertions.assertDoesNotThrow(() -> vendorBranchService.deleteBranch(100));

        Mockito.verify(branchRepo).findById(100);
        Mockito.verify(branchRepo).deleteById(100);
    }

    @Test
    public void testDeleteBranch_NotFound() {
        Mockito.when(branchRepo.findById(999)).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class,
                () -> vendorBranchService.deleteBranch(999));

        Mockito.verify(branchRepo, Mockito.never()).deleteById(Mockito.any());
    }
}