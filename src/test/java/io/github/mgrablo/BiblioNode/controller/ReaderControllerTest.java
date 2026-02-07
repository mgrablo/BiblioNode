package io.github.mgrablo.BiblioNode.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import io.github.mgrablo.BiblioNode.dto.ReaderRequest;
import io.github.mgrablo.BiblioNode.dto.ReaderResponse;
import io.github.mgrablo.BiblioNode.exception.DataIntegrityException;
import io.github.mgrablo.BiblioNode.exception.ResourceNotFoundException;
import io.github.mgrablo.BiblioNode.service.ReaderService;
import tools.jackson.databind.ObjectMapper;

@WebMvcTest(ReaderController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ReaderControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private ReaderService readerService;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	public void createReader_ShouldReturnCreated_WhenValidRequest() throws Exception {
		ReaderRequest request = new ReaderRequest("Test Name", "test@email.com");
		ReaderResponse response = new ReaderResponse(1L, "Test Name", "test@email.com", List.of());

		when(readerService.createReader(any(ReaderRequest.class))).thenReturn(response);

		mockMvc.perform(post("/api/readers")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request))
				).andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").value(1L))
				.andExpect(jsonPath("$.fullName").value("Test Name"))
				.andExpect(jsonPath("$.email").value("test@email.com"));

		verify(readerService, times(1)).createReader(any());
	}

	@Test
	public void createReader_ShouldReturnBadRequest_WhenNameIsEmpty() throws Exception {
		ReaderRequest request = new ReaderRequest("", "test@email.com");

		mockMvc.perform(post("/api/readers")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
		).andExpect(status().isBadRequest());
	}

	@Test
	public void createReader_ShouldReturnBadRequest_WhenEmailIsInvalid() throws Exception {
		ReaderRequest request = new ReaderRequest("", "bademail");

		mockMvc.perform(post("/api/readers")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
		).andExpect(status().isBadRequest());
	}

	@Test
	public void createReader_ShouldReturnConflict_WhenEmailAlreadyInUse() throws Exception {
		ReaderRequest request = new ReaderRequest("Test Name", "used@email.com");

		when(readerService.createReader(any(ReaderRequest.class))).thenThrow(DataIntegrityException.class);

		mockMvc.perform(post("/api/readers")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
		).andExpect(status().isConflict());
	}

	@Test
	public void getReaderById_ShouldReturnResponse_WhenReaderExists() throws Exception {
		ReaderResponse response = new ReaderResponse(1L, "Test Name", "test@email.com", List.of());

		when(readerService.getReaderById(any(Long.class))).thenReturn(response);

		mockMvc.perform(get("/api/readers/1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1L))
				.andExpect(jsonPath("$.fullName").value("Test Name"))
				.andExpect(jsonPath("$.email").value("test@email.com"))
				.andExpect(jsonPath("$.loans").isArray())
				.andExpect(jsonPath("$.loans.length()").value(0));
	}

	@Test
	public void getReaderById_ShouldReturnNotFound_WhenReaderDoesNotExist() throws Exception {
		when(readerService.getReaderById(any(Long.class))).thenThrow(ResourceNotFoundException.class);

		mockMvc.perform(get("/api/readers/1"))
				.andExpect(status().isNotFound());
	}


	@Test
	public void getReaderByEmail_ShouldReturnResponse_WhenReaderExists() throws Exception {
		ReaderResponse response = new ReaderResponse(1L, "Test Name", "test@email.com", List.of());

		when(readerService.getReaderByEmail(any(String.class))).thenReturn(response);

		mockMvc.perform(get("/api/readers/email")
						.param("email", "test@email.com")
				).andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1L))
				.andExpect(jsonPath("$.fullName").value("Test Name"))
				.andExpect(jsonPath("$.email").value("test@email.com"))
				.andExpect(jsonPath("$.loans").isArray())
				.andExpect(jsonPath("$.loans.length()").value(0));
	}

	@Test
	public void getReaderByEmail_ShouldReturnNotFound_WhenReaderDoesNotExist() throws Exception {
		when(readerService.getReaderByEmail(any(String.class))).thenThrow(ResourceNotFoundException.class);

		mockMvc.perform(get("/api/readers/email")
						.param("email", "notFound@email.com"))
				.andExpect(status().isNotFound());
	}

	@Test
	public void getAll_ShouldReturnPageOfResponse_WhenReadersExist() throws Exception {
		ReaderResponse response1 = new ReaderResponse(1L, "Test Name1", "1test@email.com", List.of());
		ReaderResponse response2 = new ReaderResponse(2L, "Test Name2", "2test@email.com", List.of());
		Page<ReaderResponse> readerResponsePage = new PageImpl<>(List.of(response1, response2));

		when(readerService.getAll(any(Pageable.class))).thenReturn(readerResponsePage);

		mockMvc.perform(get("/api/readers/"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").isMap())
				.andExpect(jsonPath("$.content").isArray())
				.andExpect(jsonPath("$.totalElements").value(2))
				.andExpect(jsonPath("$.content[0].id").value(1L))
				.andExpect(jsonPath("$.content[0].fullName").value("Test Name1"))
				.andExpect(jsonPath("$.content[1].email").value("2test@email.com"))
				.andExpect(jsonPath("$.content[1].loans").isArray())
				.andExpect(jsonPath("$.content[1].loans.length()").value(0));
	}

	@Test
	public void getAll_ShouldReturnEmptyPage_WhenReadersDoNotExist() throws Exception {
		when(readerService.getAll(any(Pageable.class))).thenReturn(Page.empty());

		mockMvc.perform(get("/api/readers/"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").isMap())
				.andExpect(jsonPath("$.content").isArray())
				.andExpect(jsonPath("$.totalElements").value(0));
	}

	@Test
	public void updateReader_ShouldReturnOk_WhenRequestIsValidAndReaderExists() throws Exception {
		ReaderRequest request = new ReaderRequest("New Name", "new@email.com");
		ReaderResponse response = new ReaderResponse(1L, "New Name", "new@email.com", List.of());
		when(readerService.updateReader(1L, request)).thenReturn(response);

		mockMvc.perform(put("/api/readers/1")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
		).andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1L))
				.andExpect(jsonPath("$.fullName").value("New Name"))
				.andExpect(jsonPath("$.email").value("new@email.com"))
				.andExpect(jsonPath("$.loans").isArray());
	}

	@Test
	public void updateReader_ShouldReturnConflict_WhenEmailIsAlreadyInUseByAnotherReader() throws Exception {
		ReaderRequest request = new ReaderRequest("New Name", "used@email.com");

		when(readerService.updateReader(any(Long.class), any(ReaderRequest.class))).thenThrow(DataIntegrityException.class);

		mockMvc.perform(put("/api/readers/1")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
		).andExpect(status().isConflict());
	}

	@Test
	public void updateReader_ShouldReturnBadRequest_WhenNameIsBlank() throws Exception {
		ReaderRequest request = new ReaderRequest("", "new@email.com");

		mockMvc.perform(put("/api/readers/1")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
		).andExpect(status().isBadRequest());
	}

	@Test
	public void updateReader_ShouldReturnBadRequest_WhenEmailIsInvalid() throws Exception {
		ReaderRequest request = new ReaderRequest("New Name", "newemail");

		mockMvc.perform(put("/api/readers/1")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
		).andExpect(status().isBadRequest());
	}

	@Test
	public void updateReader_ShouldReturnNotFound_WhenReaderDoesNotExist() throws Exception {
		ReaderRequest request = new ReaderRequest("New Name", "new@email.com");

		when(readerService.updateReader(any(Long.class), any(ReaderRequest.class))).thenThrow(ResourceNotFoundException.class);

		mockMvc.perform(put("/api/readers/9")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
		).andExpect(status().isNotFound());
	}

	@Test
	public void deleteReader_ShouldReturnNoContent_WhenReaderExists() throws Exception {
		doNothing().when(readerService).deleteReader(any(Long.class));

		mockMvc.perform(delete("/api/readers/1"))
				.andExpect(status().isNoContent());
	}

	@Test
	public void deleteReader_ShouldReturnNotFound_WhenReaderDoesNotExist() throws Exception {
		doThrow(ResourceNotFoundException.class).when(readerService).deleteReader(any(Long.class));

		mockMvc.perform(delete("/api/readers/1"))
				.andExpect(status().isNotFound());
	}
}
