package com.cg.test.service;

import com.cg.dto.AddressRequestDTO;
import com.cg.entity.Address;
import com.cg.exception.ResourceNotFoundException;
import com.cg.repo.AddressRepository;
import com.cg.service.AddressService;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.Optional;

@SpringBootTest
public class AddressServiceTest {

    @MockitoBean
    private AddressRepository addressRepo;

    @Autowired
    private AddressService addressService;

    private AddressRequestDTO requestDTO;
    private Address address;

    @BeforeEach
    public void beforeEach() {
        requestDTO = new AddressRequestDTO();
        requestDTO.setStreet("MG Road");
        requestDTO.setCity("Bengaluru");
        requestDTO.setState("Karnataka");
        requestDTO.setPostalCode("560001");
        requestDTO.setCountry("India");

        address = new Address();
        address.setAddressId(1);
        address.setStreet("MG Road");
        address.setCity("Bengaluru");
        address.setState("Karnataka");
        address.setPostalCode("560001");
        address.setCountry("India");
    }

    // ── createAddress ──────────────────────────────────────────

    @Test
    public void testCreateAddress_Success() {
        Mockito.when(addressRepo.save(Mockito.any(Address.class))).thenReturn(address);

        Address result = addressService.createAddress(requestDTO);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("Bengaluru", result.getCity());
        Mockito.verify(addressRepo).save(Mockito.any(Address.class));
    }

    // ── getAddressById ─────────────────────────────────────────

    @Test
    public void testGetAddressById_Found() {
        Mockito.when(addressRepo.findById(1)).thenReturn(Optional.of(address));

        Address result = addressService.getAddressById(1);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getAddressId());
        Mockito.verify(addressRepo).findById(1);
    }

    @Test
    public void testGetAddressById_NotFound() {
        Mockito.when(addressRepo.findById(99)).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class,
                () -> addressService.getAddressById(99));

        Mockito.verify(addressRepo).findById(99);
    }

    // ── getAllAddresses ────────────────────────────────────────

    @Test
    public void testGetAllAddresses_ReturnsList() {
        Mockito.when(addressRepo.findAll()).thenReturn(List.of(address));

        List<Address> result = addressService.getAllAddresses();

        Assertions.assertEquals(1, result.size());
        Mockito.verify(addressRepo).findAll();
    }

    @Test
    public void testGetAllAddresses_EmptyList() {
        Mockito.when(addressRepo.findAll()).thenReturn(List.of());

        List<Address> result = addressService.getAllAddresses();

        Assertions.assertTrue(result.isEmpty());
        Mockito.verify(addressRepo).findAll();
    }

    // ── updateAddress ─────────────────────────────────────────

    @Test
    public void testUpdateAddress_Success() {
        Mockito.when(addressRepo.findById(1)).thenReturn(Optional.of(address));
        Mockito.when(addressRepo.save(Mockito.any(Address.class))).thenReturn(address);

        Address result = addressService.updateAddress(1, requestDTO);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("MG Road", result.getStreet());
        Mockito.verify(addressRepo).findById(1);
        Mockito.verify(addressRepo).save(Mockito.any(Address.class));
    }

    @Test
    public void testUpdateAddress_NotFound() {
        Mockito.when(addressRepo.findById(99)).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class,
                () -> addressService.updateAddress(99, requestDTO));

        Mockito.verify(addressRepo).findById(99);
    }

    // ── deleteAddress ─────────────────────────────────────────

    @Test
    public void testDeleteAddress_Success() {
        Mockito.when(addressRepo.findById(1)).thenReturn(Optional.of(address));
        Mockito.doNothing().when(addressRepo).deleteById(1);

        Assertions.assertDoesNotThrow(() -> addressService.deleteAddress(1));

        Mockito.verify(addressRepo).findById(1);
        Mockito.verify(addressRepo).deleteById(1);
    }

    @Test
    public void testDeleteAddress_NotFound() {
        Mockito.when(addressRepo.findById(99)).thenReturn(Optional.empty());

        Assertions.assertThrows(ResourceNotFoundException.class,
                () -> addressService.deleteAddress(99));

        Mockito.verify(addressRepo).findById(99);
        Mockito.verify(addressRepo, Mockito.never()).deleteById(Mockito.any());
    }
}
