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

import io.github.mgrablo.BiblioNode.dto.ReaderRequest;
import io.github.mgrablo.BiblioNode.dto.ReaderResponse;
import io.github.mgrablo.BiblioNode.dto.RegisterRequest;
import io.github.mgrablo.BiblioNode.exception.DataIntegrityException;
import io.github.mgrablo.BiblioNode.model.User;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {
	@Mock
	private UserService userService;
	@Mock
	private ReaderService readerService;
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
}