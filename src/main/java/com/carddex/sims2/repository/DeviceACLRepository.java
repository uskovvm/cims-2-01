package com.carddex.sims2.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.carddex.sims2.model.Device;
import com.carddex.sims2.model.DeviceACL;

public interface DeviceACLRepository extends JpaRepository<DeviceACL, Long> {

	@Modifying
	@Query("SELECT d FROM DeviceACL d WHERE d.roleId = :roleId and d.permissionId = :permissionId")
	List<DeviceACL> getPermissionObjectsByRoleAndPrivilege(Long roleId, Long permissionId);

}
