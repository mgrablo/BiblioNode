package io.github.mgrablo.BiblioNode.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import static java.util.Collections.emptyList;

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
		Book book = spy(createTestBook(1L, "Test Book", "111"));
		Reader reader = createTestReader(1L, "Test Reader", "test@email.com");
		Loan loan = createTestLoan(1L, book, reader, expectedNow);
		LoanRequest request = new LoanRequest(1L, 1L);
		LoanResponse expectedResponse = createTestLoanResponse(1L, book, reader, expectedNow, null);

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
		Book book = createTestBook(1L, "Test Book", "111");
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
		Book book = createTestBook(1L, "Test Book", "111");
		LoanRequest request = new LoanRequest(1L, 1L);

		when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
		when(readerRepository.findById(1L)).thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class, () -> loanService.borrowBook(request));
	}

	@Test
	public void returnBook_ShouldReturnLoanResponse_WhenLoanValid() {
		Book book = spy(createTestBook(1L, "Test Book", "111"));
		book.setAvailable(false);
		Reader reader = createTestReader(1L, "Test Reader", "test@email.com");
		LocalDateTime expectedNow = LocalDateTime.now(fixedClock);
		Loan loan = createTestLoan(1L, book, reader, expectedNow);
		LoanResponse expectedResponse = createTestLoanResponse(1L, book, reader, expectedNow, expectedNow);

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
		Book book = createTestBook(1L, "Test Book", "111");
		book.setAvailable(true);
		Reader reader = createTestReader(1L, "Test Reader", "test@email.com");
		LocalDateTime expectedNow = LocalDateTime.now(fixedClock);
		Loan loan = createTestLoan(1L, book, reader, expectedNow.minusDays(14));
		loan.setReturnDate(expectedNow.minusDays(7));

		when(loanRepository.findById(1L)).thenReturn(Optional.of(loan));

		assertThrows(LoanAlreadyReturnedException.class, () -> loanService.returnBook(1L));
	}

	@Test
	public void getAllLoans_ShouldReturnMappedPage() {
		Book book = createTestBook(1L, "Test Book", "111");
		Reader reader = createTestReader(1L, "Test Reader", "test@email.com");
		LocalDateTime expectedNow = LocalDateTime.now(fixedClock);
		Loan loan = createTestLoan(1L, book, reader, expectedNow);
		LoanResponse expectedResponse = createTestLoanResponse(1L, book, reader, expectedNow, null);
		Page<Loan> loanPage = new PageImpl<>(List.of(loan));

		when(loanRepository.findAll(any(Pageable.class))).thenReturn(loanPage);
		when(mapper.toResponse(any(Loan.class))).thenReturn(expectedResponse);

		Page<LoanResponse> result = loanService.getAllLoans(Pageable.ofSize(10));

		assertEquals(1, result.getTotalElements());
		assertEquals(expectedResponse, result.getContent().getFirst());
	}

	@Test
	public void getLoansByReaderId_ShouldReturnMappedPage() {
		Book book = createTestBook(1L, "Test Book", "111");
		Reader reader = createTestReader(1L, "Test Reader", "test@email.com");
		LocalDateTime expectedNow = LocalDateTime.now(fixedClock);
		Loan loan = createTestLoan(1L, book, reader, expectedNow);
		LoanResponse expectedResponse = createTestLoanResponse(1L, book, reader, expectedNow, null);
		Page<Loan> loanPage = new PageImpl<>(List.of(loan));

		when(loanRepository.findByReaderId(anyLong(), any(Pageable.class))).thenReturn(loanPage);
		when(mapper.toResponse(any(Loan.class))).thenReturn(expectedResponse);

		Page<LoanResponse> result = loanService.getLoansByReaderId(1L, Pageable.ofSize(10));

		assertEquals(1, result.getTotalElements());
		assertEquals(expectedResponse, result.getContent().getFirst());
	}

	@Test
	public void getLoansByBookId_ShouldReturnMappedPage() {
		Book book = createTestBook(1L, "Test Book", "111");
		Reader reader = createTestReader(1L, "Test Reader", "test@email.com");
		LocalDateTime expectedNow = LocalDateTime.now(fixedClock);
		Loan loan = createTestLoan(1L, book, reader, expectedNow);
		LoanResponse expectedResponse = createTestLoanResponse(1L, book, reader, expectedNow, null);
		Page<Loan> loanPage = new PageImpl<>(List.of(loan));

		when(loanRepository.findByBookId(anyLong(), any(Pageable.class))).thenReturn(loanPage);
		when(mapper.toResponse(any(Loan.class))).thenReturn(expectedResponse);

		Page<LoanResponse> result = loanService.getLoansByBookId(1L, Pageable.ofSize(10));

		assertEquals(1, result.getTotalElements());
		assertEquals(expectedResponse, result.getContent().getFirst());
	}

	@Test
	public void getActiveLoans_ShouldReturnMappedPage() {
		Book book = createTestBook(1L, "Test Book", "111");
		Reader reader = createTestReader(1L, "Test Reader", "test@email.com");
		LocalDateTime expectedNow = LocalDateTime.now(fixedClock);
		Loan loan = createTestLoan(1L, book, reader, expectedNow);
		LoanResponse expectedResponse = createTestLoanResponse(1L, book, reader, expectedNow, null);
		Page<Loan> loanPage = new PageImpl<>(List.of(loan));

		when(loanRepository.findAllByReturnDateIsNull(any(Pageable.class))).thenReturn(loanPage);
		when(mapper.toResponse(any(Loan.class))).thenReturn(expectedResponse);

		Page<LoanResponse> result = loanService.getActiveLoans(Pageable.ofSize(10));

		assertEquals(1, result.getTotalElements());
		assertEquals(expectedResponse, result.getContent().getFirst());
	}

	@Test
	public void getActiveLoansByReaderId_ShouldReturnMappedPage() {
		Book book = createTestBook(1L, "Test Book", "111");
		Reader reader = createTestReader(1L, "Test Reader", "test@email.com");
		LocalDateTime expectedNow = LocalDateTime.now(fixedClock);
		Loan loan = createTestLoan(1L, book, reader, expectedNow);
		LoanResponse expectedResponse = createTestLoanResponse(1L, book, reader, expectedNow, null);
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

	private Book createTestBook(Long id, String title, String isbn) {
		Author author = new Author();
		author.setId(1L);
		author.setName("Test Author");
		author.setBiography("Bio");
		author.setBooks(emptyList());
		Book book = new Book();
		book.setId(id);
		book.setTitle(title);
		book.setIsbn(isbn);
		book.setAvailable(true);
		book.setAuthor(author);
		return book;
	}

	private Reader createTestReader(Long id, String name, String email) {
		Reader reader = new Reader();
		reader.setId(id);
		reader.setFullName(name);
		reader.setEmail(email);
		reader.setLoans(emptyList());

		return reader;
	}

	private Loan createTestLoan(Long id, Book book, Reader reader, LocalDateTime loanDate) {
		Loan loan = new Loan();
		loan.setId(id);
		loan.setBook(book);
		loan.setReader(reader);
		loan.setLoanDate(loanDate);
		loan.setDueDate(loanDate.plusDays(14));

		return loan;
	}

	private LoanResponse createTestLoanResponse(Long id, Book book, Reader reader, LocalDateTime loanDate, LocalDateTime returnDate) {
		return new LoanResponse(
				id,
				book.getId(),
				book.getTitle(),
				book.getAuthor().getName(),
				book.getIsbn(),
				reader.getId(),
				loanDate,
				loanDate.plusDays(14),
				returnDate
		);
	}
}
