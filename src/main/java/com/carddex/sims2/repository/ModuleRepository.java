package com.carddex.sims2.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.carddex.sims2.model.Module;
import com.carddex.sims2.rest.model.ModuleStatus;

/**
 * 
 */
public interface ModuleRepository extends JpaRepository<Module, Long> {
	Module findByName(String name);

	@Modifying
	@Query("update Module m set m.enabled = :status where m.id = :id")
	int setModuleStatus(@Param("status") Boolean status, @Param("id") Long id);
}
