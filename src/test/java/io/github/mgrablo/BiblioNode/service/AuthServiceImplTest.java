package io.github.mgrablo.BiblioNode.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;

import java.util.Optional;
import java.util.Set;

import io.github.mgrablo.BiblioNode.dto.*;
import io.github.mgrablo.BiblioNode.exception.DataIntegrityException;
import io.github.mgrablo.BiblioNode.model.Role;
import io.github.mgrablo.BiblioNode.model.RoleName;
import io.github.mgrablo.BiblioNode.model.User;
import io.github.mgrablo.BiblioNode.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {
	@Mock
	private UserService userService;
	@Mock
	private UserRepository userRepository;
	@Mock
	private ReaderService readerService;
	@Mock
	private PasswordEncoder passwordEncoder;
	@Mock
	private JwtEncoder jwtEncoder;

	@InjectMocks
	private AuthServiceImpl authService;

	@Test
	void register_ShouldCoordinateUserAndProfileCreation() {
		RegisterRequest request = new RegisterRequest("test@email.com", "password", "Test User");
		User mockUser = new User();
		mockUser.setId(10L);
		ReaderResponse expectedResponse = new ReaderResponse(1L, "Test User", "test@email.com", null);

		when(userService.createAccount(request.email(), request.password())).thenReturn(mockUser);
		when(readerService.createProfile(any(ReaderRequest.class), eq(mockUser))).thenReturn(expectedResponse);

		ReaderResponse result = authService.register(request);

		verify(userService).createAccount("test@email.com", "password");

		ArgumentCaptor<ReaderRequest> readerRequestCaptor = ArgumentCaptor.forClass(ReaderRequest.class);
		verify(readerService).createProfile(readerRequestCaptor.capture(), eq(mockUser));

		ReaderRequest capturedReaderRequest = readerRequestCaptor.getValue();
		assertEquals("Test User", capturedReaderRequest.fullName());

		assertEquals(expectedResponse, result);
	}

	@Test
	void register_ShouldNotCreateProfile_WhenUserCreationFails() {
		RegisterRequest request = new RegisterRequest("test@email.com", "password", "Test User");

		when(userService.createAccount(any(), any())).thenThrow(new DataIntegrityException("Email already in use"));

		assertThrows(DataIntegrityException.class, () -> authService.register(request));

		verifyNoInteractions(readerService);
	}

	@Test
	void login_ShouldReturnResponse_WhenCredentialsAreValid() {
		String email = "test@email.com";
		String password = "password123";
		User user = createTestUser("test@email.com", "encodedPassword");
		LoginRequest request = new LoginRequest(email, password);

		when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
		when(passwordEncoder.matches(password, "encodedPassword")).thenReturn(true);

		Jwt mockJwt = mock(Jwt.class);
		when(mockJwt.getTokenValue()).thenReturn("mocked-jwt-token");
		when(jwtEncoder.encode(any(JwtEncoderParameters.class))).thenReturn(mockJwt);

		LoginResponse response = authService.login(request);

		assertNotNull(response);
		assertEquals("mocked-jwt-token", response.token());
		assertEquals(email, response.email());
		assertTrue(response.roles().contains("ROLE_READER"));
	}

	@Test
	void login_ShouldThrowException_WhenEmailNotFound() {
		String email = "test@email.com";
		String password = "password123";
		LoginRequest request = new LoginRequest(email, password);

		when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

		assertThrows(BadCredentialsException.class, () -> authService.login(request));
	}

	@Test
	void login_ShouldThrowException_WhenPasswordDoesNotMatch() {
		String email = "test@email.com";
		String password = "password123";
		User user = createTestUser(email, "encodedPassword");
		LoginRequest request = new LoginRequest(email, password);

		when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
		when(passwordEncoder.matches(password, "encodedPassword")).thenReturn(false);

		assertThrows(BadCredentialsException.class, () -> authService.login(request));
	}

	private User createTestUser(String email, String password) {
		User user = new User();
		user.setEmail(email);
		user.setPassword(password);
		Role role = new Role();
		role.setName(RoleName.ROLE_READER);
		user.setRoles(Set.of(role));
		return user;
	}
}