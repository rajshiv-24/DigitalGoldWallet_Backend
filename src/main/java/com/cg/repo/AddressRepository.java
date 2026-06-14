package com.cg.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import com.cg.entity.Address;

public interface AddressRepository extends JpaRepository<Address, Integer> {
}