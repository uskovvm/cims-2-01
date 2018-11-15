package com.carddex.sims2.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

/**
 *
 * @author uvm
 */
@Entity
@Table(name = "DEVICE_ACL")
@IdClass(DeviceACL.class)
public class DeviceACL implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Column(name = "deviceid")
    private Long deviceId;
    @Id
    @Column(name = "roleid")
    private Long roleId;
    @Id
    @Column(name = "permissionid")
    private Long permissionId;

	public DeviceACL() {
	}

    public DeviceACL(Long roleId, Long privilegeId, Long deviceId) {
		this.deviceId = deviceId;
		this.roleId = roleId;
		this.permissionId = privilegeId;
	}

	public Long getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(Long deviceId) {
		this.deviceId = deviceId;
	}

	public Long getRoleId() {
		return roleId;
	}

	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}

	public Long getPermissionId() {
		return permissionId;
	}

	public void setPermissionId(Long permissionId) {
		this.permissionId = permissionId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((deviceId == null) ? 0 : deviceId.hashCode());
		result = prime * result + ((permissionId == null) ? 0 : permissionId.hashCode());
		result = prime * result + ((roleId == null) ? 0 : roleId.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DeviceACL other = (DeviceACL) obj;
		if (deviceId == null) {
			if (other.deviceId != null)
				return false;
		} else if (!deviceId.equals(other.deviceId))
			return false;
		if (permissionId == null) {
			if (other.permissionId != null)
				return false;
		} else if (!permissionId.equals(other.permissionId))
			return false;
		if (roleId == null) {
			if (other.roleId != null)
				return false;
		} else if (!roleId.equals(other.roleId))
			return false;
		return true;
	}
    
    

}
