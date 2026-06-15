package com.inventory.management.auth;

import java.util.Set;

public record AuthResponse(
	String tokenType,
	String accessToken,
	String username,
	String email,
	Set<String> roles,
	long expiresInSeconds
) {
}
