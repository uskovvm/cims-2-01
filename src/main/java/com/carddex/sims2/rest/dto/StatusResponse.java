package com.carddex.sims2.rest.dto;

import java.io.Serializable;

/**
 * Created by
 */
public class StatusResponse implements Serializable {

    private static final long serialVersionUID = 1250166508152483573L;

    private final String status;

    public StatusResponse(String status) {
        this.status = status;
    }

	public String getStatus() {
        return this.status;
    }
}
