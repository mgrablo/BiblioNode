package io.github.mgrablo.BiblioNode.controller;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import io.github.mgrablo.BiblioNode.dto.ReaderResponse;
import io.github.mgrablo.BiblioNode.dto.RegisterRequest;
import io.github.mgrablo.BiblioNode.exception.DataIntegrityException;
import io.github.mgrablo.BiblioNode.service.AuthService;
import tools.jackson.databind.ObjectMapper;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private AuthService authService;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void register_ShouldReturnCreated_WhenValidRequest() throws Exception {
		RegisterRequest request = new RegisterRequest("jankowalski@email.com", "password123", "Jan Kowalski");
		ReaderResponse response = new ReaderResponse(1L, "Jan Kowalski", "jankowalski@email.com", null);

		when(authService.register(any(RegisterRequest.class))).thenReturn(response);

		mockMvc.perform(post("/api/auth/register")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request))
				).andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").value(1L))
				.andExpect(jsonPath("$.email").value("jankowalski@email.com"));

		verify(authService).register(any(RegisterRequest.class));
	}

	@Test
	void register_ShouldReturnBadRequest_WhenEmailIsInvalid() throws Exception {
		RegisterRequest request = new RegisterRequest("invalid-email", "password123", "Jan Kowalski");

		mockMvc.perform(post("/api/auth/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
		).andExpect(status().isBadRequest());

		verify(authService, never()).register(any(RegisterRequest.class));
	}

	@Test
	void register_ShouldReturnBadRequest_WhenPasswordIsTooShort() throws Exception {
		RegisterRequest request = new RegisterRequest("jankowalski@email.com", "short", "Jan Kowalski");

		mockMvc.perform(post("/api/auth/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
		).andExpect(status().isBadRequest());

		verify(authService, never()).register(any(RegisterRequest.class));
	}

	@Test
	void register_ShouldReturnBadRequest_WhenNameIsEmpty() throws Exception {
		RegisterRequest request = new RegisterRequest("jankowalski@email.com", "password123", "");

		mockMvc.perform(post("/api/auth/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
		).andExpect(status().isBadRequest());

		verify(authService, never()).register(any(RegisterRequest.class));
	}

	@Test
	void register_ShouldReturnConflict_WhenEmailAlreadyExists() throws Exception {
		RegisterRequest request = new RegisterRequest("exists@email.com", "password123", "Existing User");

		when(authService.register(any(RegisterRequest.class))).thenThrow(DataIntegrityException.class);

		mockMvc.perform(post("/api/auth/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
		).andExpect(status().isConflict());
	}
}