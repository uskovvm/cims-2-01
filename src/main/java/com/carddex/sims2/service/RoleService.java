package com.carddex.sims2.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.carddex.sims2.repository.PrivilegeRepository;
import com.carddex.sims2.repository.RoleRepository;
import com.carddex.sims2.rest.dto.RoleDto;
import com.carddex.sims2.security.model.Privilege;
import com.carddex.sims2.security.model.Role;

@Service
public class RoleService {

	@Autowired
	private RoleRepository roleRepository;
	@Autowired
	private PrivilegeRepository privilegeRepository;

	@Transactional
	public List<RoleDto> findAll() {
		List<RoleDto> result = new ArrayList<>();
		roleRepository.findAll().forEach(role -> {
			RoleDto roleDto = new RoleDto(role.getId(), role.getName(), role.getDescription());
			role.getPrivileges().forEach(privilege -> {
				roleDto.getPrivileges().add(privilege.getId());
			});
			result.add(roleDto);
		});
		return result;
	}

	@Transactional
	public void setRolePermissions(RoleDto roledto) {
	
		Role role = roleRepository.getOne(roledto.getId());
		Collection<Privilege> privileges = role.getPrivileges();
		for (Long privilegeId : roledto.getPrivileges()) {
			Privilege privilege = privilegeRepository.getOne(privilegeId);
			privileges.add(privilege);
		}
		roleRepository.save(role);
	}

}
