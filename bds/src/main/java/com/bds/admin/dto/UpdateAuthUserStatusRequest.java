package com.bds.admin.dto;

import lombok.Data;

@Data
public class UpdateAuthUserStatusRequest {
	private String userId;
	private Integer status;
}

