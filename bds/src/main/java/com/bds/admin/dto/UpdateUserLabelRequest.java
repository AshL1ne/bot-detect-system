package com.bds.admin.dto;

import lombok.Data;

@Data
public class UpdateUserLabelRequest {
	private String userId;
	private Boolean isMalicious;
	private Double malProb;
}

