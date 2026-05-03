package com.bds.auth.controller;

import com.bds.auth.dto.AuthUserProfileResponse;
import com.bds.auth.dto.LoginRequest;
import com.bds.auth.dto.RegisterRequest;
import com.bds.auth.dto.TokenResponse;
import com.bds.auth.service.AuthService;
import com.bds.common.Result;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
	private final AuthService authService;

	public AuthController(AuthService authService) {
		this.authService = authService;
	}

	@PostMapping("/login")
	public Result<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
		return Result.success(authService.login(request));
	}

	@PostMapping("/register")
	public Result<TokenResponse> register(@Valid @RequestBody RegisterRequest request) {
		return Result.success(authService.register(request));
	}

	@GetMapping("/me")
	public Result<AuthUserProfileResponse> me() {
		return Result.success(authService.getCurrentUserProfile());
	}
}
