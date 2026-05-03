package com.bds.auth.security;

import com.bds.auth.service.JwtService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {
	private final JwtService jwtService;

	public JwtAuthFilter(JwtService jwtService) {
		this.jwtService = jwtService;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		String token = resolveToken(request);
		if (token != null && SecurityContextHolder.getContext().getAuthentication() == null) {
			Claims claims = jwtService.parseToken(token);
			if (claims != null) {
				String userId = claims.getSubject();
				String username = claims.get("username", String.class);
				String role = claims.get("role", String.class);
				String normalizedRole = role == null ? "USER" : role.toUpperCase();
				UserPrincipal principal = new UserPrincipal(userId, username, normalizedRole);
				List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + normalizedRole));
				UsernamePasswordAuthenticationToken authentication =
						new UsernamePasswordAuthenticationToken(principal, null, authorities);
				SecurityContextHolder.getContext().setAuthentication(authentication);
			}
		}
		filterChain.doFilter(request, response);
	}

	private String resolveToken(HttpServletRequest request) {
		String header = request.getHeader("Authorization");
		if (header == null) {
			return null;
		}
		String prefix = "Bearer ";
		if (header.startsWith(prefix)) {
			return header.substring(prefix.length()).trim();
		}
		return null;
	}
}

