package com.carddex.sims2.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.carddex.sims2.model.Module;
import com.carddex.sims2.repository.ModuleRepository;
import com.carddex.sims2.rest.model.ModuleStatus;

@Service
@Transactional
public class ModuleService {
	@Autowired
	private ModuleRepository moduleRepository;

	public List<Module> loadAllModules() {
		List<Module> modules = moduleRepository.findAll();
		return modules;
	}

	public Boolean setModuleStatus(List<ModuleStatus> list) {
		int count = list.size();
		for (ModuleStatus moduleStatus : list) {
			count = count - moduleRepository.setModuleStatus(moduleStatus.getStatus(), moduleStatus.getId());
		}

		return count == 0 ? true : false;
	}
}
