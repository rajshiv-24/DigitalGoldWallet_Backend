package com.cg.controller;

import com.cg.dto.AddressRequestDTO;
import com.cg.entity.Address;
import com.cg.repo.AddressRepository;
import com.cg.service.AddressService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/addresses")
public class AddressController {

    private final AddressService addressService;
    private final AddressRepository addressRepository;

    public AddressController(AddressService addressService, AddressRepository addressRepository) {
        this.addressService = addressService;
        this.addressRepository = addressRepository;
    }

    @PostMapping
    public ResponseEntity<Address> createAddress(@RequestBody AddressRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(addressService.createAddress(request));
    }

    @GetMapping
    public List<Address> getAllAddresses() {
        return addressService.getAllAddresses();
    }

    @GetMapping("/{addressId}")
    public Address getAddressById(@PathVariable Integer addressId) {
        return addressService.getAddressById(addressId);
    }

    @PutMapping("/{addressId}")
    public Address updateAddress(@PathVariable Integer addressId, @RequestBody AddressRequestDTO request) {
        return addressService.updateAddress(addressId, request);
    }

    @DeleteMapping("/{addressId}")
    public ResponseEntity<Void> deleteAddress(@PathVariable Integer addressId) {
        addressService.deleteAddress(addressId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/count")
    public Map<String, Long> countAddresses() {
        return Map.of("count", addressRepository.count());
    }

    @GetMapping("/{addressId}/exists")
    public Map<String, Boolean> addressExists(@PathVariable Integer addressId) {
        return Map.of("exists", addressRepository.existsById(addressId));
    }

    @GetMapping("/by-city")
    public List<Address> getAddressesByCity(@RequestParam String city) {
        return addressRepository.findAll().stream()
                .filter(address -> equalsIgnoreCase(address.getCity(), city))
                .toList();
    }

    @GetMapping("/by-country")
    public List<Address> getAddressesByCountry(@RequestParam String country) {
        return addressRepository.findAll().stream()
                .filter(address -> equalsIgnoreCase(address.getCountry(), country))
                .toList();
    }

    @GetMapping("/search")
    public List<Address> searchAddresses(@RequestParam String q) {
        String query = q.toLowerCase();
        return addressRepository.findAll().stream()
                .filter(address -> contains(address.getStreet(), query)
                        || contains(address.getCity(), query)
                        || contains(address.getState(), query)
                        || contains(address.getPostalCode(), query)
                        || contains(address.getCountry(), query))
                .toList();
    }

    private boolean equalsIgnoreCase(String value, String expected) {
        return value != null && value.equalsIgnoreCase(expected);
    }

    private boolean contains(String value, String query) {
        return value != null && value.toLowerCase().contains(query);
    }
}
