package com.carddex.sims2.rest.model;

public class ModuleStatus {
	
	private Long id;
	private Boolean status;
	
	public ModuleStatus(Long id, Boolean status) {
		super();
		this.id = id;
		this.status = status;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Boolean getStatus() {
		return status;
	}
	public void setStatus(Boolean status) {
		this.status = status;
	}
	
}
