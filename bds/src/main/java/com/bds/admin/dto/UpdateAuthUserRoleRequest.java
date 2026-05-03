package com.bds.admin.dto;

import lombok.Data;

@Data
public class UpdateAuthUserRoleRequest {
	private String userId;
	private String role;
}

