package com.carddex.sims2.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.carddex.sims2.security.model.Privilege;

public interface PrivilegeRepository extends JpaRepository<Privilege, Long> {
    
	Privilege findByName(String name);

	List<Privilege> findAll();
}
