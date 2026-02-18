package io.github.mgrablo.BiblioNode.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import io.github.mgrablo.BiblioNode.config.JpaConfig;
import io.github.mgrablo.BiblioNode.model.Author;
import io.github.mgrablo.BiblioNode.model.Book;
import io.github.mgrablo.BiblioNode.model.Loan;
import io.github.mgrablo.BiblioNode.model.Reader;

@DataJpaTest
@Import(JpaConfig.class)
public class LoanRepositoryTest {
	@Autowired
	private LoanRepository loanRepository;

	@Autowired
	private TestEntityManager entityManager;

	private Clock fixedClock;
	private final Instant fixedInstant = Instant.parse("2026-01-01T12:00:00Z");
	private final ZoneId zoneId = ZoneId.of("UTC");

	@BeforeEach
	void setup() {
		fixedClock = Clock.fixed(fixedInstant, zoneId);
	}

	@Test
	public void shouldFindOverdueLoans() {
		// GIVEN
		Author author = persistAuthor("Author1");
		Book book = persistBook("Book 1", "1", author);

		Reader reader = persistReader("Reader1", "reader@email.com");
		entityManager.persist(reader);

		LocalDateTime now = LocalDateTime.now(fixedClock);

		Loan overdueLoan = persistLoan(book, reader, now.minusDays(20), now.minusDays(6), null); // Overdue
		Loan activeLoan = persistLoan(book, reader, now.minusDays(10), now.plusDays(4), null); // Active
		Loan returnedLoan = persistLoan(book, reader, now.minusDays(15), now.minusDays(1), now.minusDays(2)); // Returned

		entityManager.flush();

		// WHEN
		Page<Loan> overdueLoans = loanRepository.findAllByReturnDateIsNullAndDueDateBefore(now, Pageable.ofSize(10));

		// THEN
		assertEquals(1, overdueLoans.getTotalElements());
		assertEquals(overdueLoan.getId(), overdueLoans.getContent().getFirst().getId());
	}

	@Test
	public void shouldFindActiveLoansByReaderId() {
		// GIVEN
		Author author = persistAuthor("Author1");
		Book book = persistBook("Book 1", "1", author);
		Reader reader1 = persistReader("Reader1", "reader1@email.com");
		Reader reader2 = persistReader("Reader2", "reader2@email.com");

		LocalDateTime now = LocalDateTime.now(fixedClock);

		Loan activeLoan1 = persistLoan(book, reader1, now.minusDays(10), now.plusDays(4), null); // Active for reader1
		Loan activeLoan2 = persistLoan(book, reader2, now.minusDays(5), now.plusDays(9), null); // Active for reader2

		Loan returnedLoan1 = persistLoan(book, reader1, now.minusDays(15), now.minusDays(1), now.minusDays(2)); // Returned for reader1
		Loan returnedLoan2 = persistLoan(book, reader2, now.minusDays(20), now.minusDays(6), now.minusDays(5)); // Returned for reader2

		entityManager.flush();

		// WHEN
		Page<Loan> activeLoansReader1 = loanRepository.findAllByReturnDateIsNullAndReaderId(reader1.getId(), Pageable.ofSize(10));
		Page<Loan> activeLoansReader2 = loanRepository.findAllByReturnDateIsNullAndReaderId(reader2.getId(), Pageable.ofSize(10));

		// THEN
		assertEquals(1, activeLoansReader1.getTotalElements());
		assertEquals(activeLoan1.getId(), activeLoansReader1.getContent().getFirst().getId());

		assertEquals(1, activeLoansReader2.getTotalElements());
		assertEquals(activeLoan2.getId(), activeLoansReader2.getContent().getFirst().getId());
	}

	private Author persistAuthor(String name) {
		Author author = new Author(null, name, "Bio", null);
		return entityManager.persist(author);
	}

	private Book persistBook(String title, String isbn, Author author) {
		Book book = new Book(null, title, isbn, author, true);
		return entityManager.persist(book);
	}

	private Reader persistReader(String name, String email) {
		Reader reader = new Reader();
		reader.setFullName(name);
		reader.setEmail(email);
		return entityManager.persist(reader);
	}

	private Loan persistLoan(Book b, Reader r, LocalDateTime loanDate, LocalDateTime dueDate, LocalDateTime returnDate) {
		Loan loan = new Loan();
		loan.setBook(b);
		loan.setReader(r);
		loan.setLoanDate(loanDate);
		loan.setDueDate(dueDate);
		loan.setReturnDate(returnDate);
		return entityManager.persist(loan);
	}
}
