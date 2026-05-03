package com.bds.auth.service;

import com.bds.auth.entity.AuthUserEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Date;

@Service
public class JwtService {
	private final String secret;
	private final long expirationSeconds;
	private final String issuer;

	public JwtService(
			@Value("${jwt.secret}") String secret,
			@Value("${jwt.expiration-seconds}") long expirationSeconds,
			@Value("${jwt.issuer}") String issuer) {
		this.secret = secret;
		this.expirationSeconds = expirationSeconds;
		this.issuer = issuer;
	}

	public String issueToken(AuthUserEntity user) {
		Instant now = Instant.now();
		Instant exp = now.plusSeconds(expirationSeconds);
		return Jwts.builder()
				.issuer(issuer)
				.subject(user.getId())
				.claim("username", user.getUsername())
				.claim("role", user.getRole())
				.issuedAt(Date.from(now))
				.expiration(Date.from(exp))
				.signWith(signingKey())
				.compact();
	}

	public Claims parseToken(String token) {
		try {
			Jws<Claims> claimsJws = Jwts.parser()
					.verifyWith(signingKey())
					.build()
					.parseSignedClaims(token);
			return claimsJws.getPayload();
		} catch (JwtException ex) {
			return null;
		}
	}

	private SecretKey signingKey() {
		byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
		if (keyBytes.length < 32) {
			keyBytes = sha256(keyBytes);
		}
		return Keys.hmacShaKeyFor(keyBytes);
	}

	private byte[] sha256(byte[] input) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			return digest.digest(input);
		} catch (NoSuchAlgorithmException ex) {
			throw new IllegalStateException("SHA-256 not available", ex);
		}
	}
}

