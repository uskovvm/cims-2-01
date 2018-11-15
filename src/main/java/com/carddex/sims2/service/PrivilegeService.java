package com.carddex.sims2.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.carddex.sims2.repository.PrivilegeRepository;
import com.carddex.sims2.rest.dto.PrivilegeResponse;
import com.carddex.sims2.rest.dto.ResponceFactory;
import com.carddex.sims2.security.model.Privilege;

@Service
public class PrivilegeService {

	@Autowired
	private PrivilegeRepository privilegeRepository;

	public List<Privilege> loadAllPrivileges() {
		return privilegeRepository.findAll();
	}

	public List<PrivilegeResponse> getPrivilegeResponseList() {
		
		List<PrivilegeResponse> list = new ArrayList<>();
		
		loadAllPrivileges().forEach(privilege -> list.add(ResponceFactory.getResponceInstance(privilege)));

		return list;
	}
}
