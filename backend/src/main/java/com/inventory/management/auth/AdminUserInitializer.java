package com.inventory.management.auth;

import java.util.Set;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AdminUserInitializer {

	@Bean
	CommandLineRunner seedAdminUser(UserRepository appUserRepository, PasswordEncoder passwordEncoder) {
		return args -> {
			if (appUserRepository.existsByUsername("admin")) {
				return;
			}

			User admin = new User();
			admin.setUsername("admin");
			admin.setEmail("admin@inventory.local");
			admin.setPassword(passwordEncoder.encode("admin123"));
			admin.setRoles(Set.of("ADMIN", "USER"));
			appUserRepository.save(admin);
		};
	}

}
