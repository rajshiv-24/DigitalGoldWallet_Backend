package com.cg.test.controller;

import com.cg.controller.VendorController;
import com.cg.dto.VendorRequestDTO;
import com.cg.entity.VendorBranch;
import com.cg.entity.Vendors;
import com.cg.exception.ResourceNotFoundException;
import com.cg.repo.VendorBranchRepository;
import com.cg.repo.VendorRepository;
import com.cg.service.VendorService;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

@SpringBootTest
public class VendorControllerTest {

    @MockitoBean
    private VendorService vendorService;

    @MockitoBean
    private VendorRepository vendorRepository;

    @MockitoBean
    private VendorBranchRepository branchRepository;

    @Autowired
    private VendorController vendorController;

    private Vendors vendor;
    private VendorRequestDTO requestDTO;

    @BeforeEach
    public void setUp() {
        vendor = new Vendors();
        vendor.setVendorId(1L);   // ✅ FIXED (Integer)
        vendor.setVendorName("Gold Palace");
        vendor.setDescription("Trusted Gold Dealer");
        vendor.setContactPersonName("Amit Jain");
        vendor.setContactEmail("amit@goldpalace.com");
        vendor.setContactPhone("9988776655");
        vendor.setCurrentGoldPrice(6200.0);
        vendor.setTotalGoldQuantity(5000.00);

        requestDTO = new VendorRequestDTO();
        requestDTO.setVendorName("Gold Palace");
        requestDTO.setDescription("Trusted Gold Dealer");
        requestDTO.setContactPersonName("Amit Jain");
        requestDTO.setContactEmail("amit@goldpalace.com");
        requestDTO.setContactPhone("9988776655");
        requestDTO.setCurrentGoldPrice(6200.0);
        requestDTO.setTotalGoldQuantity(5000.00);
    }

    // ================= CREATE =================

    @Test
    public void testCreateVendor() {
        Mockito.when(vendorService.createVendor(Mockito.any())).thenReturn(vendor);

        ResponseEntity<Vendors> response = vendorController.createVendor(requestDTO);

        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
        Assertions.assertEquals("Gold Palace", response.getBody().getVendorName());
    }

    // ================= GET ALL =================

    @Test
    public void testGetAllVendors() {
        Mockito.when(vendorService.getAllVendors()).thenReturn(List.of(vendor));

        List<Vendors> result = vendorController.getAllVendors();

        Assertions.assertEquals(1, result.size());
    }

    @Test
    public void testGetAllVendorsEmpty() {
        Mockito.when(vendorService.getAllVendors()).thenReturn(List.of());

        List<Vendors> result = vendorController.getAllVendors();

        Assertions.assertTrue(result.isEmpty());
    }

    // ================= GET BY ID =================

    @Test
    public void testGetVendorById() {
        Mockito.when(vendorService.getVendorById(1)).thenReturn(vendor);

        Vendors result = vendorController.getVendorById(1);

        Assertions.assertEquals(1, result.getVendorId());
    }

    @Test
    public void testGetVendorByIdNotFound() {
        Mockito.when(vendorService.getVendorById(99))
                .thenThrow(new ResourceNotFoundException("Not found"));

        Assertions.assertThrows(ResourceNotFoundException.class,
                () -> vendorController.getVendorById(99));
    }

    // ================= UPDATE =================

    @Test
    public void testUpdateVendor() {
        Mockito.when(vendorService.updateVendor(Mockito.eq(1), Mockito.any()))
                .thenReturn(vendor);

        Vendors result = vendorController.updateVendor(1, requestDTO);

        Assertions.assertEquals("Gold Palace", result.getVendorName());
    }

    @Test
    public void testUpdateVendorNotFound() {
        Mockito.when(vendorService.updateVendor(Mockito.eq(99), Mockito.any()))
                .thenThrow(new ResourceNotFoundException("Not found"));

        Assertions.assertThrows(ResourceNotFoundException.class,
                () -> vendorController.updateVendor(99, requestDTO));
    }

    // ================= DELETE =================

    @Test
    public void testDeleteVendor() {
        Mockito.doNothing().when(vendorService).deleteVendor(1);

        ResponseEntity<Void> response = vendorController.deleteVendor(1);

        Assertions.assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    public void testDeleteVendorNotFound() {
        Mockito.doThrow(new ResourceNotFoundException("Not found"))
                .when(vendorService).deleteVendor(99);

        Assertions.assertThrows(ResourceNotFoundException.class,
                () -> vendorController.deleteVendor(99));
    }

    // ================= COUNT =================

    @Test
    public void testCountVendors() {
        Mockito.when(vendorRepository.count()).thenReturn(5L);

        Map<String, Long> result = vendorController.countVendors();

        Assertions.assertEquals(5L, result.get("count"));
    }

    // ================= EXISTS =================

    @Test
    public void testVendorExistsTrue() {
        Mockito.when(vendorRepository.existsById(1)).thenReturn(true);

        Map<String, Boolean> result = vendorController.vendorExists(1);

        Assertions.assertTrue(result.get("exists"));
    }

    @Test
    public void testVendorExistsFalse() {
        Mockito.when(vendorRepository.existsById(99)).thenReturn(false);

        Map<String, Boolean> result = vendorController.vendorExists(99);

        Assertions.assertFalse(result.get("exists"));
    }

    // ================= SEARCH =================

    @Test
    public void testGetVendorsByName() {
        Mockito.when(vendorRepository.findAll()).thenReturn(List.of(vendor));

        List<Vendors> result = vendorController.getVendorsByName("gold");

        Assertions.assertEquals(1, result.size());
    }

    @Test
    public void testGetVendorsByEmail() {
        Mockito.when(vendorRepository.findAll()).thenReturn(List.of(vendor));

        List<Vendors> result = vendorController.getVendorsByEmail("amit@goldpalace.com");

        Assertions.assertEquals(1, result.size());
    }

    // ================= GOLD PRICE =================

    @Test
    public void testGetVendorGoldPrice() {
        Mockito.when(vendorService.getVendorById(1)).thenReturn(vendor);

        Map<String, Object> result = vendorController.getVendorGoldPrice(1);

        Assertions.assertEquals(1, ((Number) result.get("vendorId")).intValue());
        Assertions.assertEquals(6200.0, result.get("currentGoldPrice"));
    }

    // ================= BRANCHES =================

    @Test
    public void testGetVendorBranches() {
        VendorBranch branch = new VendorBranch();
        branch.setVendor(vendor);

        Mockito.when(branchRepository.findAll()).thenReturn(List.of(branch));

        List<VendorBranch> result = vendorController.getVendorBranches(1);

        Assertions.assertEquals(1, result.size());
    }

    @Test
    public void testGetVendorBranchesEmpty() {
        Mockito.when(branchRepository.findAll()).thenReturn(List.of());

        List<VendorBranch> result = vendorController.getVendorBranches(1);

        Assertions.assertTrue(result.isEmpty());
    }
}