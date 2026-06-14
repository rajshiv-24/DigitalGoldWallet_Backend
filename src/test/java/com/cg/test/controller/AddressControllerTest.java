package com.cg.test.controller;

import com.cg.controller.AddressController;
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
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

@SpringBootTest(properties = {
        "spring.main.allow-bean-definition-overriding=true",
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration"
})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class AddressControllerTest {

    @MockitoBean
    private AddressService addressService;

    @MockitoBean
    private AddressRepository addressRepository;

    @Autowired
    private AddressController addressController;

    private AddressRequestDTO requestDTO;
    private Address address;

    @BeforeEach
    void setup() {
        requestDTO = new AddressRequestDTO(
                "Connaught Place",
                "New Delhi",
                "Delhi",
                "110001",
                "India"
        );

        address = new Address();
        address.setAddressId(1);
        address.setStreet("Connaught Place");
        address.setCity("New Delhi");
        address.setState("Delhi");
        address.setPostalCode("110001");
        address.setCountry("India");
    }

    @Test
    void testCreateAddress() {
        Mockito.when(addressService.createAddress(Mockito.any()))
                .thenReturn(address);

        ResponseEntity<Address> response = addressController.createAddress(requestDTO);

        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    void testGetAllAddresses() {
        Mockito.when(addressService.getAllAddresses())
                .thenReturn(List.of(address));

        Assertions.assertEquals(1, addressController.getAllAddresses().size());
    }

    @Test
    void testGetAddressById() {
        Mockito.when(addressService.getAddressById(1))
                .thenReturn(address);

        Assertions.assertEquals(1, addressController.getAddressById(1).getAddressId());
    }

    @Test
    void testGetAddressById_NotFound() {
        Mockito.when(addressService.getAddressById(99))
                .thenThrow(new ResourceNotFoundException("Not found"));

        Assertions.assertThrows(ResourceNotFoundException.class,
                () -> addressController.getAddressById(99));
    }

    @Test
    void testUpdateAddress() {
        Mockito.when(addressService.updateAddress(Mockito.eq(1), Mockito.any()))
                .thenReturn(address);

        Assertions.assertEquals("New Delhi",
                addressController.updateAddress(1, requestDTO).getCity());
    }

    @Test
    void testDeleteAddress() {
        Mockito.doNothing().when(addressService).deleteAddress(1);

        ResponseEntity<Void> response = addressController.deleteAddress(1);

        Assertions.assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void testCountAddresses() {
        Mockito.when(addressRepository.count()).thenReturn(10L);

        Assertions.assertEquals(10L,
                addressController.countAddresses().get("count"));
    }

    @Test
    void testAddressExists() {
        Mockito.when(addressRepository.existsById(1)).thenReturn(true);

        Assertions.assertTrue(
                addressController.addressExists(1).get("exists"));
    }

    @Test
    void testGetAddressesByCity() {
        Mockito.when(addressRepository.findAll()).thenReturn(List.of(address));

        Assertions.assertEquals(1,
                addressController.getAddressesByCity("New Delhi").size());
    }

    @Test
    void testGetAddressesByCountry() {
        Mockito.when(addressRepository.findAll()).thenReturn(List.of(address));

        Assertions.assertEquals(1,
                addressController.getAddressesByCountry("India").size());
    }

    @Test
    void testSearchAddresses() {
        Mockito.when(addressRepository.findAll()).thenReturn(List.of(address));

        Assertions.assertFalse(
                addressController.searchAddresses("delhi").isEmpty());
    }
}