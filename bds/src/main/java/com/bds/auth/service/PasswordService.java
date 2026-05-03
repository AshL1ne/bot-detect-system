package com.bds.auth.service;

import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

@Service
public class PasswordService {
	public record HashResult(String salt, String hash) {
	}

	public HashResult hash(String rawPassword) {
		String salt = BCrypt.gensalt(12);
		String hash = BCrypt.hashpw(rawPassword, salt);
		return new HashResult(salt, hash);
	}

	public boolean matches(String rawPassword, String salt, String expectedHash) {
		if (salt == null || expectedHash == null) {
			return false;
		}
		String actualHash = BCrypt.hashpw(rawPassword, salt);
		return expectedHash.equals(actualHash);
	}
}

