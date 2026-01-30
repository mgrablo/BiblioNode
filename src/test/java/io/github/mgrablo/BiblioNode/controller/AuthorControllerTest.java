package io.github.mgrablo.BiblioNode.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

import java.util.Collections;
import java.util.List;

import io.github.mgrablo.BiblioNode.dto.AuthorRequest;
import io.github.mgrablo.BiblioNode.dto.AuthorResponse;
import io.github.mgrablo.BiblioNode.exception.ResourceNotFoundException;
import io.github.mgrablo.BiblioNode.service.AuthorService;
import tools.jackson.databind.ObjectMapper;

@WebMvcTest(AuthorController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AuthorControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private AuthorService authorService;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void addAuthor_ShouldReturnBadRequest_WhenNameIsEmpty() throws Exception {
		AuthorRequest invalidRequest = new AuthorRequest("", "Bio");

		mockMvc.perform(post("/api/authors")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(invalidRequest))
		).andExpect(status().isBadRequest());

		verify(authorService, never()).saveAuthor(any());
	}

	@Test
	void addAuthor_ShouldReturnCreated_WhenAuthorDoesNotExist() throws Exception {
		AuthorRequest request = new AuthorRequest("AAA", "Bio");

		mockMvc.perform(post("/api/authors")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
		).andExpect(status().isCreated());

		verify(authorService, times(1)).saveAuthor(any());
	}

	@Test
	void addAuthor_ShouldReturnBadRequest_WhenNameTooLong() throws Exception {
		StringBuilder builder = new StringBuilder();
		builder.repeat("A", 1000);
		String name = builder.toString();
		AuthorRequest invalidRequest = new AuthorRequest(name, "Bio");

		mockMvc.perform(post("/api/authors")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(invalidRequest))
		).andExpect(status().isBadRequest());

		verify(authorService, never()).saveAuthor(any());
	}

	@Test
	void getAuthor_ShouldReturnAuthor_WhenExists() throws Exception {
		AuthorResponse response = new AuthorResponse(1L, "AAA", "Bio", null);
		when(authorService.findById(1L)).thenReturn(response);

		mockMvc.perform(get("/api/authors/1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1))
				.andExpect(jsonPath("$.name").value("AAA"))
				.andExpect(jsonPath("$.biography").value("Bio"));
	}

	@Test
	void getAuthor_ShouldReturnNotFound_WhenAuthorDoesNotExist() throws Exception {
		when(authorService.findById(99L)).thenThrow(new ResourceNotFoundException("Not found"));

		mockMvc.perform(get("/api/authors/99"))
				.andExpect(status().isNotFound());
	}

	@Test
	void getAll_ShouldReturnList_WhenAuthorsExist() throws Exception {
		AuthorResponse response = new AuthorResponse(1L, "AAA", "Bio", null);
		when(authorService.getAll()).thenReturn(List.of(response));

		mockMvc.perform(get("/api/authors"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").isArray())
				.andExpect(jsonPath("$.length()").value(1))
				.andExpect(jsonPath("$[0].id").value(1L))
				.andExpect(jsonPath("$[0].name").value("AAA"));
	}

	@Test
	void getAll_ShouldReturnEmptyList_WhenAuthorsDoNotExist() throws Exception {
		when(authorService.getAll()).thenReturn(Collections.emptyList());

		mockMvc.perform(get("/api/authors"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").isArray())
				.andExpect(jsonPath("$.length()").value(0));
	}

	@Test
	void searchByName_ShouldReturnAuthor_WhenExists() throws Exception {
		String name = "AAA";
		AuthorResponse response = new AuthorResponse(1L, "AAA", "Bio", null);

		when(authorService.findByName(name)).thenReturn(response);

		mockMvc.perform(get("/api/authors/search")
						.param("name", "AAA"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1L))
				.andExpect(jsonPath("$.name").value("AAA"))
				.andExpect(jsonPath("$.biography").value("Bio"));
	}

	@Test
	void searchByName_ShouldReturnNotFound_WhenAuthorDoesNotExist() throws Exception {
		when(authorService.findByName("AAA")).thenThrow(new ResourceNotFoundException("Not found"));

		mockMvc.perform(get("/api/authors/search")
						.param("name", "AAA"))
				.andExpect(status().isNotFound());
	}
}
