package com.carddex.sims2.rest.dto;

import java.util.List;

public class PermissionObjects {
	private Long userId; // [optional], ид пользователя
	private Long roleId; // [optional], ид роли
	private Long privilegeId; // ид привилегии
	private List<Long> objects;

	//
	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getRoleId() {
		return roleId;
	}

	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}

	public Long getPrivilegeId() {
		return privilegeId;
	}

	public void setPrivilegeId(Long privilegeId) {
		this.privilegeId = privilegeId;
	}

	public List<Long> getObjects() {
		return objects;
	}

	public void setObjects(List<Long> objects) {
		this.objects = objects;
	}

}
