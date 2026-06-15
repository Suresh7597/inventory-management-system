package com.inventory.management.auth;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class JwtService {

	private static final String HMAC_ALGORITHM = "HmacSHA256";

	private final ObjectMapper objectMapper;
	private final byte[] secret;
	private final long expirationSeconds;

	public JwtService(
		ObjectMapper objectMapper,
		@Value("${app.jwt.secret}") String secret,
		@Value("${app.jwt.expiration-seconds}") long expirationSeconds
	) {
		this.objectMapper = objectMapper;
		this.secret = secret.getBytes(StandardCharsets.UTF_8);
		this.expirationSeconds = expirationSeconds;
	}

	public String generateToken(UserDetails userDetails) {
		Instant now = Instant.now();
		Map<String, Object> header = Map.of("alg", "HS256", "typ", "JWT");
		Map<String, Object> payload = new LinkedHashMap<>();
		payload.put("sub", userDetails.getUsername());
		payload.put("roles", toRoles(userDetails.getAuthorities()));
		payload.put("iat", now.getEpochSecond());
		payload.put("exp", now.plusSeconds(expirationSeconds).getEpochSecond());

		String unsignedToken = encodeJson(header) + "." + encodeJson(payload);
		return unsignedToken + "." + sign(unsignedToken);
	}

	public boolean isTokenValid(String token, UserDetails userDetails) {
		String username = extractUsername(token);
		return username.equals(userDetails.getUsername()) && !isExpired(token) && hasValidSignature(token);
	}

	public String extractUsername(String token) {
		return payload(token).get("sub").toString();
	}

	public long getExpirationSeconds() {
		return expirationSeconds;
	}

	private boolean isExpired(String token) {
		Object expiration = payload(token).get("exp");
		return Instant.now().getEpochSecond() >= Long.parseLong(expiration.toString());
	}

	private boolean hasValidSignature(String token) {
		String[] parts = token.split("\\.");
		if (parts.length != 3) {
			return false;
		}
		return sign(parts[0] + "." + parts[1]).equals(parts[2]);
	}

	private Map<String, Object> payload(String token) {
		try {
			String[] parts = token.split("\\.");
			if (parts.length != 3) {
				throw new IllegalArgumentException("Invalid JWT format");
			}
			byte[] json = Base64.getUrlDecoder().decode(parts[1]);
			return objectMapper.readValue(json, new TypeReference<>() {
			});
		} catch (Exception exception) {
			throw new IllegalArgumentException("Invalid JWT token", exception);
		}
	}

	private String encodeJson(Map<String, Object> value) {
		try {
			return Base64.getUrlEncoder().withoutPadding()
				.encodeToString(objectMapper.writeValueAsBytes(value));
		} catch (Exception exception) {
			throw new IllegalStateException("Unable to create JWT", exception);
		}
	}

	private String sign(String value) {
		try {
			Mac mac = Mac.getInstance(HMAC_ALGORITHM);
			mac.init(new SecretKeySpec(secret, HMAC_ALGORITHM));
			return Base64.getUrlEncoder().withoutPadding()
				.encodeToString(mac.doFinal(value.getBytes(StandardCharsets.UTF_8)));
		} catch (Exception exception) {
			throw new IllegalStateException("Unable to sign JWT", exception);
		}
	}

	private List<String> toRoles(Collection<? extends GrantedAuthority> authorities) {
		return authorities.stream()
			.map(GrantedAuthority::getAuthority)
			.map(authority -> authority.replaceFirst("^ROLE_", ""))
			.toList();
	}

}
