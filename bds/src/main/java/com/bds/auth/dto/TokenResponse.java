package com.bds.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TokenResponse {
	private String token;
	private String tokenType;
	/** Login / register 返回，便于前端写入路由守卫所需角色 */
	private String role;
}

