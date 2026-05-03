package com.bds.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthUserProfileResponse {
	private String id;
	private String username;
	private String role;
	private Integer status;
}

