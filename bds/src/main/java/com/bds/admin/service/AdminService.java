package com.bds.admin.service;

import com.bds.admin.dto.UpdateAuthUserRoleRequest;
import com.bds.admin.dto.UpdateAuthUserStatusRequest;
import com.bds.admin.dto.UpdateUserLabelRequest;
import com.bds.auth.entity.AuthUserEntity;
import com.bds.auth.mapper.AuthUserMapper;
import com.bds.user.entity.UserEntity;
import com.bds.user.mapper.UserMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Set;

@Service
public class AdminService {
	private static final Set<String> ALLOWED_ROLES = Set.of("USER", "ADMIN");

	private final UserMapper userMapper;
	private final AuthUserMapper authUserMapper;

	public AdminService(UserMapper userMapper, AuthUserMapper authUserMapper) {
		this.userMapper = userMapper;
		this.authUserMapper = authUserMapper;
	}

	public void updateUserLabel(UpdateUserLabelRequest request) {
		String userId = requireUserId(request.getUserId());
		if (request.getIsMalicious() == null && request.getMalProb() == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No label fields provided");
		}
		UserEntity existing = userMapper.selectById(userId);
		if (existing == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
		}
		UserEntity update = new UserEntity();
		update.setUserId(userId);
		update.setIsMalicious(request.getIsMalicious());
		update.setMalProb(request.getMalProb());
		userMapper.updateById(update);
	}

	public void updateAuthUserRole(UpdateAuthUserRoleRequest request) {
		String userId = requireUserId(request.getUserId());
		String role = normalizeRole(request.getRole());
		AuthUserEntity existing = authUserMapper.selectById(userId);
		if (existing == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Auth user not found");
		}
		AuthUserEntity update = new AuthUserEntity();
		update.setId(userId);
		update.setRole(role);
		update.setUpdatedAt(LocalDateTime.now());
		authUserMapper.updateById(update);
	}

	public void updateAuthUserStatus(UpdateAuthUserStatusRequest request) {
		String userId = requireUserId(request.getUserId());
		Integer status = request.getStatus();
		if (status == null || (status != 0 && status != 1)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status must be 0 or 1");
		}
		AuthUserEntity existing = authUserMapper.selectById(userId);
		if (existing == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Auth user not found");
		}
		AuthUserEntity update = new AuthUserEntity();
		update.setId(userId);
		update.setStatus(status);
		update.setUpdatedAt(LocalDateTime.now());
		authUserMapper.updateById(update);
	}

	private String requireUserId(String userId) {
		if (userId == null || userId.isBlank()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "userId is required");
		}
		return userId;
	}

	private String normalizeRole(String role) {
		if (role == null || role.isBlank()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "role is required");
		}
		String normalized = role.trim().toUpperCase();
		if (!ALLOWED_ROLES.contains(normalized)) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unsupported role");
		}
		return normalized;
	}
}
