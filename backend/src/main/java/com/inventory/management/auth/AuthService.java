package com.inventory.management.auth;

import java.util.Set;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuthService {

	private final UserRepository appUserRepository;
	private final AuthenticationManager authenticationManager;
	private final JwtService jwtService;
	private final PasswordEncoder passwordEncoder;
	private final UserDetailsService userDetailsService;

	public AuthService(
		UserRepository appUserRepository,
		AuthenticationManager authenticationManager,
		JwtService jwtService,
		PasswordEncoder passwordEncoder,
		UserDetailsService userDetailsService
	) {
		this.appUserRepository = appUserRepository;
		this.authenticationManager = authenticationManager;
		this.jwtService = jwtService;
		this.passwordEncoder = passwordEncoder;
		this.userDetailsService = userDetailsService;
	}

	public AuthResponse register(RegisterRequest request) {
		if (appUserRepository.existsByUsername(request.username())) {
			throw new UsernameAlreadyExistsException(request.username());
		}
		if (appUserRepository.existsByEmail(request.email())) {
			throw new EmailAlreadyExistsException(request.email());
		}

		User appUser = new User();
		appUser.setUsername(request.username());
		appUser.setEmail(request.email());
		appUser.setPassword(passwordEncoder.encode(request.password()));
		appUser.setRoles(Set.of("USER"));
		appUserRepository.save(appUser);

		UserDetails userDetails = userDetailsService.loadUserByUsername(request.username());
		return tokenResponse(userDetails, Set.of("USER"));
	}

	public AuthResponse login(AuthRequest request) {
		authenticationManager.authenticate(
			new UsernamePasswordAuthenticationToken(request.username(), request.password())
		);
		UserDetails userDetails = userDetailsService.loadUserByUsername(request.username());
		Set<String> roles = appUserRepository.findByUsername(request.username())
			.map(User::getRoles)
			.orElse(Set.of());
		return tokenResponse(userDetails, roles);
	}

	private AuthResponse tokenResponse(UserDetails userDetails, Set<String> roles) {
		return new AuthResponse(
			"Bearer",
			jwtService.generateToken(userDetails),
			userDetails.getUsername(),
			appUserRepository.findByUsername(userDetails.getUsername()).map(User::getEmail).orElse(null),
			roles,
			jwtService.getExpirationSeconds()
		);
	}

}
