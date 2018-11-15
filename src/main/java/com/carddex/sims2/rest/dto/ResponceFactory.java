package com.carddex.sims2.rest.dto;

import com.carddex.sims2.security.model.Privilege;

public class ResponceFactory {
	
	public static PrivilegeResponse getResponceInstance( Privilege privilege) {
		return new PrivilegeResponse(privilege.getId(), privilege.getName());
	}

}
