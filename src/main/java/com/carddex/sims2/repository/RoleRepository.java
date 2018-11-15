package com.carddex.sims2.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.carddex.sims2.security.model.Role;

/**
 * Created by
 */
public interface RoleRepository extends JpaRepository<Role, Long> {

	//@Modifying
	//@Query("SELECT r FROM Role r WHERE r.users.id = :userId")
	//List<Role> getRolesByUser(Long userId); 

}
