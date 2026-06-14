package com.cg.test.service;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.cg.dto.VendorRequestDTO;
import com.cg.entity.Vendors;
import com.cg.exception.ResourceNotFoundException;
import com.cg.repo.VendorRepository;
import com.cg.service.VendorService;

@SpringBootTest
public class VendorServiceTest {

    @MockitoBean
    private VendorRepository vendorRepo;

    @Autowired
    private VendorService vendorService;

    private VendorRequestDTO requestDTO;
    private Vendors vendor;

    @BeforeEach
    public void beforeEach() {
        requestDTO = new VendorRequestDTO();
        requestDTO.setVendorName("Gold Point India");
        requestDTO.setDescription("Premium Gold Dealer");
        requestDTO.setContactPersonName("Suresh Kumar");
        requestDTO.setContactEmail("suresh@goldpoint.com");
        requestDTO.setContactPhone("9876543210");
        requestDTO.setWebsiteUrl("https://goldpoint.in");
        requestDTO.setTotalGoldQuantity(1000.0);
        requestDTO.setCurrentGoldPrice(6200.0);

        vendor = new Vendors();
        vendor.setVendorId(1L);
        vendor.setVendorName("Gold Point India");
        vendor.setDescription("Premium Gold Dealer");
        vendor.setContactPersonName("Suresh Kumar");
        vendor.setContactEmail("suresh@goldpoint.com");
        vendor.setContactPhone("9876543210");
        vendor.setWebsiteUrl("https://goldpoint.in");
        vendor.setCurrentGoldPrice(6200.0);
    }

    // ── createVendor ───────────────────────────────────────────

    @Test
    public void testCreateVendor_Success() {
        Mockito.when(vendorRepo.save(Mockito.any(Vendors.class))).thenReturn(vendor);

        Vendors result = vendorService.createVendor(requestDTO);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("Gold Point India", result.getVendorName());
        Mockito.verify(vendorRepo).save(Mockito.any(Vendors.class));
    }

    @Test
    public void testCreateVendor_DefaultsMissingQuantity() {
        requestDTO.setTotalGoldQuantity(null);
        Mockito.when(vendorRepo.save(Mockito.any(Vendors.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Vendors result = vendorService.createVendor(requestDTO);

        Assertions.assertEquals(0.0, result.getTotalGoldQuantity());
        Assertions.assertEquals(6200.0, result.getCurrentGoldPrice());
    }

    @Test
    public void testCreateVendor_NameRequired() {
        requestDTO.setVendorName(" ");

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> vendorService.createVendor(requestDTO));

        Mockito.verify(vendorRepo, Mockito.never()).save(Mockito.any());
    }

    // ── getVendorById ──────────────────────────────────────────

    @Test
    public void testGetVendorById_Found() {
        Mockito.when(vendorRepo.findById(1)).thenReturn(Optional.of(vendor));

        Vendors result = vendorService.getVendorById(1);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getVendorId());
        Mockito.verify(vendorRepo).findById(1);
    }

    @Test
    public void testGetVendorById_NotFound() {
        Mockito.when(vendorRepo.findById(99)).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class,
                () -> vendorService.getVendorById(99));

        Mockito.verify(vendorRepo).findById(99);
    }

    // ── getAllVendors ──────────────────────────────────────────

    @Test
    public void testGetAllVendors_ReturnsList() {
        Mockito.when(vendorRepo.findAll()).thenReturn(List.of(vendor));

        List<Vendors> result = vendorService.getAllVendors();

        Assertions.assertEquals(1, result.size());
        Mockito.verify(vendorRepo).findAll();
    }

    @Test
    public void testGetAllVendors_EmptyList() {
        Mockito.when(vendorRepo.findAll()).thenReturn(List.of());

        List<Vendors> result = vendorService.getAllVendors();

        Assertions.assertTrue(result.isEmpty());
        Mockito.verify(vendorRepo).findAll();
    }

    // ── updateVendor ───────────────────────────────────────────

    @Test
    public void testUpdateVendor_Success() {
        Mockito.when(vendorRepo.findById(1)).thenReturn(Optional.of(vendor));
        Mockito.when(vendorRepo.save(Mockito.any(Vendors.class))).thenReturn(vendor);

        Vendors result = vendorService.updateVendor(1, requestDTO);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("Gold Point India", result.getVendorName());
        Mockito.verify(vendorRepo).findById(1);
        Mockito.verify(vendorRepo).save(Mockito.any(Vendors.class));
    }

    @Test
    public void testUpdateVendor_NotFound() {
        Mockito.when(vendorRepo.findById(99)).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class,
                () -> vendorService.updateVendor(99, requestDTO));

        Mockito.verify(vendorRepo).findById(99);
    }

    // ── deleteVendor ───────────────────────────────────────────

    @Test
    public void testDeleteVendor_Success() {
        Mockito.when(vendorRepo.findById(1)).thenReturn(Optional.of(vendor));
        Mockito.doNothing().when(vendorRepo).deleteById(1);

        Assertions.assertDoesNotThrow(() -> vendorService.deleteVendor(1));

        Mockito.verify(vendorRepo).findById(1);
        Mockito.verify(vendorRepo).deleteById(1);
    }

    @Test
    public void testDeleteVendor_NotFound() {
        Mockito.when(vendorRepo.findById(99)).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class,
                () -> vendorService.deleteVendor(99));

        Mockito.verify(vendorRepo, Mockito.never()).deleteById(Mockito.any());
    }
}
