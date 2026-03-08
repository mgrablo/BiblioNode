package io.github.mgrablo.BiblioNode.bootstrap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import io.github.mgrablo.BiblioNode.model.Role;
import io.github.mgrablo.BiblioNode.model.RoleName;
import io.github.mgrablo.BiblioNode.model.User;
import io.github.mgrablo.BiblioNode.repository.RoleRepository;
import io.github.mgrablo.BiblioNode.repository.UserRepository;
import io.github.mgrablo.BiblioNode.service.UserService;
import lombok.RequiredArgsConstructor;

@Component
@Order(1)
@RequiredArgsConstructor
public class BaseDataInitializer implements CommandLineRunner {
	private final UserService userService;
	private final UserRepository userRepository;
	private final RoleRepository roleRepository;

	@Value("${app.initial-admin.email}")
	private String adminEmail;

	@Value("${app.initial-admin.password}")
	private String adminPassword;

	@Override
	public void run(String... args) throws Exception {
		Role adminRole = roleRepository.findByName(RoleName.ROLE_ADMIN)
				.orElseGet(() -> roleRepository.save(new Role(1L, RoleName.ROLE_ADMIN)));

		roleRepository.findByName(RoleName.ROLE_READER)
				.orElseGet(() -> roleRepository.save(new Role(2L, RoleName.ROLE_READER)));

		boolean adminExists = userRepository.existsByRolesName(RoleName.ROLE_ADMIN);

		if (!adminExists) {
			User admin = userService.createAccount(adminEmail, adminPassword);
			admin.getRoles().add(adminRole);
			userRepository.save(admin);
		}
	}
}
