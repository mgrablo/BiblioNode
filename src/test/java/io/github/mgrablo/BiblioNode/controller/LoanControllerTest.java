package io.github.mgrablo.BiblioNode.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
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

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import io.github.mgrablo.BiblioNode.dto.LoanRequest;
import io.github.mgrablo.BiblioNode.dto.LoanResponse;
import io.github.mgrablo.BiblioNode.exception.BookNotAvailableException;
import io.github.mgrablo.BiblioNode.exception.LoanAlreadyReturnedException;
import io.github.mgrablo.BiblioNode.exception.ResourceNotFoundException;
import io.github.mgrablo.BiblioNode.service.LoanService;
import tools.jackson.databind.ObjectMapper;

@WebMvcTest(LoanController.class)
@AutoConfigureMockMvc(addFilters = false)
public class LoanControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private LoanService loanService;

	@Autowired
	private ObjectMapper objectMapper;

	private Clock fixedClock;
	private final Instant fixedInstant = Instant.parse("2026-01-01T12:00:01Z");
	private final ZoneId zoneId = ZoneId.of("UTC");

	@BeforeEach
	public void setup() {
		fixedClock = Clock.fixed(fixedInstant, zoneId);
	}

	@Test
	public void borrowBook_ShouldReturnCreated_WhenValidRequest() throws Exception {
		LoanRequest request = new LoanRequest(5L, 12L);
		LocalDateTime fixedNow = LocalDateTime.now(fixedClock);

		when(loanService.borrowBook(any(LoanRequest.class)))
				.thenReturn(new LoanResponse(1L,
						5L,
						"Test Title",
						"Test Author",
						"111",
						12L,
						fixedNow,
						fixedNow.plusDays(14),
						null
						));

		mockMvc.perform(post("/api/loans/borrow")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request))
				).andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").value(1L))
				.andExpect(jsonPath("$.bookId").value(5L))
				.andExpect(jsonPath("$.bookTitle").value("Test Title"))
				.andExpect(jsonPath("$.bookAuthorName").value("Test Author"))
				.andExpect(jsonPath("$.bookIsbn").value("111"))
				.andExpect(jsonPath("$.readerId").value(12L))
				.andExpect(jsonPath("$.loanDate").value(fixedNow.toString()))
				.andExpect(jsonPath("$.dueDate").value(fixedNow.plusDays(14).toString()))
				.andExpect(jsonPath("$.returnDate").doesNotExist());
	}

	@Test
	public void borrowBook_ShouldReturnConflict_WhenBookNotAvailable() throws Exception {
		LoanRequest request = new LoanRequest(5L, 12L);

		when(loanService.borrowBook(any(LoanRequest.class)))
				.thenThrow(new BookNotAvailableException("Book not available"));

		mockMvc.perform(post("/api/loans/borrow")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request))
				).andExpect(status().isConflict())
				.andExpect(jsonPath("$.message").value("Book not available"));
	}

	@Test
	public void borrowBook_ShouldReturnBadRequest_WhenInvalidRequest() throws Exception {
		LoanRequest request = new LoanRequest(null, null);

		mockMvc.perform(post("/api/loans/borrow")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request))
				).andExpect(status().isBadRequest());
	}

	@Test
	public void borrowBook_ShouldReturnNotFound_WhenBookOrReaderNotFound() throws Exception {
		LoanRequest request = new LoanRequest(5L, 12L);

		when(loanService.borrowBook(any(LoanRequest.class)))
				.thenThrow(new ResourceNotFoundException("Resource not found"));

		mockMvc.perform(post("/api/loans/borrow")
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(request))
				).andExpect(status().isNotFound())
				.andExpect(jsonPath("$.message").value("Resource not found"));
	}

	@Test
	public void returnBook_ShouldReturnOk_WhenLoanExists() throws Exception {
		Long loanId = 1L;
		LocalDateTime fixedNow = LocalDateTime.now(fixedClock);

		when(loanService.returnBook(loanId))
				.thenReturn(new LoanResponse(loanId,
						5L,
						"Test Title",
						"Test Author",
						"111",
						12L,
						fixedNow.minusDays(15),
						fixedNow.minusDays(1),
						fixedNow
				));

		mockMvc.perform(patch("/api/loans/{id}/return", loanId)
						.contentType(MediaType.APPLICATION_JSON)
				).andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(loanId))
				.andExpect(jsonPath("$.returnDate").value(fixedNow.toString()));
	}

	@Test
	public void returnBook_ShouldReturnNotFound_WhenLoanDoesNotExist() throws Exception {
		Long loanId = 1L;

		when(loanService.returnBook(loanId))
				.thenThrow(new ResourceNotFoundException("Loan not found"));

		mockMvc.perform(patch("/api/loans/{id}/return", loanId)
						.contentType(MediaType.APPLICATION_JSON)
				).andExpect(status().isNotFound())
				.andExpect(jsonPath("$.message").value("Loan not found"));
	}

	@Test
	public void returnBook_ShouldReturnBadRequest_WhenInvalidId() throws Exception {
		mockMvc.perform(patch("/api/loans/{id}/return", "invalid-id")
						.contentType(MediaType.APPLICATION_JSON)
				).andExpect(status().isBadRequest());
	}

	@Test
	public void returnBook_ShouldReturnConflict_WhenLoanAlreadyReturned() throws Exception {
		Long loanId = 1L;

		when(loanService.returnBook(loanId))
				.thenThrow(new LoanAlreadyReturnedException("Loan already returned"));

		mockMvc.perform(patch("/api/loans/{id}/return", loanId)
						.contentType(MediaType.APPLICATION_JSON)
				).andExpect(status().isConflict())
				.andExpect(jsonPath("$.message").value("Loan already returned"));
	}

	@Test
	public void getLoans_ShouldReturnOk_WhenNoFilters() throws Exception {
		LoanResponse response = createMockLoanResponse(
				LocalDateTime.now(fixedClock).minusDays(15),
				LocalDateTime.now(fixedClock).minusDays(1),
				null
		);
		Page<LoanResponse> loanResponsePage = new PageImpl<>(List.of(response));

		when(loanService.getAllLoans(any(Pageable.class))).thenReturn(loanResponsePage);

		mockMvc.perform(get("/api/loans")
						.contentType(MediaType.APPLICATION_JSON)
				).andExpect(status().isOk())
				.andExpect(jsonPath("$.content").isArray())
				.andExpect(jsonPath("$.content.length()").value(1))
				.andExpect(jsonPath("$.content[0].id").value(1L))
				.andExpect(jsonPath("$.content[0].readerId").value(12L))
				.andExpect(jsonPath("$.content[0].bookId").value(5L))
				.andExpect(jsonPath("$.content[0].bookTitle").value("Test Title"))
				.andExpect(jsonPath("$.content[0].bookAuthorName").value("Test Author"))
				.andExpect(jsonPath("$.content[0].bookIsbn").value("111"));

		verify(loanService).getAllLoans(any(Pageable.class));
	}

	@Test
	public void getLoans_ShouldReturnOk_WhenReaderIdFilter() throws Exception {
		LoanResponse response = createMockLoanResponse(
				LocalDateTime.now(fixedClock).minusDays(15),
				LocalDateTime.now(fixedClock).minusDays(1),
				null
		);
		Page<LoanResponse> loanResponsePage = new PageImpl<>(List.of(response));

		when(loanService.getLoansByReaderId(eq(12L), any(Pageable.class))).thenReturn(loanResponsePage);

		mockMvc.perform(get("/api/loans")
						.param("readerId", "12")
						.contentType(MediaType.APPLICATION_JSON)
				).andExpect(status().isOk())
				.andExpect(jsonPath("$.content").isArray())
				.andExpect(jsonPath("$.content.length()").value(1))
				.andExpect(jsonPath("$.content[0].id").value(1L))
				.andExpect(jsonPath("$.content[0].readerId").value(12L))
				.andExpect(jsonPath("$.content[0].bookId").value(5L))
				.andExpect(jsonPath("$.content[0].bookTitle").value("Test Title"))
				.andExpect(jsonPath("$.content[0].bookAuthorName").value("Test Author"))
				.andExpect(jsonPath("$.content[0].bookIsbn").value("111"));

		verify(loanService).getLoansByReaderId(eq(12L), any(Pageable.class));
	}

	@Test
	public void getLoans_ShouldReturnOk_WhenReaderIdAndActiveOnlyFilter() throws Exception {
		LoanResponse response = createMockLoanResponse(
				LocalDateTime.now(fixedClock).minusDays(15),
				LocalDateTime.now(fixedClock).plusDays(1),
				null
		);
		Page<LoanResponse> loanResponsePage = new PageImpl<>(List.of(response));

		when(loanService.getActiveLoansByReaderId(eq(12L), any(Pageable.class))).thenReturn(loanResponsePage);

		mockMvc.perform(get("/api/loans")
				.param("readerId", "12")
				.param("activeOnly", "true")
				.contentType(MediaType.APPLICATION_JSON)
		).andExpect(status().isOk())
				.andExpect(jsonPath("$.content").isArray())
				.andExpect(jsonPath("$.content.length()").value(1))
				.andExpect(jsonPath("$.content[0].id").value(1L))
				.andExpect(jsonPath("$.content[0].readerId").value(12L))
				.andExpect(jsonPath("$.content[0].bookId").value(5L))
				.andExpect(jsonPath("$.content[0].bookTitle").value("Test Title"))
				.andExpect(jsonPath("$.content[0].bookAuthorName").value("Test Author"))
				.andExpect(jsonPath("$.content[0].bookIsbn").value("111"));

		verify(loanService).getActiveLoansByReaderId(eq(12L), any(Pageable.class));
	}

	@Test
	public void getLoans_ShouldReturnOk_WhenBookIdFilter() throws Exception {
		LoanResponse response = createMockLoanResponse(
				LocalDateTime.now(fixedClock).minusDays(15),
				LocalDateTime.now(fixedClock).minusDays(1),
				null
		);
		Page<LoanResponse> loanResponsePage = new PageImpl<>(List.of(response));

		when(loanService.getLoansByBookId(eq(5L), any(Pageable.class))).thenReturn(loanResponsePage);

		mockMvc.perform(get("/api/loans")
				.param("bookId", "5")
				.contentType(MediaType.APPLICATION_JSON)
		).andExpect(status().isOk())
		.andExpect(jsonPath("$.content").isArray())
				.andExpect(jsonPath("$.content.length()").value(1))
				.andExpect(jsonPath("$.content[0].id").value(1L))
				.andExpect(jsonPath("$.content[0].readerId").value(12L))
				.andExpect(jsonPath("$.content[0].bookId").value(5L))
				.andExpect(jsonPath("$.content[0].bookTitle").value("Test Title"))
				.andExpect(jsonPath("$.content[0].bookAuthorName").value("Test Author"))
				.andExpect(jsonPath("$.content[0].bookIsbn").value("111"));

		verify(loanService).getLoansByBookId(eq(5L), any(Pageable.class));
	}

	@Test
	public void getLoans_ShouldReturnOk_WhenActiveOnlyFilter() throws Exception {
		LoanResponse response = createMockLoanResponse(
				LocalDateTime.now(fixedClock).minusDays(15),
				LocalDateTime.now(fixedClock).plusDays(1),
				null
		);
		Page<LoanResponse> loanResponsePage = new PageImpl<>(List.of(response));

		when(loanService.getActiveLoans(any(Pageable.class))).thenReturn(loanResponsePage);

		mockMvc.perform(get("/api/loans")
				.param("activeOnly", "true")
				.contentType(MediaType.APPLICATION_JSON)
		).andExpect(status().isOk())
				.andExpect(jsonPath("$.content").isArray())
				.andExpect(jsonPath("$.content.length()").value(1))
				.andExpect(jsonPath("$.content[0].id").value(1L))
				.andExpect(jsonPath("$.content[0].readerId").value(12L))
				.andExpect(jsonPath("$.content[0].bookId").value(5L))
				.andExpect(jsonPath("$.content[0].bookTitle").value("Test Title"))
				.andExpect(jsonPath("$.content[0].bookAuthorName").value("Test Author"))
				.andExpect(jsonPath("$.content[0].bookIsbn").value("111"));

		verify(loanService).getActiveLoans(any(Pageable.class));
	}

	@Test
	public void getOverdueLoans_ShouldReturnOk() throws Exception {
		LoanResponse response = createMockLoanResponse(
				LocalDateTime.now(fixedClock).minusDays(15),
				LocalDateTime.now(fixedClock).minusDays(1),
				null
		);
		Page<LoanResponse> loanResponsePage = new PageImpl<>(List.of(response));

		when(loanService.getOverdueLoans(any(Pageable.class))).thenReturn(loanResponsePage);

		mockMvc.perform(get("/api/loans/overdue")
						.contentType(MediaType.APPLICATION_JSON)
				).andExpect(status().isOk())
				.andExpect(jsonPath("$.content").isArray())
				.andExpect(jsonPath("$.content.length()").value(1))
				.andExpect(jsonPath("$.content[0].id").value(1L))
				.andExpect(jsonPath("$.content[0].readerId").value(12L))
				.andExpect(jsonPath("$.content[0].bookId").value(5L))
				.andExpect(jsonPath("$.content[0].bookTitle").value("Test Title"))
				.andExpect(jsonPath("$.content[0].bookAuthorName").value("Test Author"))
				.andExpect(jsonPath("$.content[0].bookIsbn").value("111"));

		verify(loanService).getOverdueLoans(any(Pageable.class));
	}

	@Test
	public void getLoans_ShouldReturnBadRequest_WhenInvalidActiveOnlyValue() throws Exception {
		mockMvc.perform(get("/api/loans")
				.param("activeOnly", "invalid-value")
				.contentType(MediaType.APPLICATION_JSON)
		).andExpect(status().isBadRequest());
	}

	@Test
	public void getLoans_ShouldReturnBadRequest_WhenInvalidReaderId() throws Exception {
		mockMvc.perform(get("/api/loans")
				.param("readerId", "invalid-id")
				.contentType(MediaType.APPLICATION_JSON)
		).andExpect(status().isBadRequest());
	}

	@Test
	public void getLoans_ShouldReturnBadRequest_WhenInvalidBookId() throws Exception {
		mockMvc.perform(get("/api/loans")
				.param("bookId", "invalid-id")
				.contentType(MediaType.APPLICATION_JSON)
		).andExpect(status().isBadRequest());
	}

	private LoanResponse createMockLoanResponse(
			LocalDateTime loanDate,
			LocalDateTime dueDate,
			LocalDateTime returnDate
	) {
		return new LoanResponse(1L,
				5L,
				"Test Title",
				"Test Author",
				"111",
				12L,
				loanDate,
				dueDate,
				returnDate
		);
	}
}
