package com.bds.admin.controller;

import com.bds.admin.dto.UpdateAuthUserRoleRequest;
import com.bds.admin.dto.UpdateAuthUserStatusRequest;
import com.bds.admin.dto.UpdateUserLabelRequest;
import com.bds.admin.service.AdminService;
import com.bds.common.Result;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
	private final AdminService adminService;

	public AdminController(AdminService adminService) {
		this.adminService = adminService;
	}

	@PutMapping("/users/{id}/label")
	public Result<Void> updateUserLabel(@PathVariable("id") String userId,
									@RequestBody UpdateUserLabelRequest request) {
		request.setUserId(userId);
		adminService.updateUserLabel(request);
		return Result.success(null);
	}

	@PutMapping("/auth-users/{id}/role")
	public Result<Void> updateAuthUserRole(@PathVariable("id") String userId,
											@RequestBody UpdateAuthUserRoleRequest request) {
		request.setUserId(userId);
		adminService.updateAuthUserRole(request);
		return Result.success(null);
	}

	@PutMapping("/auth-users/{id}/status")
	public Result<Void> updateAuthUserStatus(@PathVariable("id") String userId,
												@RequestBody UpdateAuthUserStatusRequest request) {
		request.setUserId(userId);
		adminService.updateAuthUserStatus(request);
		return Result.success(null);
	}
}
