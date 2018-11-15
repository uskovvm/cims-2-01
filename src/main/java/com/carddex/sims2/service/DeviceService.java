package com.carddex.sims2.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.carddex.sims2.model.DeviceACL;
import com.carddex.sims2.repository.DeviceACLRepository;
import com.carddex.sims2.repository.DeviceRepository;
import com.carddex.sims2.repository.DeviceTypeRepository;
import com.carddex.sims2.repository.UserRepository;
import com.carddex.sims2.rest.dto.DeviceDto;
import com.carddex.sims2.rest.dto.DeviceTypeDto;
import com.carddex.sims2.rest.dto.PermissionObjects;
import com.carddex.sims2.security.model.Role;

@Service
public class DeviceService {

	@Autowired
	private DeviceTypeRepository deviceTypeRepository;

	@Autowired
	private DeviceRepository deviceRepository;

	@Autowired
	private DeviceACLRepository deviceACLRepository;

	@Autowired
	private UserRepository userRepository;

	public List<DeviceTypeDto> findAllDeviceType() {

		List<DeviceTypeDto> list = new ArrayList<>();

		deviceTypeRepository.findAll().forEach(deviceType -> list
				.add(new DeviceTypeDto(deviceType.getId(), deviceType.getName(), deviceType.getDescription())));

		return list;
	}

	public List<DeviceDto> findAllDevice() {

		List<DeviceDto> list = new ArrayList<>();
		Integer[] directionOpenMask = { new Integer(2), new Integer(2) };

		deviceRepository.findAll()
				.forEach(device -> list.add(new DeviceDto(device.getId(), device.getName(), device.getDescription(),
						device.getEnabled(), device.getTypeId(), device.getConnectionId(), device.getProtocolId(),
						new Integer(2), directionOpenMask, device.getZoneAId(), device.getZoneBId(),
						device.getAccessModeAB(), device.getAccessModeBA())));

		return list;
	}

	public List<Long> getPermissionObjectsByRoleAndPrivilege(Long roleId, Long privilegeId) {

		List<Long> list = new ArrayList<>();

		deviceACLRepository.getPermissionObjectsByRoleAndPrivilege(roleId, privilegeId)
				.forEach(deviceACL -> list.add(deviceACL.getDeviceId()));

		return list;
	}

	private List<Role> getRolesByUserId(Long userId) {
		return userRepository.findById(userId).get().getRoles();
	}

	public List<Long> getPermissionObjectsByUserAndPrivilege(Long userId, Long privilegeId) {

		Set<Long> result = new HashSet<>();

		getRolesByUserId(userId).forEach(role -> {
			deviceACLRepository.getPermissionObjectsByRoleAndPrivilege(role.getId(), privilegeId)
					.forEach(deviceACL -> result.add(deviceACL.getDeviceId()));
		});

		return new ArrayList<Long>(result);
	}

	private void setDevices(Long roleId, Long privilegeId, List<Long> deviceIds) {

		deviceIds.forEach(deviceId -> {
			deviceACLRepository.save(new DeviceACL(roleId, privilegeId, deviceId));
		});
	}

	public boolean setPermissionObjects(PermissionObjects permissionObjects) {

		Long userId = permissionObjects.getUserId();
		Long roleId = permissionObjects.getRoleId();
		Long privilegeId = permissionObjects.getPrivilegeId();
		List<Long> deviceIds = permissionObjects.getObjects();

		if (userId != null && userId > 0) {
			getRolesByUserId(userId).forEach(role -> setDevices(role.getId(), privilegeId, deviceIds));
		} else if (roleId != null && roleId > 0) {
			setDevices(roleId, privilegeId, deviceIds);
		} else {
			return false;
		}

		return true;
	}
}
