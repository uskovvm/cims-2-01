package com.carddex.sims2.rest.dto;

import java.io.Serializable;

/**
 * Created by
 */
public class PrivilegeResponse implements Serializable {
	private static final long serialVersionUID = 4859384523349689852L;

	private final Long id;
	private final String name;
	
	
	public PrivilegeResponse(Long id, String name) {

		this.id = id;
		this.name = name;
	}
	
	public Long getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
}
