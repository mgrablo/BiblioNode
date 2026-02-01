package io.github.mgrablo.BiblioNode.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import io.github.mgrablo.BiblioNode.dto.BookRequest;
import io.github.mgrablo.BiblioNode.dto.BookResponse;
import io.github.mgrablo.BiblioNode.exception.ResourceNotFoundException;
import io.github.mgrablo.BiblioNode.service.BookService;
import tools.jackson.databind.ObjectMapper;

@WebMvcTest(BookController.class)
@AutoConfigureMockMvc(addFilters = false)
public class BookControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private BookService bookService;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	void addBook_ShouldReturnNotFound_WhenAuthorDoesNotExist() throws Exception {
		Long authorId = 9L;
		BookRequest invalidRequest = new BookRequest("Title", "111", authorId);
		when(bookService.addBook(any(BookRequest.class))).thenThrow(new ResourceNotFoundException("Author not found"));

		mockMvc.perform(post("/api/books")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(invalidRequest))
		).andExpect(status().isNotFound());
	}

	@Test
	void addBook_ShouldReturnBadRequest_WhenTitleIsEmpty() throws Exception {
		BookRequest invalidRequest = new BookRequest("", "111", 1L);

		mockMvc.perform(post("/api/books")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(invalidRequest))
		).andExpect(status().isBadRequest());

		verify(bookService, never()).addBook(any());
	}

	@Test
	void addBook_ShouldReturnBadRequest_WhenISBNIsEmpty() throws Exception {
		BookRequest invalidRequest = new BookRequest("Title", "", 1L);

		mockMvc.perform(post("/api/books")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(invalidRequest))
		).andExpect(status().isBadRequest());

		verify(bookService, never()).addBook(any());
	}

	@Test
	void addBook_ShouldReturnBadRequest_WhenAuthorIdIsNull() throws Exception {
		BookRequest invalidRequest = new BookRequest("Title", "111", null);

		mockMvc.perform(post("/api/books")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(invalidRequest))
		).andExpect(status().isBadRequest());

		verify(bookService, never()).addBook(any());
	}

	@Test
	void addBook_ShouldReturnCreated_WhenValidRequest() throws Exception {
		BookRequest request = new BookRequest("Title", "111", 1L);

		mockMvc.perform(post("/api/books")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
		).andExpect(status().isCreated());

		verify(bookService, times(1)).addBook(any());
	}

	@Test
	void getAll_ShouldReturnEmptyList_WhenBooksDoNotExist() throws Exception {
		when(bookService.getAllBooks(any(Pageable.class))).thenReturn(Page.empty());

		mockMvc.perform(get("/api/books"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").isMap())
				.andExpect(jsonPath("$.content").isArray())
				.andExpect(jsonPath("$.totalElements").value(0));
	}

	@Test
	void getAll_ShouldReturnList_WhenBooksExist() throws Exception {
		BookResponse response = new BookResponse(1L, "Title", "111", "Name", 2L, null, null);
		Page<BookResponse> bookResponsePage = new PageImpl<>(List.of(response));
		when(bookService.getAllBooks(any(Pageable.class))).thenReturn(bookResponsePage);

		mockMvc.perform(get("/api/books"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").isMap())
				.andExpect(jsonPath("$.content").isArray())
				.andExpect(jsonPath("$.totalElements").value(1))
				.andExpect(jsonPath("$.content[0].id").value(1L))
				.andExpect(jsonPath("$.content[0].title").value("Title"))
				.andExpect(jsonPath("$.content[0].isbn").value("111"))
				.andExpect(jsonPath("$.content[0].authorName").value("Name"))
				.andExpect(jsonPath("$.content[0].authorId").value(2L));
	}

	@Test
	void getAll_ShouldPassCorrectPaginationAndSortingToService() throws Exception {
		int page = 7;
		int size = 23;
		String sortParam = "title,desc";
		ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);

		when(bookService.getAllBooks(any(Pageable.class))).thenReturn(Page.empty());

		mockMvc.perform(get("/api/books")
				.param("page", String.valueOf(page))
				.param("size", String.valueOf(size))
				.param("sort", sortParam)
		).andExpect(status().isOk());

		verify(bookService).getAllBooks(pageableCaptor.capture());
		Pageable capturedPageable = pageableCaptor.getValue();

		assertEquals(page, capturedPageable.getPageNumber());
		assertEquals(size, capturedPageable.getPageSize());

		Sort.Order order = capturedPageable.getSort().getOrderFor("title");
		assertNotNull(order);
		assertTrue(order.isDescending());
	}

	@Test
	void getById_ShouldReturnBook_WhenExists() throws Exception {
		BookResponse response = new BookResponse(1L, "Title", "111", "Name", 2L, null, null);
		when(bookService.findBookById(1L)).thenReturn(response);

		mockMvc.perform(get("/api/books/1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1L))
				.andExpect(jsonPath("$.title").value("Title"))
				.andExpect(jsonPath("$.isbn").value("111"))
				.andExpect(jsonPath("$.authorName").value("Name"))
				.andExpect(jsonPath("$.authorId").value(2L));
	}

	@Test
	void getById_ShouldReturnNotFound_WhenBookDoesNotExist() throws Exception {
		when(bookService.findBookById(1L)).thenThrow(new ResourceNotFoundException("Book not found"));

		mockMvc.perform(get("/api/books/1"))
				.andExpect(status().isNotFound());
	}

	@Test
	void getById_ShouldReturnDatesInJson() throws Exception {
		LocalDateTime date = LocalDateTime.of(2026, 2, 10, 12, 21);
		BookResponse response = new BookResponse(1L, "Title", "111", "Name", 2L, date, date);
		when(bookService.findBookById(1L)).thenReturn(response);

		mockMvc.perform(get("/api/books/1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.createdAt").value("2026-02-10 12:21:00"))
				.andExpect(jsonPath("$.modifiedAt").value("2026-02-10 12:21:00"));
	}

	@Test
	void search_ShouldReturnEmptyList_WhenNoMatchesFound() throws Exception {
		when(bookService.searchBooks("Title", "Name")).thenReturn(Collections.emptyList());

		mockMvc.perform(get("/api/books/search")
						.param("bookTitle", "Title")
						.param("authorName", "Name")
				)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").isArray())
				.andExpect(jsonPath("$.length()").value(0));
	}

	@Test
	void search_ShouldReturnList_WhenMatchesFound() throws Exception {
		BookResponse response = new BookResponse(1L, "AAA", "111", "BBB", 2L, null, null);
		when(bookService.searchBooks("A", "B")).thenReturn(List.of(response));

		mockMvc.perform(get("/api/books/search")
						.param("bookTitle", "A")
						.param("authorName", "B")
				)
				.andExpect(status().isOk())
				.andExpect(jsonPath("$").isArray())
				.andExpect(jsonPath("$.length()").value(1))
				.andExpect(jsonPath("$[0].id").value(1L))
				.andExpect(jsonPath("$[0].title").value("AAA"))
				.andExpect(jsonPath("$[0].isbn").value("111"))
				.andExpect(jsonPath("$[0].authorName").value("BBB"))
				.andExpect(jsonPath("$[0].authorId").value(2L));

	}

	@Test
	void updateBook_ShouldReturnUpdatedBook_WhenBookExists() throws Exception {
		Long id = 1L;
		BookRequest request = new BookRequest("NewTitle", "111", 2L);
		BookResponse response = new BookResponse(id, "NewTitle", "111", "AuthorName", 2L, null, null);
		when(bookService.updateBook(id, request)).thenReturn(response);

		mockMvc.perform(put("/api/books/1")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request))
				).andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(id))
				.andExpect(jsonPath("$.title").value("NewTitle"))
				.andExpect(jsonPath("$.isbn").value("111"))
				.andExpect(jsonPath("$.authorName").value("AuthorName"))
				.andExpect(jsonPath("$.authorId").value(2));
	}

	@Test
	void updateBook_ShouldReturnNotFound_WhenNotFound() throws Exception {
		Long id = 9L;
		BookRequest request = new BookRequest("NewTitle", "111", 2L);
		when(bookService.updateBook(id, request)).thenThrow(new ResourceNotFoundException("Not found"));

		mockMvc.perform(put("/api/books/9")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
		).andExpect(status().isNotFound());
	}

	@Test
	void updateBook_ShouldReturnBadRequest_WhenTitleIsEmpty() throws Exception {
		Long id = 9L;
		BookRequest request = new BookRequest("", "111", 2L);

		mockMvc.perform(put("/api/books/9")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request))
		).andExpect(status().isBadRequest());

		verify(bookService, never()).updateBook(any(), any());
	}

	@Test
	void deleteBook_ShouldReturnNoContent_WhenBookExists() throws Exception {
		Long id = 1L;
		doNothing().when(bookService).deleteBook(id);

		mockMvc.perform(delete("/api/books/1"))
				.andExpect(status().isNoContent());

		verify(bookService, times(1)).deleteBook(any());
	}


	@Test
	void deleteBook_ShouldReturnNotFound_WhenBookDoesNotExist() throws Exception {
		Long id = 9L;
		doThrow(new ResourceNotFoundException("Not Found")).when(bookService).deleteBook(id);

		mockMvc.perform(delete("/api/books/9"))
				.andExpect(status().isNotFound());

		verify(bookService, times(1)).deleteBook(any());
	}
}
