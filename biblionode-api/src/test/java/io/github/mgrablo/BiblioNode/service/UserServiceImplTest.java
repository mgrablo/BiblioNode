package io.github.mgrablo.BiblioNode.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import io.github.mgrablo.BiblioNode.dto.PasswordChangeRequest;
import io.github.mgrablo.BiblioNode.exception.DataIntegrityException;
import io.github.mgrablo.BiblioNode.exception.ResourceNotFoundException;
import io.github.mgrablo.BiblioNode.model.Role;
import io.github.mgrablo.BiblioNode.model.RoleName;
import io.github.mgrablo.BiblioNode.model.User;
import io.github.mgrablo.BiblioNode.repository.RoleRepository;
import io.github.mgrablo.BiblioNode.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
	@Mock
	private UserRepository userRepository;
	@Mock
	private RoleRepository roleRepository;
	@Mock
	private PasswordEncoder passwordEncoder;

	@InjectMocks
	private UserServiceImpl userService;

	@Test
	void createAccount_ShouldSaveUserWithEncodedPasswordAndRole() {
		String email = "test@email.com";
		String rawPassword = "rawPassword123";
		String encodedPassword = "encodedPassword123";

		Role role = new Role(1L, RoleName.ROLE_READER);

		when(userRepository.existsByEmail(email)).thenReturn(false);
		when(roleRepository.findByName(RoleName.ROLE_READER)).thenReturn(Optional.of(role));
		when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);

		when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
			User user = invocation.getArgument(0);
			user.setId(1L);
			return user;
		});

		User savedUser = userService.createAccount(email, rawPassword);
		assertNotNull(savedUser.getId());
		assertEquals(encodedPassword, savedUser.getPassword());
		assertTrue(savedUser.getRoles().contains(role));

		verify(userRepository).save(any(User.class));
		verify(passwordEncoder).encode(rawPassword);
	}

	@Test
	void createAccount_ShouldThrowException_WhenEmailAlreadyExists() {
		String email = "exists@email.com";

		when(userRepository.existsByEmail(email)).thenReturn(true);

		assertThrows(DataIntegrityException.class, () -> userService.createAccount(email, "anyPassword"));

		verify(userRepository, never()).save(any());
	}

	@Test
	void createAccount_ShouldThrowException_WhenRoleNotFound() {
		String email = "test@email.com";

		when(userRepository.existsByEmail(email)).thenReturn(false);
		when(roleRepository.findByName(RoleName.ROLE_READER)).thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class, () -> userService.createAccount(email, "anyPassword"));
		verify(userRepository, never()).save(any());
	}

	@Test
	void updateEmail_ShouldUpdateEmail_WhenUserExistsEmailNotInUse() {
		Long userId = 1L;
		String newEmail = "new@email.com";
		User user = createTestUser(userId, "old@email.com", "password");

		when(userRepository.existsByEmail(newEmail)).thenReturn(false);
		when(userRepository.findById(userId)).thenReturn(Optional.of(user));

		userService.updateEmail(userId, newEmail);
		assertEquals(newEmail, user.getEmail());
	}

	@Test
	void updateEmail_ShouldThrowException_WhenNewEmailAlreadyExists() {
		Long userId = 1L;
		String newEmail = "exists@email.com";

		when(userRepository.existsByEmail(newEmail)).thenReturn(true);

		assertThrows(DataIntegrityException.class, () -> userService.updateEmail(userId, newEmail));
		verify(userRepository, never()).findById(any());
	}

	@Test
	void updateEmail_ShouldThrowException_WhenUserNotFound() {
		Long userId = 1L;
		String newEmail = "new@email.com";

		when(userRepository.existsByEmail(newEmail)).thenReturn(false);
		when(userRepository.findById(userId)).thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class, () -> userService.updateEmail(userId, newEmail));
	}

	@Test
	void updatePassword_ShouldUpdatePassword_WhenCurrentPasswordIsCorrect() {
		Long userId = 1L;
		String currentPassword = "currentPassword";
		String newPassword = "newPassword";
		String encodedCurrentPassword = "encodedCurrentPassword";
		PasswordChangeRequest request = new PasswordChangeRequest(currentPassword, newPassword);
		User user = createTestUser(userId, "test@email.com", encodedCurrentPassword);

		when(userRepository.findById(userId)).thenReturn(Optional.of(user));
		when(passwordEncoder.matches(currentPassword, encodedCurrentPassword)).thenReturn(true);
		when(passwordEncoder.encode(newPassword)).thenReturn("encodedNewPassword");

		userService.updatePassword(userId, request);

		assertEquals("encodedNewPassword", user.getPassword());
		verify(passwordEncoder).matches(currentPassword, encodedCurrentPassword);
		verify(passwordEncoder).encode(newPassword);
	}

	@Test
	void updatePassword_ShouldThrowException_WhenCurrentPasswordIsIncorrect() {
		Long userId = 1L;
		String currentPassword = "wrongCurrentPassword";
		String newPassword = "newPassword";
		String encodedCurrentPassword = "encodedCurrentPassword";
		PasswordChangeRequest request = new PasswordChangeRequest(currentPassword, newPassword);
		User user = createTestUser(userId, "test@email.com", encodedCurrentPassword);

		when(userRepository.findById(userId)).thenReturn(Optional.of(user));
		when(passwordEncoder.matches(currentPassword, encodedCurrentPassword)).thenReturn(false);

		assertThrows(BadCredentialsException.class, () ->
				userService.updatePassword(userId, request)
		);
		verify(passwordEncoder).matches(currentPassword, encodedCurrentPassword);
		verify(passwordEncoder, never()).encode(any());
	}

	@Test
	void updatePassword_ShouldThrowException_WhenUserNotFound() {
		Long userId = 1L;
		String currentPassword = "currentPassword";
		String newPassword = "newPassword";
		PasswordChangeRequest request = new PasswordChangeRequest(currentPassword, newPassword);

		when(userRepository.findById(userId)).thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class, () ->
				userService.updatePassword(userId, request)
		);
		verify(passwordEncoder, never()).matches(any(), any());
		verify(passwordEncoder, never()).encode(any());
	}

	private User createTestUser(Long id, String email, String password) {
		User user = new User();
		user.setId(1L);
		user.setEmail(email);
		user.setPassword(password);
		return user;
	}
}