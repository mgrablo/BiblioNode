package io.github.mgrablo.BiblioNode.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
import io.github.mgrablo.BiblioNode.exception.DataIntegrityException;
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

	@Test
	void updateAuthor_ShouldReturnAuthor_WhenUpdatedSuccessfuly() throws Exception {
		Long id = 1L;
		AuthorRequest request = new AuthorRequest("NewName", "Bio");
		AuthorResponse response = new AuthorResponse(id, "NewName", "Bio", null);

		when(authorService.updateAuthor(id, request)).thenReturn(response);

		mockMvc.perform(put("/api/authors/1")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request))
				).andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(id))
				.andExpect(jsonPath("$.name").value("NewName"))
				.andExpect(jsonPath("$.biography").value("Bio"));
	}

	@Test
	void updateAuthor_ShouldReturnBadRequest_WhenNameIsEmpty() throws Exception {
		AuthorRequest request = new AuthorRequest("", "Bio");
		mockMvc.perform(put("/api/authors/1")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request))
				).andExpect(status().isBadRequest());

		verify(authorService, never()).updateAuthor(any(), any());
	}

	@Test
	void updateAuthor_ShouldReturnNotFound_WhenAuthorDoesNotExist() throws Exception {
		Long id = 9L;
		AuthorRequest request = new AuthorRequest("NewName", "Bio");
		when(authorService.updateAuthor(id, request)).thenThrow(new ResourceNotFoundException("Not Found"));

		mockMvc.perform(put("/api/authors/9")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
		).andExpect(status().isNotFound());
	}

	@Test
	void deleteAuthor_ShouldReturnNoContent_WhenAuthorExist() throws Exception {
		Long id = 1L;
		doNothing().when(authorService).deleteAuthor(id);

		mockMvc.perform(delete("/api/authors/1"))
				.andExpect(status().isNoContent());

		verify(authorService, times(1)).deleteAuthor(id);
	}

	@Test
	void deleteAuthor_ShouldReturnNotFound_WhenAuthorDoesNotExist() throws Exception {
		Long id = 9L;
		doThrow(new ResourceNotFoundException("Not Found")).when(authorService).deleteAuthor(id);

		mockMvc.perform(delete("/api/authors/9"))
				.andExpect(status().isNotFound());

		verify(authorService, times(1)).deleteAuthor(id);
	}

	@Test
	void deleteAuthor_ShouldReturnConflict_WhenAuthorHasBooks() throws Exception {
		Long id = 1L;
		doThrow(new DataIntegrityException("Cannot delete author with assigned books"))
				.when(authorService).deleteAuthor(id);

		mockMvc.perform(delete("/api/authors/1"))
				.andExpect(status().isConflict());

		verify(authorService, times(1)).deleteAuthor(id);
	}
}
