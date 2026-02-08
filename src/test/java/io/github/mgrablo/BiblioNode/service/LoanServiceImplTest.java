package io.github.mgrablo.BiblioNode.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import io.github.mgrablo.BiblioNode.dto.LoanRequest;
import io.github.mgrablo.BiblioNode.dto.LoanResponse;
import io.github.mgrablo.BiblioNode.exception.BookNotAvailableException;
import io.github.mgrablo.BiblioNode.exception.LoanAlreadyReturnedException;
import io.github.mgrablo.BiblioNode.exception.ResourceNotFoundException;
import io.github.mgrablo.BiblioNode.mapper.LoanMapper;
import io.github.mgrablo.BiblioNode.model.Author;
import io.github.mgrablo.BiblioNode.model.Book;
import io.github.mgrablo.BiblioNode.model.Loan;
import io.github.mgrablo.BiblioNode.model.Reader;
import io.github.mgrablo.BiblioNode.repository.BookRepository;
import io.github.mgrablo.BiblioNode.repository.LoanRepository;
import io.github.mgrablo.BiblioNode.repository.ReaderRepository;

@ExtendWith(MockitoExtension.class)
public class LoanServiceImplTest {
	@Mock
	private LoanMapper mapper;

	@Mock
	private LoanRepository loanRepository;

	@Mock
	private BookRepository bookRepository;

	@Mock
	private ReaderRepository readerRepository;

	@InjectMocks
	private LoanServiceImpl loanService;

	private Clock fixedClock;
	private final Instant fixedInstant = Instant.parse("2026-01-01T12:00:00Z");
	private final ZoneId zoneId = ZoneId.of("UTC");

	private static final Author testAuthor = new Author(1L, "Test Author", "Bio", null);

	@BeforeEach
	void setup() {
		fixedClock = Clock.fixed(fixedInstant, zoneId);
		loanService = new LoanServiceImpl(loanRepository,
				bookRepository,
				readerRepository,
				mapper,
				fixedClock
		);
	}

	@Test
	public void borrowBook_ShouldReturnLoanResponse_WhenBookAvailable() {
		LocalDateTime expectedNow = LocalDateTime.now(fixedClock);
		Book book = spy(new Book(1L, "Test Book", "111", testAuthor));
		Reader reader = new Reader(1L, "Test Reader", "test@email.com", null);
		Loan loan = new Loan(1L, book, reader, expectedNow, null, null);
		LoanRequest request = new LoanRequest(1L, 1L);
		LoanResponse expectedResponse = new LoanResponse(
				1L,
				1L,
				"Test Book",
				testAuthor.getName(),
				"111",
				1L,
				expectedNow,
				expectedNow.plusDays(14),
				null
		);

		when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
		when(readerRepository.findById(1L)).thenReturn(Optional.of(reader));
		when(loanRepository.save(any(Loan.class))).thenReturn(loan);
		when(mapper.toResponse(any(Loan.class))).thenReturn(expectedResponse);

		LoanResponse result = loanService.borrowBook(request);

		assertEquals(expectedResponse, result);
		assertFalse(book.isAvailable());
		verify(loanRepository, times(1)).save(any(Loan.class));
	}

	@Test
	public void borrowBook_ShouldThrowException_WhenBookNotAvailable() {
		Book book = new Book(1L, "Test Book", "111", testAuthor);
		book.setAvailable(false);
		LoanRequest request = new LoanRequest(1L, 1L);

		when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

		assertThrows(BookNotAvailableException.class, () -> loanService.borrowBook(request));
	}

	@Test
	public void borrowBook_ShouldThrowException_WhenBookNotFound() {
		LoanRequest request = new LoanRequest(1L, 1L);

		when(bookRepository.findById(1L)).thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class, () -> loanService.borrowBook(request));
	}

	@Test
	public void borrowBook_ShouldThrowException_WhenReaderNotFound() {
		Book book = new Book(1L, "Test Book", "111", testAuthor);
		LoanRequest request = new LoanRequest(1L, 1L);

		when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
		when(readerRepository.findById(1L)).thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class, () -> loanService.borrowBook(request));
	}

	@Test
	public void returnBook_ShouldReturnLoanResponse_WhenLoanValid() {
		Book book = spy(new Book(1L, "Test Book", "111", testAuthor));
		book.setAvailable(false);
		Reader reader = new Reader(1L, "Test Reader", "test@email.com", null);
		LocalDateTime expectedNow = LocalDateTime.now(fixedClock);
		Loan loan = new Loan(1L, book, reader, expectedNow, null, null);
		LoanResponse expectedResponse = new LoanResponse(
				1L,
				1L,
				"Test Book",
				testAuthor.getName(),
				"111",
				1L,
				expectedNow,
				expectedNow.plusDays(14),
				expectedNow
		);

		when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));
		when(loanRepository.save(any(Loan.class))).thenReturn(loan);
		when(mapper.toResponse(any(Loan.class))).thenReturn(expectedResponse);

		LoanResponse result = loanService.returnBook(1L);

		assertEquals(expectedResponse, result);
		verify(loanRepository, times(1)).save(any(Loan.class));
		verify(book).setAvailable(true);
	}

	@Test
	public void returnBook_ShouldThrowException_WhenLoanNotFound() {
		when(loanRepository.findById(1L)).thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class, () -> loanService.returnBook(1L));
	}

	@Test
	public void returnBook_ShouldThrowException_WhenLoanAlreadyReturned() {
		Book book = new Book(1L, "Test Book", "111", testAuthor);
		book.setAvailable(true);
		Reader reader = new Reader(1L, "Test Reader", "test@email.com", null);
		LocalDateTime expectedNow = LocalDateTime.now(fixedClock);
		Loan loan = new Loan(1L, book, reader, expectedNow, expectedNow.plusDays(14), expectedNow.plusDays(8));

		when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));

		assertThrows(LoanAlreadyReturnedException.class, () -> loanService.returnBook(1L));
	}

	@Test
	public void getAllLoans_ShouldReturnMappedPage() {
		Book book = new Book(1L, "Test Book", "111", testAuthor);
		Reader reader = new Reader(1L, "Test Reader", "test@email.com", null);
		LocalDateTime expectedNow = LocalDateTime.now(fixedClock);
		Loan loan = new Loan(1L, book, reader, expectedNow, null, null);
		LoanResponse expectedResponse = new LoanResponse(
				1L,
				1L,
				"Test Book",
				testAuthor.getName(),
				"111",
				1L,
				expectedNow,
				expectedNow.plusDays(14),
				null
		);
		Page<Loan> loanPage = new PageImpl<>(List.of(loan));

		when(loanRepository.findAll(any(Pageable.class))).thenReturn(loanPage);
		when(mapper.toResponse(any(Loan.class))).thenReturn(expectedResponse);

		Page<LoanResponse> result = loanService.getAllLoans(Pageable.ofSize(10));

		assertEquals(1, result.getTotalElements());
		assertEquals(expectedResponse, result.getContent().getFirst());
	}

	@Test
	public void getLoansByReaderId_ShouldReturnMappedPage() {
		Book book = new Book(1L, "Test Book", "111", testAuthor);
		Reader reader = new Reader(1L, "Test Reader", "test@email.com", null);
		LocalDateTime expectedNow = LocalDateTime.now(fixedClock);
		Loan loan = new Loan(1L, book, reader, expectedNow, null, null);
		LoanResponse expectedResponse = new LoanResponse(
				1L,
				1L,
				"Test Book",
				testAuthor.getName(),
				"111",
				1L,
				expectedNow,
				expectedNow.plusDays(14),
				null
		);
		Page<Loan> loanPage = new PageImpl<>(List.of(loan));

		when(loanRepository.findByReaderId(anyLong(), any(Pageable.class))).thenReturn(loanPage);
		when(mapper.toResponse(any(Loan.class))).thenReturn(expectedResponse);

		Page<LoanResponse> result = loanService.getLoansByReaderId(1L, Pageable.ofSize(10));

		assertEquals(1, result.getTotalElements());
		assertEquals(expectedResponse, result.getContent().getFirst());
	}

	@Test
	public void getLoansByBookId_ShouldReturnMappedPage() {
		Book book = new Book(1L, "Test Book", "111", testAuthor);
		Reader reader = new Reader(1L, "Test Reader", "test@email.com", null);
		LocalDateTime expectedNow = LocalDateTime.now(fixedClock);
		Loan loan = new Loan(1L, book, reader, expectedNow, null, null);
		LoanResponse expectedResponse = new LoanResponse(
				1L,
				1L,
				"Test Book",
				testAuthor.getName(),
				"111",
				1L,
				expectedNow,
				expectedNow.plusDays(14),
				null
		);
		Page<Loan> loanPage = new PageImpl<>(List.of(loan));

		when(loanRepository.findByBookId(anyLong(), any(Pageable.class))).thenReturn(loanPage);
		when(mapper.toResponse(any(Loan.class))).thenReturn(expectedResponse);

		Page<LoanResponse> result = loanService.getLoansByBookId(1L, Pageable.ofSize(10));

		assertEquals(1, result.getTotalElements());
		assertEquals(expectedResponse, result.getContent().getFirst());
	}

	@Test
	public void getActiveLoans_ShouldReturnMappedPage() {
		Book book = new Book(1L, "Test Book", "111", testAuthor);
		Reader reader = new Reader(1L, "Test Reader", "test@email.com", null);
		LocalDateTime expectedNow = LocalDateTime.now(fixedClock);
		Loan loan = new Loan(1L, book, reader, expectedNow, null, null);
		LoanResponse expectedResponse = new LoanResponse(
				1L,
				1L,
				"Test Book",
				testAuthor.getName(),
				"111",
				1L,
				expectedNow,
				expectedNow.plusDays(14),
				null);
		Page<Loan> loanPage = new PageImpl<>(List.of(loan));

		when(loanRepository.findAllByReturnDateIsNull(any(Pageable.class))).thenReturn(loanPage);
		when(mapper.toResponse(any(Loan.class))).thenReturn(expectedResponse);

		Page<LoanResponse> result = loanService.getActiveLoans(Pageable.ofSize(10));

		assertEquals(1, result.getTotalElements());
		assertEquals(expectedResponse, result.getContent().getFirst());
	}

	@Test
	public void getActiveLoansByReaderId_ShouldReturnMappedPage() {
		Book book = new Book(1L, "Test Book", "111", testAuthor);
		Reader reader = new Reader(1L, "Test Reader", "test@email.com", null);
		LocalDateTime expectedNow = LocalDateTime.now(fixedClock);
		Loan loan = new Loan(1L, book, reader, expectedNow, null, null);
		LoanResponse expectedResponse = new LoanResponse(
				1L,
				1L,
				"Test Book",
				testAuthor.getName(),
				"111",
				1L,
				expectedNow,
				expectedNow.plusDays(14),
				null);
		Page<Loan> loanPage = new PageImpl<>(List.of(loan));

		when(loanRepository.findAllByReturnDateIsNullAndReaderId(anyLong(), any(Pageable.class))).thenReturn(loanPage);
		when(mapper.toResponse(any(Loan.class))).thenReturn(expectedResponse);

		Page<LoanResponse> result = loanService.getActiveLoansByReaderId(1L, Pageable.ofSize(10));

		assertEquals(1, result.getTotalElements());
		assertEquals(expectedResponse, result.getContent().getFirst());
	}

	@Test
	public void getOverdueLoans_ShouldUseCurrentTimeToFindOverdue() {
		Page<Loan> loanPage = new PageImpl<>(List.of());

		when(loanRepository.findAllByReturnDateIsNullAndDueDateBefore(any(LocalDateTime.class), any(Pageable.class)))
				.thenReturn(loanPage);

		loanService.getOverdueLoans(Pageable.ofSize(10));

		verify(loanRepository, times(1))
				.findAllByReturnDateIsNullAndDueDateBefore(eq(LocalDateTime.now(fixedClock)), any(Pageable.class));
	}
}
