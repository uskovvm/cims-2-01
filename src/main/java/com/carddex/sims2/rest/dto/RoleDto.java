package com.carddex.sims2.rest.dto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RoleDto {

	private Long id;

	private String name;

	private String description;

	private List<Long> privileges = new ArrayList<>();

	public RoleDto(Long id, String name, String description) {

		this.id = id;
		this.name = name;
		this.description = description;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Collection<Long> getPrivileges() {
		return privileges;
	}

	public void setPrivileges(List<Long> privileges) {
		this.privileges = privileges;
	}

}