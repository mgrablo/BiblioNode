package io.github.mgrablo.BiblioNode.service;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.mgrablo.BiblioNode.dto.PasswordChangeRequest;
import io.github.mgrablo.BiblioNode.exception.DataIntegrityException;
import io.github.mgrablo.BiblioNode.exception.ResourceNotFoundException;
import io.github.mgrablo.BiblioNode.model.Role;
import io.github.mgrablo.BiblioNode.model.RoleName;
import io.github.mgrablo.BiblioNode.model.User;
import io.github.mgrablo.BiblioNode.repository.RoleRepository;
import io.github.mgrablo.BiblioNode.repository.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final PasswordEncoder passwordEncoder;

	@Override
	public User createAccount(String email, String password) {
		if (userRepository.existsByEmail(email)) {
			throw new DataIntegrityException("Email is already in use");
		}

		User user = new User();
		user.setEmail(email);
		user.setPassword(passwordEncoder.encode(password));

		Role readerRode = roleRepository.findByName(RoleName.ROLE_READER)
				.orElseThrow(() -> new ResourceNotFoundException("Role not found: " + RoleName.ROLE_READER));

		user.getRoles().add(readerRode);

		return userRepository.save(user);
	}

	@Override
	public void updateEmail(Long userId, String newEmail) {
		if (userRepository.existsByEmail(newEmail)) {
			throw new DataIntegrityException("New email already in use");
		}

		User user = userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User not found for id: " + userId));

		user.setEmail(newEmail);
	}

	@Override
	public void updatePassword(Long userId, PasswordChangeRequest request) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User not found for id: " + userId));

		if (!passwordEncoder.matches(request.currentPassword(), user.getPassword())) {
			throw new BadCredentialsException("Old password is incorrect");
		}

		user.setPassword(passwordEncoder.encode(request.newPassword()));
	}
}
