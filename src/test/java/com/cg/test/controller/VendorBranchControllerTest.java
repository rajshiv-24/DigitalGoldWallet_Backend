package com.cg.test.controller;
import com.cg.controller.VendorBranchController;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@SpringBootTest
public class VendorBranchControllerTest {

    @MockitoBean
    private VendorBranchService branchService;

    @MockitoBean
    private VendorBranchRepository branchRepository;

    @MockitoBean
    private VendorRepository vendorRepository;

    @MockitoBean
    private AddressRepository addressRepository;

    @Autowired
    private VendorBranchController vendorBranchController;

    private VendorBranch branch;
    private Vendors vendor;
    private Address address;
    private VendorBranchRequestDTO requestDTO;

    @BeforeEach
    public void beforeEach() {
        vendor = new Vendors();
        vendor.setVendorId(1L);
        vendor.setVendorName("Gold Palace");
        vendor.setCurrentGoldPrice(6200.0);

        address = new Address();
        address.setAddressId(10);
        address.setCity("Pune");
        address.setState("Maharashtra");

        branch = new VendorBranch();
        branch.setBranchId(100);
        branch.setVendor(vendor);
        branch.setAddress(address);
        branch.setQuantity(new BigDecimal("250.00"));
        branch.setCreatedAt(LocalDateTime.now());

        requestDTO = new VendorBranchRequestDTO();
        requestDTO.setVendorId(1);
        requestDTO.setAddressId(10);
        requestDTO.setQuantity(new BigDecimal("250.00"));
    }

    // ── createBranch ───────────────────────────────────────────

    @Test
    public void testCreateBranch_Returns201() {
        Mockito.when(branchService.createBranch(Mockito.any(VendorBranchRequestDTO.class))).thenReturn(branch);

        ResponseEntity<VendorBranch> response = vendorBranchController.createBranch(requestDTO);

        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertEquals(100, response.getBody().getBranchId());
        Mockito.verify(branchService).createBranch(Mockito.any(VendorBranchRequestDTO.class));
    }

    // ── getAllBranches ─────────────────────────────────────────

    @Test
    public void testGetAllBranches_ReturnsList() {
        Mockito.when(branchService.getAllBranches()).thenReturn(List.of(branch));

        List<VendorBranch> result = vendorBranchController.getAllBranches();

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(100, result.get(0).getBranchId());
        Mockito.verify(branchService).getAllBranches();
    }

    @Test
    public void testGetAllBranches_EmptyList() {
        Mockito.when(branchService.getAllBranches()).thenReturn(List.of());

        List<VendorBranch> result = vendorBranchController.getAllBranches();

        Assertions.assertTrue(result.isEmpty());
    }

    // ── getBranchById ──────────────────────────────────────────

    @Test
    public void testGetBranchById_Found() {
        Mockito.when(branchService.getBranchById(100)).thenReturn(branch);

        VendorBranch result = vendorBranchController.getBranchById(100);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(100, result.getBranchId());
        Mockito.verify(branchService).getBranchById(100);
    }

    @Test
    public void testGetBranchById_NotFound() {
        Mockito.when(branchService.getBranchById(999))
               .thenThrow(new ResourceNotFoundException("Branch not found with id: 999"));

        Assertions.assertThrows(ResourceNotFoundException.class,
                () -> vendorBranchController.getBranchById(999));
    }

    // ── updateBranch ───────────────────────────────────────────

    @Test
    public void testUpdateBranch_Success() {
        Mockito.when(branchService.getBranchById(100)).thenReturn(branch);
        Mockito.when(vendorRepository.findById(1)).thenReturn(Optional.of(vendor));
        Mockito.when(addressRepository.findById(10)).thenReturn(Optional.of(address));
        Mockito.when(branchRepository.save(Mockito.any(VendorBranch.class))).thenReturn(branch);

        VendorBranch result = vendorBranchController.updateBranch(100, requestDTO);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(100, result.getBranchId());
        Mockito.verify(branchService).getBranchById(100);
        Mockito.verify(branchRepository).save(Mockito.any(VendorBranch.class));
    }

    @Test
    public void testUpdateBranch_BranchNotFound() {
        Mockito.when(branchService.getBranchById(999))
               .thenThrow(new ResourceNotFoundException("Branch not found with id: 999"));

        Assertions.assertThrows(ResourceNotFoundException.class,
                () -> vendorBranchController.updateBranch(999, requestDTO));
    }

    @Test
    public void testUpdateBranch_VendorNotFound() {
        Mockito.when(branchService.getBranchById(100)).thenReturn(branch);
        Mockito.when(vendorRepository.findById(1)).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class,
                () -> vendorBranchController.updateBranch(100, requestDTO));
    }

    // ── deleteBranch ───────────────────────────────────────────

    @Test
    public void testDeleteBranch_Returns204() {
        Mockito.doNothing().when(branchService).deleteBranch(100);

        ResponseEntity<Void> response = vendorBranchController.deleteBranch(100);

        Assertions.assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        Mockito.verify(branchService).deleteBranch(100);
    }

    @Test
    public void testDeleteBranch_NotFound() {
        Mockito.doThrow(new ResourceNotFoundException("Branch not found with id: 999"))
               .when(branchService).deleteBranch(999);

        Assertions.assertThrows(ResourceNotFoundException.class,
                () -> vendorBranchController.deleteBranch(999));
    }

    // ── countBranches ─────────────────────────────────────────

    @Test
    public void testCountBranches() {
        Mockito.when(branchRepository.count()).thenReturn(15L);

        Map<String, Long> result = vendorBranchController.countBranches();

        Assertions.assertEquals(15L, result.get("count"));
        Mockito.verify(branchRepository).count();
    }

    // ── branchExists ───────────────────────────────────────────

    @Test
    public void testBranchExists_True() {
        Mockito.when(branchRepository.existsById(100)).thenReturn(true);

        Map<String, Boolean> result = vendorBranchController.branchExists(100);

        Assertions.assertTrue(result.get("exists"));
    }

    @Test
    public void testBranchExists_False() {
        Mockito.when(branchRepository.existsById(999)).thenReturn(false);

        Map<String, Boolean> result = vendorBranchController.branchExists(999);

        Assertions.assertFalse(result.get("exists"));
    }

    // ── getBranchStock ─────────────────────────────────────────

    @Test
    public void testGetBranchStock_Success() {
        Mockito.when(branchService.getBranchById(100)).thenReturn(branch);

        Map<String, Object> result = vendorBranchController.getBranchStock(100);

        Assertions.assertEquals(100, result.get("branchId"));
        Assertions.assertEquals(new BigDecimal("250.00"), result.get("quantity"));
    }
}