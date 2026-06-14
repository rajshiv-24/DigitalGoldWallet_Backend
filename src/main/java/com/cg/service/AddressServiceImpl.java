package com.cg.service;

import com.cg.dto.AddressRequestDTO;
import com.cg.entity.Address;
import com.cg.exception.ResourceNotFoundException;
import com.cg.repo.AddressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressServiceImpl implements AddressService {

    @Autowired
    private AddressRepository addressRepo;

    @Override
    public Address createAddress(AddressRequestDTO request) {
        Address address = new Address();
        address.setStreet(request.getStreet());
        address.setCity(request.getCity());
        address.setState(request.getState());
        address.setPostalCode(request.getPostalCode());
        address.setCountry(request.getCountry());
        return addressRepo.save(address);
    }

    @Override
    public Address getAddressById(Integer addressId) {
        return addressRepo.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Address not found with id: " + addressId));
    }

    @Override
    public List<Address> getAllAddresses() {
        return addressRepo.findAll();
    }

    @Override
    public Address updateAddress(Integer addressId, AddressRequestDTO request) {
        Address address = getAddressById(addressId);
        address.setStreet(request.getStreet());
        address.setCity(request.getCity());
        address.setState(request.getState());
        address.setPostalCode(request.getPostalCode());
        address.setCountry(request.getCountry());
        return addressRepo.save(address);
    }

    @Override
    public void deleteAddress(Integer addressId) {
        getAddressById(addressId); // throws 404 if not found
        addressRepo.deleteById(addressId);
    }
}