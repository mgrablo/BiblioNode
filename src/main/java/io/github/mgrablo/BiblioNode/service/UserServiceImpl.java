package io.github.mgrablo.BiblioNode.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

	@Override
	public User createAccount(String email, String password) {
		if (userRepository.existsByEmail(email)) {
			throw new DataIntegrityException("Email is already in use");
		}

		User user = new User();
		user.setEmail(email);
		user.setPassword(password); // TODO: Encode the password before saving

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
}
