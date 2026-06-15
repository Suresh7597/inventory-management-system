package com.inventory.management.auth;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class DatabaseUserDetailsService implements UserDetailsService {

	private final UserRepository appUserRepository;

	public DatabaseUserDetailsService(UserRepository userRepository) {
		this.appUserRepository = userRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User appUser = appUserRepository.findByUsername(username)
			.orElseThrow(() -> new UsernameNotFoundException("User " + username + " was not found"));

		return org.springframework.security.core.userdetails.User.builder()
			.username(appUser.getUsername())
			.password(appUser.getPassword())
			.authorities(appUser.getRoles().stream().map(role -> "ROLE_" + role).toArray(String[]::new))
			.build();
	}

}
