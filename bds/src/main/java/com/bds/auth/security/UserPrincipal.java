package com.bds.auth.security;

public record UserPrincipal(String userId, String username, String role) {
}

