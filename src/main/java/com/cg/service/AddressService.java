package com.cg.service;

import com.cg.dto.AddressRequestDTO;
import com.cg.entity.Address;

import java.util.List;

public interface AddressService {

    // Insert a new address into addresses table
    Address createAddress(AddressRequestDTO request);

    // Get address by addresses.address_id — throws ResourceNotFoundException if not found
    Address getAddressById(Integer addressId);

    // Get all addresses
    List<Address> getAllAddresses();

    // Update address fields by ID
    Address updateAddress(Integer addressId, AddressRequestDTO request);

    // Delete address by ID
    void deleteAddress(Integer addressId);
}