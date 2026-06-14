package com.cg.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import com.cg.entity.Vendors;

public interface VendorRepository extends JpaRepository<Vendors, Integer> {
	
}
