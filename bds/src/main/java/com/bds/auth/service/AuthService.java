package com.bds.auth.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bds.auth.dto.AuthUserProfileResponse;
import com.bds.auth.dto.LoginRequest;
import com.bds.auth.dto.RegisterRequest;
import com.bds.auth.dto.TokenResponse;
import com.bds.auth.entity.AuthUserEntity;
import com.bds.auth.mapper.AuthUserMapper;
import com.bds.auth.security.UserPrincipal;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
public class AuthService {
	private final AuthUserMapper authUserMapper;
	private final JwtService jwtService;
	private final PasswordService passwordService;

	public AuthService(AuthUserMapper authUserMapper, JwtService jwtService, PasswordService passwordService) {
		this.authUserMapper = authUserMapper;
		this.jwtService = jwtService;
		this.passwordService = passwordService;
	}

	public TokenResponse login(LoginRequest request) {
		AuthUserEntity user = authUserMapper.selectOne(new LambdaQueryWrapper<AuthUserEntity>()
				.eq(AuthUserEntity::getUsername, request.getUsername()));
		if (user == null || user.getStatus() != null && user.getStatus() == 0) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
		}
		if (!passwordService.matches(request.getPassword(), user.getPasswordSalt(), user.getPasswordHash())) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
		}
		String token = jwtService.issueToken(user);
		return new TokenResponse(token, "Bearer");
	}

	public TokenResponse register(RegisterRequest request) {
		AuthUserEntity existing = authUserMapper.selectOne(new LambdaQueryWrapper<AuthUserEntity>()
				.eq(AuthUserEntity::getUsername, request.getUsername()));
		if (existing != null) {
			throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists");
		}
		PasswordService.HashResult hashResult = passwordService.hash(request.getPassword());
		AuthUserEntity user = new AuthUserEntity();
		user.setUsername(request.getUsername());
		user.setPasswordSalt(hashResult.salt());
		user.setPasswordHash(hashResult.hash());
		user.setRole("USER");
		user.setStatus(1);
		LocalDateTime now = LocalDateTime.now();
		user.setCreatedAt(now);
		user.setUpdatedAt(now);
		authUserMapper.insert(user);

		String token = jwtService.issueToken(user);
		return new TokenResponse(token, "Bearer");
	}

	public AuthUserProfileResponse getCurrentUserProfile() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || !(authentication.getPrincipal() instanceof UserPrincipal principal)) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
		}
		AuthUserEntity user = authUserMapper.selectById(principal.userId());
		if (user == null) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
		}
		return new AuthUserProfileResponse(user.getId(), user.getUsername(), user.getRole(), user.getStatus());
	}
}
